/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
            HashMap<Integer, Long> clientHeartbeats = LiveBeansServer.getInstance().getClientHeartbeats();

            for (HashMap.Entry<Integer, Long> heartbeat : clientHeartbeats.entrySet())
            {
                if ((System.nanoTime() - heartbeat.getValue()) / 1000000000 > _checkTime)
                {
                    System.out.println("[SERVER-LOG] Found a disconnected/crashed client, removing...");

                    LiveBeansServer.getInstance().unRegisterClient(heartbeat.getKey());
                }
            }
        }
        catch (RemoteException ex)
        {
            System.out.println("[SERVER-ERROR] There was a problem updating the client list:\r\n\r\n" + ex.toString());
        }
    }
}
