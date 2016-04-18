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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import livebeanscommon.ILiveBeansClient;
import livebeanscommon.ILiveBeansCodeSegment;
import livebeanscommon.IServerWatcher;
import livebeansserver.util.ServerConstants.ServerStatus;
import static org.hamcrest.CoreMatchers.instanceOf;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 * @author Luke Dawkes
 */
public class LiveBeansServerTest
{

    private LiveBeansServer instance;

    public LiveBeansServerTest()
    {
        this._port = 1500;
    }

    private final Integer _port;

    @Before
    public void setUp()
    {
        instance = LiveBeansServer.getInstance();
        instance.serverInit(_port);
    }

    @After
    public void tearDown()
    {
    }

    private void rehostServer()
    {
        instance.closeServer();
        instance.serverInit(_port);
    }

    /**
     * Test of getInstance method, of class LiveBeansServer.
     */
    @Test
    public void testGetInstance()
    {
        System.out.println("\r\ngetInstance");

        LiveBeansServer result = LiveBeansServer.getInstance();

        assertNotNull(result);
        assertThat(result, instanceOf(LiveBeansServer.class));
    }

    /**
     * Test of serverInit method, of class LiveBeansServer.
     */
    @Test
    public void testServerInit()
    {
        System.out.println("\r\nserverInit");

        instance.serverInit(_port);

        ServerStatus result = instance.getCurrentStatus();

        assertNotNull(result);
        assertThat(result, instanceOf(ServerStatus.class));
        assertEquals(ServerStatus.ONLINE, result);
    }

    /**
     * Test of addWatcher method, of class LiveBeansServer.
     */
    @Test
    public void testAddWatcher()
    {
        System.out.println("\r\naddWatcher");

        IServerWatcher newWatcher = Mockito.mock(IServerWatcher.class);
        instance.addWatcher(newWatcher);
    }

    /**
     * Test of getClientHeartbeats method, of class LiveBeansServer.
     *
     * @throws java.rmi.RemoteException
     */
    @Test
    public void testGetClientHeartbeats() throws RemoteException
    {
        System.out.println("\r\ngetClientHeartbeats");

        rehostServer();

        // Return initial heartbeats
        HashMap<Integer, Long> result = instance.getClientHeartbeats();

        assertTrue(result.isEmpty());

        // Mock a client to register
        ILiveBeansClient mockClient = Mockito.mock(ILiveBeansClient.class);
        Mockito.when(mockClient.getName()).thenReturn("Test");
        System.out.println(mockClient.getName());

        instance.registerClient(mockClient);

        // Grab the generated ID and make the mocked client return that ID
        HashMap<Integer, ILiveBeansClient> connectedClients;
        connectedClients = instance.getConectedClients();

        HashMap.Entry<Integer, ILiveBeansClient> entry;
        entry = connectedClients.entrySet().iterator().next();

        Mockito.when(mockClient.getID()).thenReturn(entry.getKey());

        // Send a heartbeat using that ID
        instance.sendHeartbeat(mockClient.getID());

        // Get new list of heartbeats
        result = instance.getClientHeartbeats();

        assertFalse(result.isEmpty());

    }

    /**
     * Test of registerClient method, of class LiveBeansServer.
     *
     * @throws java.rmi.RemoteException
     * @throws java.lang.Exception
     */
    @Test
    public void testRegisterClient() throws RemoteException
    {
        System.out.println("\r\nregisterClient");

        ILiveBeansClient mockClient = Mockito.mock(ILiveBeansClient.class);

        boolean expResult = true;
        boolean result = instance.registerClient(mockClient);

        assertEquals(expResult, result);

        expResult = false;
        result = instance.registerClient(mockClient);

        assertEquals(expResult, result);
    }

    /**
     * Test of unRegisterClient method, of class LiveBeansServer.
     *
     * @throws java.rmi.RemoteException
     * @throws java.lang.Exception
     */
    @Test
    public void testUnRegisterClient_ILiveBeansClient() throws RemoteException
    {
        System.out.println("\r\nunRegisterClient");

        // Mock and test a client that shouldn't be registered
        ILiveBeansClient mockClient = Mockito.mock(ILiveBeansClient.class);
        boolean result = instance.unRegisterClient(mockClient);

        assertFalse(result);

        // Register the mocked client, and expect the method to return true
        instance.registerClient(mockClient);
        result = instance.unRegisterClient(mockClient);

        assertTrue(result);
    }

    /**
     * Test of unRegisterClient method, of class LiveBeansServer.
     *
     * @throws java.rmi.RemoteException
     * @throws java.lang.Exception
     */
    @Test
    public void testUnRegisterClient_Integer() throws RemoteException
    {
        System.out.println("\r\nunRegisterClient");

        // Test against a non-existent client
        Integer clientID = 0;
        boolean result = instance.unRegisterClient(clientID);
        assertFalse(result);

        // Mock a client to register
        ILiveBeansClient mockClient = Mockito.mock(ILiveBeansClient.class);
        Mockito.when(mockClient.getName()).thenReturn("Test");
        System.out.println(mockClient.getName());

        instance.registerClient(mockClient);

        // Grab the generated ID and make the mocked client return that ID
        HashMap<Integer, ILiveBeansClient> connectedClients = instance.getConectedClients();
        HashMap.Entry<Integer, ILiveBeansClient> entry = connectedClients.entrySet().iterator().next();
        Mockito.when(mockClient.getID()).thenReturn(entry.getKey());

        // Test against a client that should exist
        result = instance.unRegisterClient(mockClient.getID());
        assertTrue(result);
    }

    /**
     * Test of sendHeartbeat method, of class LiveBeansServer.
     *
     * @throws java.rmi.RemoteException
     */
    @Test
    public void testSendHeartbeat() throws RemoteException
    {
        System.out.println("\r\nsendHeartbeat");

        // Mock a client to register
        ILiveBeansClient mockClient = Mockito.mock(ILiveBeansClient.class);
        Mockito.when(mockClient.getName()).thenReturn("Test");
        System.out.println(mockClient.getName());

        instance.registerClient(mockClient);

        // Grab the generated ID and make the mocked client return that ID
        HashMap<Integer, ILiveBeansClient> connectedClients = instance.getConectedClients();
        HashMap.Entry<Integer, ILiveBeansClient> entry = connectedClients.entrySet().iterator().next();
        Mockito.when(mockClient.getID()).thenReturn(entry.getKey());

        instance.sendHeartbeat(mockClient.getID());
    }

    /**
     * Test of distributeCodeSegments method, of class LiveBeansServer.
     *
     * @throws java.rmi.RemoteException
     */
    @Test
    public void testDistributeCodeSegments() throws RemoteException
    {
        System.out.println("\r\ndistributeCodeSegments");
        List<ILiveBeansCodeSegment> codeSegments = new ArrayList<>();
        codeSegments.add(Mockito.mock(ILiveBeansCodeSegment.class));

        // Mock a client to register
        ILiveBeansClient mockClient = Mockito.mock(ILiveBeansClient.class);
        Mockito.when(mockClient.getName()).thenReturn("Test");
        System.out.println(mockClient.getName());

        instance.registerClient(mockClient);

        // Grab the generated ID and make the mocked client return that ID
        HashMap<Integer, ILiveBeansClient> connectedClients = instance.getConectedClients();
        HashMap.Entry<Integer, ILiveBeansClient> entry = connectedClients.entrySet().iterator().next();
        Mockito.when(mockClient.getID()).thenReturn(entry.getKey());

        instance.distributeCodeSegments(codeSegments, mockClient.getID());
    }

    /**
     * Test of getCurrentStatus method, of class LiveBeansServer.
     */
    @Test
    public void testGetCurrentStatus()
    {
        System.out.println("\r\ngetCurrentStatus");

        instance.serverInit(_port);
        ServerStatus result = instance.getCurrentStatus();

        assertNotNull(result);
        assertThat(result, instanceOf(ServerStatus.class));
        assertEquals(ServerStatus.ONLINE, result);

        instance.closeServer();

        result = instance.getCurrentStatus();

        assertNotNull(result);
        assertThat(result, instanceOf(ServerStatus.class));
        assertEquals(ServerStatus.OFFLINE, result);
    }
}
