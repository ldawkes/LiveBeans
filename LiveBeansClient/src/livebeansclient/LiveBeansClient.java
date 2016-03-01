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
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import livebeanscommon.ILiveBeansClient;
import livebeanscommon.ILiveBeansCodeSegment;
import livebeanscommon.ILiveBeansServer;

/**
 *
 * @author Luke Dawkes
 */
public class LiveBeansClient extends UnicastRemoteObject implements Serializable, ILiveBeansClient
{

    private int _clientID;
    private String _clientName;
    private final String _ipAddressRegex;
    private final Pattern _ipAddressRegexPattern;
    private ILiveBeansServer _currentServer;

    private final ScheduledExecutorService _scheduler;
    private ScheduledFuture _heartbeatSchedule, _codeSynchroniseSchedule;

    private List<CodeSegment> _segmentBacklog;

    private static LiveBeansClient _instance;

    public static ILiveBeansClient getInstance() throws RemoteException
    {
        if (_instance == null)
        {
            _instance = new LiveBeansClient();
        }

        return _instance;
    }

    private LiveBeansClient() throws RemoteException
    {
        _ipAddressRegex = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
        _ipAddressRegexPattern = Pattern.compile(_ipAddressRegex);

        _scheduler = Executors.newScheduledThreadPool(2);
    }

    public void addSegmentToBacklog(String code, int codeOffset) throws RemoteException
    {
        CodeSegment codeSegment = new CodeSegment();
        codeSegment.setAuthorID(_clientID);
        codeSegment.setCodeText(code);
        codeSegment.setDocumentOffset(codeOffset);

        _segmentBacklog.add(codeSegment);
    }

    public void addSegmentToBacklog(int codeOffset, int codeLength) throws RemoteException
    {
        CodeSegment codeSegment = new CodeSegment();
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
            System.out.println(String.format("IP Address (%s) matches regex pattern", serverAddress));
        }

        try
        {
            Registry reg = LocateRegistry.getRegistry(serverAddress);

            _currentServer = (ILiveBeansServer) Naming.lookup("rmi://" + serverAddress + "/LiveBeansServer");
            _currentServer.registerClient(this);

            _heartbeatSchedule = _scheduler.scheduleAtFixedRate(new ClientHeartbeat(), 2, 2, TimeUnit.SECONDS);
            _codeSynchroniseSchedule = _scheduler.scheduleAtFixedRate(new CodeSegmentSynchroniser(), 1, 2, TimeUnit.SECONDS);

            System.out.println("Found Server.");
        } catch (NotBoundException | MalformedURLException ex)
        {
            System.out.println(ex.getMessage());
            return;
        }

        System.out.println(String.format("Current server is %s", _currentServer == null ? "null" : "not null"));

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
    public void updateLocalCode(ILiveBeansCodeSegment[] codeSegments) throws RemoteException
    {
        System.out.println(String.format("[CLIENT-LOG] Received collection of %s code segments", codeSegments.length));

        for (CodeSegment codeSegment : (CodeSegment[]) codeSegments)
        {
            System.out.println("[CLIENT-LOG] Code segment contains: " + codeSegment.getCodeText());
        }
    }

    @Override
    public void updateRemoteCode() throws RemoteException
    {
        if (_segmentBacklog.isEmpty())
        {
            return;
        }

        try
        {
            _currentServer.distributeCodeSegments((CodeSegment[]) _segmentBacklog.toArray(), _clientID);
            _segmentBacklog.clear();

        } catch (RemoteException ex)
        {
            System.out.println("[CLIENT-WARNING] There was an error synchronising the code segments");
        }
    }
}
