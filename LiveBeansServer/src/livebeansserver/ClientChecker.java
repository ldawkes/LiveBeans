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
import java.util.HashMap;

/**
 *
 * @author Luke Dawkes
 */
class ClientChecker implements Runnable
{

    private static ClientChecker _instance;

    public static ClientChecker getInstance()
    {
        if (_instance == null)
        {
            _instance = new ClientChecker();
        }

        return _instance;
    }
    private final int _checkTime = 5;

    private ClientChecker()
    {
    }

    @Override
    public void run()
    {
        try
        {
            LiveBeansServer serverInstance = LiveBeansServer.getInstance();
            HashMap<Integer, Long> clientHeartbeats
                                   = serverInstance.getClientHeartbeats();

            for (HashMap.Entry<Integer, Long> heartbeat
                 : clientHeartbeats.entrySet())
            {
                if ((System.nanoTime() - heartbeat.getValue())
                    / 1000000000 > _checkTime)
                {
                    System.out.println("[SERVER-LOG] Found a disconnected/crashed"
                                       + " client, removing...");

                    serverInstance.unRegisterClient(heartbeat.getKey());
                }
            }
        }
        catch (RemoteException ex)
        {
            System.out.println("[SERVER-ERROR] There was a problem updating the client list:\r\n\r\n" + ex.toString());
        }
    }
}
