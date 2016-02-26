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
 * @author ooddl
 */
class ClientChecker extends Thread
{

    private long _startTime, _estimatedTime;
    private int _checkTime = 5;

    private static ClientChecker _instance;

    private ClientChecker()
    {
    }

    public static ClientChecker getInstance()
    {
        if (_instance == null)
        {
            _instance = new ClientChecker();
        }

        return _instance;
    }

    @Override
    public void run()
    {
        while (true)
        {
            _estimatedTime = (System.nanoTime() - _startTime) / 1000000000;

            if (_estimatedTime > _checkTime)
            {
                System.out.println("[SERVER-LOG] Checking for disconnected clients...");

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
                } catch (RemoteException ex)
                {
                    System.out.println("[SERVER-ERROR] There was a problem updating the client list:\r\n\r\n" + ex.toString());
                }

                updateTime();
            }
        }
    }

    private void updateTime()
    {
        _startTime = System.nanoTime();
    }
}
