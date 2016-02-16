/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package livebeansclient;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import livebeanscommon.ILiveBeansClient;
import livebeanscommon.ILiveBeansServer;
import org.openide.util.Exceptions;

/**
 *
 * @author ooddl
 */
public class LiveBeansClient extends UnicastRemoteObject implements Remote, Serializable, ILiveBeansClient
{

    private int _clientID;
    private String _clientName;
    private final String _ipAddressRegex;
    private final Pattern _ipAddressRegexPattern;
    private ILiveBeansServer _currentServer;

    private static LiveBeansClient _instance;

    public static ILiveBeansClient GetInstance() throws RemoteException
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
    }

    @Override
    public void SetID(int newID) throws RemoteException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void SetName(String newName) throws RemoteException
    {
        _clientName = newName;
    }

    @Override
    public void ConnectToServer(String serverAddress) throws RemoteException
    {
        Matcher regexMatcher = _ipAddressRegexPattern.matcher(serverAddress);

        if (regexMatcher.matches())
        {
            System.out.println(String.format("IP Address (%s) matches regex pattern", serverAddress));
        }
        
        try {            
            Registry reg = LocateRegistry.getRegistry(serverAddress);

            _currentServer = (ILiveBeansServer) Naming.lookup("rmi://" + serverAddress + "/LiveBeansServer");
            _currentServer.RegisterClient(this);
            
            System.out.println("Found Server.");
        } catch (NotBoundException | MalformedURLException ex) {
            System.out.println(ex.getMessage());
        }
        
        System.out.println(String.format("Current server is %s", _currentServer == null ? "null" : "not null"));
    }

    @Override
    public void DisconnectFromServer() throws RemoteException
    {
        try
        {
            _currentServer.UnRegisterClient(this);
        } catch (RemoteException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public int GetID() throws RemoteException
    {
        return _clientID;
    }

    @Override
    public String GetName() throws RemoteException
    {
        return _clientName;
    }

    @Override
    public ILiveBeansServer GetServer() throws RemoteException
    {
        return _currentServer;
    }
}
