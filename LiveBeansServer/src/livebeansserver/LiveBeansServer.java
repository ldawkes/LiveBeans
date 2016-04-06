/*
 * The MIT License
 *
 * Copyright 2016 Luke Dawkes.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package livebeansserver;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import livebeanscommon.ILiveBeansClient;
import livebeanscommon.ILiveBeansCodeSegment;
import livebeanscommon.ILiveBeansServer;
import livebeanscommon.IServerWatcher;
import livebeansserver.util.ServerConstants.ServerStatus;

/**
 *
 * @author Luke Dawkes
 */
public class LiveBeansServer extends UnicastRemoteObject implements ILiveBeansServer, Remote
{

    private static LiveBeansServer _instance;

    /**
     * Gets the singleton instance of the server
     *
     * @return LiveBeansServer
     */
    public static LiveBeansServer getInstance()
    {
        if (_instance == null)
        {
            try
            {
                _instance = new LiveBeansServer();
            }
            catch (RemoteException ex)
            {
                System.out.println("[SERVER-ERROR] Failed to create server instance.\r\nError: " + ex.getMessage());
            }
        }

        return _instance;
    }

    private final ArrayList<IServerWatcher> _watchers;
    private final HashMap<Integer, ILiveBeansClient> _connectedClients;
    private final HashMap<Integer, Long> _clientHeartbeats;
    private final ScheduledExecutorService _scheduler;

    private ServerStatus _currentStatus;

    private LiveBeansServer() throws RemoteException
    {
        _watchers = new ArrayList<>();

        _connectedClients = new HashMap<>();
        _clientHeartbeats = new HashMap<>();

        _scheduler = Executors.newScheduledThreadPool(1);
    }

    public void serverInit(Integer port)
    {
        try
        {

            InetAddress localHost = InetAddress.getLocalHost();
            String ipAddress = localHost.getHostAddress();

            System.out.println(String.format("[SERVER-SETUP] Using LocalHost: %s\r\n[SERVER-SETUP] Using Host Address (%s)", localHost.toString(), ipAddress));

            Registry registry = LocateRegistry.createRegistry(port);

            registry.rebind("LiveBeansServer", getInstance());
            System.out.println("[SERVER-SETUP] LiveBeansServer bound to host address");
            _scheduler.scheduleAtFixedRate(ClientChecker.getInstance(), 1, 5, TimeUnit.SECONDS);

            _currentStatus = ServerStatus.ONLINE;
            notifyWatchers();

        }
        catch (RemoteException ex)
        {
            System.out.println("[SERVER-ERROR] There was a problem setting up the server.\r\n\tError: " + ex.getMessage());
            notifyError();
        }
        catch (UnknownHostException ex)
        {
            System.out.println("[SERVER-ERROR] There was a problem locating your localhost address.\r\n\tError: " + ex.getMessage());
            notifyError();
        }
        catch (AccessControlException ex)
        {
            System.out.println("[SERVER-ERROR] The server does not have required access.\r\n\tError: " + ex.getMessage());
        }
    }

    public void addWatcher(IServerWatcher newWatcher)
    {
        if (!_watchers.contains(newWatcher))
        {
            _watchers.add(newWatcher);
        }
    }

    private void notifyWatchers()
    {
        _watchers.stream().forEach((watcher)
                ->
                {
                    watcher.onServerStatusChange();
        });
    }

    private void notifyError()
    {
        _currentStatus = ServerStatus.ERROR;
        notifyWatchers();
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
        }
        else
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
        }
        else
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
        }
        else
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

            if (_connectedClients.isEmpty())
            {
                foundNumber = true;
            }
            else
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
                    }
                    catch (RemoteException ex)
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
     * @param authorID The author of the code segments
     * @throws RemoteException
     */
    @Override
    public void distributeCodeSegments(List<ILiveBeansCodeSegment> codeSegments, int authorID) throws RemoteException
    {
        System.out.println(String.format("[SERVER-INFO] Received %d code segment(s) from client %d", codeSegments.size(), authorID));

        //.filter(client -> client.getKey() != clientID)
        _connectedClients.entrySet().stream().filter(client -> client.getKey() != authorID).forEach((client)
                ->
                {
                    try
                    {
                        client.getValue().updateLocalCode(codeSegments);
                    }
                    catch (RemoteException ex)
                    {
                        System.out.println("[SERVER-WARNING] Found a non-responsive client");
                    }
        });
    }

    public ServerStatus getCurrentStatus()
    {
        return _currentStatus;
    }
}
