/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package livebeansclient;

import java.rmi.RemoteException;

/**
 *
 * @author ooddl
 */
public class ClientHeartbeat implements Runnable
{

    @Override
    public void run()
    {
        try
        {
            LiveBeansClient clientInstance = (LiveBeansClient) LiveBeansClient.getInstance();

            clientInstance.getServer().sendHeartbeat(clientInstance.getID());
        } catch (RemoteException ex)
        {
            System.out.println("[CLIENT-WARNING] Failed to update server heartbeat");

            // TODO: Warn client about failure to update server with client status
        }
    }

}