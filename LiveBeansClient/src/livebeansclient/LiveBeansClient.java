/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package livebeansclient;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import livebeansclient.gui.TabListenerHandler;
import livebeansclient.threads.ClientHeartbeat;
import livebeansclient.threads.CodeSegmentSynchroniser;
import livebeanscommon.ILiveBeansClient;
import livebeanscommon.ILiveBeansCodeSegment;
import livebeanscommon.ILiveBeansServer;

/**
 *
 * @author Luke Dawkes
 */
public class LiveBeansClient extends UnicastRemoteObject implements Serializable, ILiveBeansClient
{

    private static LiveBeansClient _instance;

    public static ILiveBeansClient getInstance() throws RemoteException
    {
        if (_instance == null)
        {
            _instance = new LiveBeansClient();
        }

        return _instance;
    }

    private int _clientID;
    private String _clientName;
    private final String _ipAddressRegex;
    private final Pattern _ipAddressRegexPattern;
    private ILiveBeansServer _currentServer;

    private final ScheduledExecutorService _scheduler;
    private ScheduledFuture _heartbeatSchedule, _codeSynchroniseSchedule;

    private List<ILiveBeansCodeSegment> _segmentBacklog;

    private LiveBeansClient() throws RemoteException
    {
        _ipAddressRegex = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
        _ipAddressRegexPattern = Pattern.compile(_ipAddressRegex);

        _scheduler = Executors.newScheduledThreadPool(2);
        _segmentBacklog = new ArrayList<>();

        if (System.getSecurityManager() == null)
        {
            System.setProperty("java.security.policy", "security/client.policy");
            System.setSecurityManager(new SecurityManager());
        }
    }

    /* Code Segment Methods */
    /**
     * Adds an addition segment to the backlog
     *
     * @param documentName The name of the document the code is in
     * @param projectName The name of the project the document is in
     * @param code The code to be updated
     * @param codeOffset The offset of the updated code within the document
     * @throws RemoteException
     */
    public void addSegmentToBacklog(String documentName, String projectName, String code, int codeOffset) throws RemoteException
    {
        CodeSegment codeSegment = new CodeSegment();
        codeSegment.setAuthorID(_clientID);
        codeSegment.setDocumentName(documentName);
        codeSegment.setProject(projectName);
        codeSegment.setCodeText(code);
        codeSegment.setDocumentOffset(codeOffset);

        _segmentBacklog.add(codeSegment);
    }

    /**
     * Adds a removal segment to the backlog
     *
     * @param documentName The name of the document the code is in
     * @param projectName The name of the project the document is in
     * @param codeOffset The offset of the updated code within the document
     * @param codeLength The length of the updated code
     * @throws RemoteException
     */
    public void addSegmentToBacklog(String documentName, String projectName, int codeOffset, int codeLength) throws RemoteException
    {
        CodeSegment codeSegment = new CodeSegment();
        codeSegment.setAuthorID(_clientID);
        codeSegment.setDocumentName(documentName);
        codeSegment.setProject(projectName);
        codeSegment.setDocumentOffset(codeOffset);
        codeSegment.setCodeLength(codeLength);

        _segmentBacklog.add(codeSegment);
    }

    /**
     * Adds an addition segment to the backlog
     *
     * @param documentName The name of the document the code is in
     * @param code The code to be updated
     * @param codeOffset The offset of the code within the document
     * @throws RemoteException
     */
    public void addSegmentToBacklog(String documentName, String code, int codeOffset) throws RemoteException
    {
        CodeSegment codeSegment = new CodeSegment();
        codeSegment.setAuthorID(_clientID);
        codeSegment.setDocumentName(documentName);
        codeSegment.setCodeText(code);
        codeSegment.setDocumentOffset(codeOffset);

        _segmentBacklog.add(codeSegment);
    }

    /**
     * Adds a removal segment to the backlog
     *
     * @param documentName The name of the document the code is in
     * @param codeOffset The offset of the code within the document
     * @param codeLength The length of the updated code
     * @throws RemoteException
     */
    public void addSegmentToBacklog(String documentName, int codeOffset, int codeLength) throws RemoteException
    {
        CodeSegment codeSegment = new CodeSegment();
        codeSegment.setDocumentName(documentName);
        codeSegment.setAuthorID(_clientID);
        codeSegment.setDocumentOffset(codeOffset);
        codeSegment.setCodeLength(codeLength);

        _segmentBacklog.add(codeSegment);
    }

    @Override
    public void setID(int newID) throws RemoteException
    {
        _clientID = newID;
    }

    @Override
    public void setName(String newName) throws RemoteException
    {
        _clientName = newName;
    }

    @Override
    public void connectToServer(String serverAddress) throws RemoteException
    {
        Matcher regexMatcher = _ipAddressRegexPattern.matcher(serverAddress);

        if (regexMatcher.matches())
        {
            System.out.println(String.format("[CLIENT-INFO] IP Address (%s) matches regex pattern", serverAddress));
        }

        try
        {
            Registry reg = LocateRegistry.getRegistry(serverAddress);

            _currentServer = (ILiveBeansServer) Naming.lookup("rmi://" + serverAddress + "/LiveBeansServer");
            _currentServer.registerClient(this);

            _heartbeatSchedule = _scheduler.scheduleAtFixedRate(new ClientHeartbeat(), 2, 2, TimeUnit.SECONDS);
            _codeSynchroniseSchedule = _scheduler.scheduleAtFixedRate(new CodeSegmentSynchroniser(), 2, 2, TimeUnit.SECONDS);

            System.out.println("[CLIENT-INFO] Found Server.");
        } catch (NotBoundException | MalformedURLException ex)
        {
            System.out.println(ex.getMessage());
            return;
        }

        System.out.println(String.format("[CLIENT-INFO] Current server is %s", _currentServer == null ? "null" : "not null"));

        TabListenerHandler.GetInstance().setUpListeners();
    }

    @Override
    public void disconnectFromServer() throws RemoteException
    {
        try
        {
            _currentServer.unRegisterClient(this);

            _scheduler.shutdown();
        } catch (RemoteException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public int getID() throws RemoteException
    {
        return _clientID;
    }

    @Override
    public String getName() throws RemoteException
    {
        return _clientName;
    }

    @Override
    public ILiveBeansServer getServer() throws RemoteException
    {
        return _currentServer;
    }

    @Override
    public void updateLocalCode(List<? extends ILiveBeansCodeSegment> codeSegments) throws RemoteException
    {
        System.out.println(String.format("[CLIENT-LOG] Received collection of %d code segments:", codeSegments.size()));

        for (ILiveBeansCodeSegment codeSegment : codeSegments)
        {
            System.out.println(String.format("\t[CLIENT-LOG] Code segment contains: %s", codeSegment.getCodeText()));
        }
    }

    @Override
    public void updateRemoteCode() throws RemoteException
    {
        if (_segmentBacklog.isEmpty())
        {
            System.out.println("[CLIENT-INFO] Attempted to synchronise an empty code backlog");
            return;
        }

        System.out.println("[CLIENT-INFO] Synchronising...");

        try
        {
            _currentServer.distributeCodeSegments(_segmentBacklog, _clientID);

            System.out.println(String.format("[CLIENT-INFO] Synchronised %d code segment(s)", _segmentBacklog.size()));

            _segmentBacklog.clear();

        } catch (RemoteException ex)
        {
            System.out.println("[CLIENT-WARNING] There was an error synchronising the code segments\r\n" + ex);
        }
    }
}
