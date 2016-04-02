/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package livebeansclient.threads;

import java.rmi.RemoteException;
import livebeansclient.LiveBeansClient;

/**
 *
 * @author Luke Dawkes
 */
public class CodeSegmentSynchroniser implements Runnable
{

    @Override
    public void run()
    {
        try
        {
            LiveBeansClient.getInstance().updateRemoteCode();
        }
        catch (RemoteException ex)
        {
            System.out.println("[CLIENT-WARNING] Failed to call updateRemoteCode\r\n" + ex);
        }
    }

}
