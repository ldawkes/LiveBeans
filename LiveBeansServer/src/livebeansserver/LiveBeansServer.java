/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package livebeansserver;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import livebeanscommon.ILiveBeansClient;
import livebeanscommon.ILiveBeansServer;

/**
 *
 * @author ooddl
 */
public class LiveBeansServer extends UnicastRemoteObject implements ILiveBeansServer, Remote
{

    private ArrayList<ILiveBeansClient> _connectedClients;

    private LiveBeansServer() throws RemoteException
    {
        _connectedClients = new ArrayList<>();
    }

    /**
     * @param args the command line arguments
     * @throws java.rmi.RemoteException
     * @throws java.net.MalformedURLException
     * @throws java.net.UnknownHostException
     */
    public static void main(String[] args) throws RemoteException, MalformedURLException, UnknownHostException
    {
        try {            
            InetAddress localHost = InetAddress.getLocalHost();
            String ipAddress = localHost.getHostAddress();
            System.out.println(String.format("Using LocalHost: %s\r\nUsing Host Address (%s)", localHost.toString(), ipAddress));
            
            Registry registry = LocateRegistry.createRegistry(1099);
            LiveBeansServer serverInstance = new LiveBeansServer();
            
            Naming.rebind("LiveBeansServer", serverInstance);
            System.out.println("LiveBeansServer bound to host address");
        }
        catch (RemoteException ex) {
            System.out.println("There was a problem setting up the server.\r\nError: " + ex.getMessage());
        }
    }

    @Override
    public boolean RegisterClient(ILiveBeansClient client) throws RemoteException
    {
        if (!_connectedClients.contains(client))
        {
            _connectedClients.add(client);
            System.out.println(String.format("Client %s connected to server", client.GetName()));
            return true;
        } else
        {
            return false;
        }
    }

    @Override
    public boolean UnRegisterClient(ILiveBeansClient client) throws RemoteException
    {
        if (_connectedClients.contains(client))
        {
            _connectedClients.remove(client);
            System.out.println(String.format("Client %s disconnected from server", client.GetName()));
            return true;
        }
        else
        {
            return false;
        }
    }

}
