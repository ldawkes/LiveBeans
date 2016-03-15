/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package livebeansserver;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import livebeanscommon.ILiveBeansClient;
import livebeanscommon.ILiveBeansCodeSegment;
import livebeanscommon.ILiveBeansServer;

/**
 *
 * @author Luke Dawkes
 */
public class LiveBeansServer extends UnicastRemoteObject implements ILiveBeansServer, Remote
{

    private static LiveBeansServer _instance;

    /**
     *
     * @return @throws RemoteException
     */
    public static LiveBeansServer getInstance() throws RemoteException
    {
        if (_instance == null)
        {
            _instance = new LiveBeansServer();
        }

        return _instance;
    }

    /**
     * @param args The command line arguments
     * @throws java.rmi.RemoteException
     * @throws java.net.MalformedURLException
     * @throws java.net.UnknownHostException
     */
    public static void main(String[] args) throws RemoteException, MalformedURLException, UnknownHostException
    {
        try
        {
            if (System.getSecurityManager() == null)
            {
                System.setProperty("java.security.policy", "src/livebeansserver/security/server.policy");
                System.setSecurityManager(new SecurityManager());
            }

            InetAddress localHost = InetAddress.getLocalHost();
            String ipAddress = localHost.getHostAddress();
            System.out.println(String.format("[SERVER-SETUP] Using LocalHost: %s\r\n[SERVER-SETUP] Using Host Address (%s)", localHost.toString(), ipAddress));

            Registry registry = LocateRegistry.createRegistry(1099);

            Naming.rebind("LiveBeansServer", getInstance());
            System.out.println("[SERVER-SETUP] LiveBeansServer bound to host address");
        } catch (RemoteException ex)
        {
            System.out.println("[SERVER-ERROR] There was a problem setting up the server.\r\nError: " + ex.getMessage());
        }
    }

    private final HashMap<Integer, ILiveBeansClient> _connectedClients;
    private final HashMap<Integer, Long> _clientHeartbeats;
    private ScheduledExecutorService _scheduler;

    private LiveBeansServer() throws RemoteException
    {
        _connectedClients = new HashMap<>();
        _clientHeartbeats = new HashMap<>();

        _scheduler = Executors.newScheduledThreadPool(1);
        _scheduler.scheduleAtFixedRate(ClientChecker.getInstance(), 1, 5, TimeUnit.SECONDS);

        if (System.getSecurityManager() == null)
        {
            System.setProperty("java.security.policy", "security/server.policy");
            System.setSecurityManager(new SecurityManager());
        }
    }

    /**
     * Gets a HashMap of client heartbeats on the server
     *
     * @return HashMap<Integer, Long>
     * @see HashMap
     */
    public HashMap<Integer, Long> getClientHeartbeats()
    {
        return _clientHeartbeats;
    }

    /**
     * Registers the given client on the server
     *
     * @param client The client interface to register on the server
     * @return Returns true if successful registration, false if otherwise
     * @throws RemoteException
     */
    @Override
    public boolean registerClient(ILiveBeansClient client) throws RemoteException
    {
        if (!_connectedClients.containsValue(client))
        {
            int newClientID = generateUniqueID();

            client.setID(newClientID);
            _connectedClients.put(newClientID, client);

            System.out.println(String.format("[SERVER-LOG] Client %s(%d) connected to server", client.getName(), client.getID()));
            return true;
        } else
        {
            return false;
        }
    }

    /**
     * Unregisters a client with the server
     *
     * @param client The client interface to remove from the server
     * @return Returns true if successful, false if otherwise
     * @throws RemoteException
     */
    @Override
    public boolean unRegisterClient(ILiveBeansClient client) throws RemoteException
    {
        if (_connectedClients.containsValue(client))
        {
            _clientHeartbeats.remove(client.getID());
            _connectedClients.remove(client.getID());

            System.out.println("[SERVER-LOG] Client disconnected from server");
            return true;
        } else
        {
            System.out.println("[SERVER-WARNING] Attempted to remove a non-existent client");
            return false;
        }
    }

    /**
     * Unregisters a client with the server
     *
     * @param clientID The ID of the client to unregister
     * @return Returns true if successful, false if otherwise
     * @throws RemoteException
     */
    public boolean unRegisterClient(Integer clientID) throws RemoteException
    {
        ILiveBeansClient client = getClientByID(clientID);

        if (client == null)
        {
            return false;
        }

        if (_connectedClients.containsValue(client))
        {
            _clientHeartbeats.remove(clientID);
            _connectedClients.remove(clientID);
            System.out.println("[SERVER-LOG] Client disconnected from server");
            return true;
        } else
        {
            System.out.println("[SERVER-WARNING] Attempted to remove a non-existent client with ID " + clientID);
            return false;
        }
    }

    /**
     * Updates a client heartbeat on the server to let the server know they are
     * still connected
     *
     * @param clientID The clientID with which to update a heartbeat
     * @throws RemoteException
     */
    @Override
    public void sendHeartbeat(int clientID) throws RemoteException
    {
        for (Integer storedID : _clientHeartbeats.keySet())
        {
            if (storedID == clientID)
            {
                _clientHeartbeats.put(storedID, System.nanoTime());
                return;
            }
        }

        _clientHeartbeats.put(clientID, System.nanoTime());

        System.out.println(String.format("[SERVER-LOG] Created a new client heartbeat entry for %s", getClientByID(clientID).getName()));
    }

    private ILiveBeansClient getClientByID(int clientID) throws RemoteException
    {
        for (HashMap.Entry<Integer, ILiveBeansClient> client : _connectedClients.entrySet())
        {
            // Could attempt to contact client first to ask for client ID
            // but that would risk throwing RemoteException errors, and
            // I'd prefer to keep the chance of throwing them at a minimum
            if (client.getKey() == clientID)
            {
                return client.getValue();
            }
        }

        System.out.println(String.format("[SERVER-WARNING] Attempted to get a client by invalid ID(%s)", clientID));

        return null;
    }

    private int generateUniqueID() throws RemoteException
    {
        Random randomGenerator = new Random();

        boolean foundNumber = false;
        int randNumber = 0;

        while (!foundNumber)
        {
            // Using Integer.SIZE guarantees a positive number is generated
            // rather than have some clients given a negative number
            randNumber = randomGenerator.nextInt(Integer.SIZE - 1);

            System.out.println(randNumber);

            if (_connectedClients.isEmpty())
            {
                foundNumber = true;
            } else
            {
                for (HashMap.Entry<Integer, ILiveBeansClient> client : _connectedClients.entrySet())
                {
                    try
                    {
                        if (client.getValue().getID() == randNumber)
                        {
                            continue;
                        }

                        foundNumber = true;
                    } catch (RemoteException ex)
                    {
                        System.out.println("[SERVER-WARNING] Found a non-responsive client");
                    }
                }
            }
        }

        return randNumber;
    }

    /**
     * Tells the server to distribute the code segments between all clients
     *
     * @param codeSegments The list of code segments that will be sent to the
     * server
     * @param clientID The author of the code segments
     * @throws RemoteException
     */
    @Override
    public void distributeCodeSegments(List<? extends ILiveBeansCodeSegment> codeSegments, int clientID) throws RemoteException
    {
        System.out.println(String.format("[SERVER-INFO] Received %d code segment(s) from client %d", codeSegments.size(), clientID));

        _connectedClients.entrySet().stream().filter(client -> client.getKey() != clientID).forEach((client)
                ->
                {
                    try
                    {
                        client.getValue().updateLocalCode(codeSegments);
                    } catch (RemoteException ex)
                    {
                        System.out.println("[SERVER-WARNING] Found a non-responsive client");
                    }
        });
    }
}
