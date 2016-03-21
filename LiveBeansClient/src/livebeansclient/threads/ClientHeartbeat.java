/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package livebeansclient.threads;

import java.rmi.RemoteException;
import javax.swing.JOptionPane;
import livebeansclient.LiveBeansClient;

/**
 *
 * @author Luke Dawkes
 */
public class ClientHeartbeat implements Runnable {

    private int _errors = 0;
    private final int _maxErrors = 2;

    @Override
    public void run() {

        LiveBeansClient clientInstance = (LiveBeansClient) LiveBeansClient.getInstance();
        try {

            clientInstance.getServer().sendHeartbeat(clientInstance.getID());

            _errors = 0;
        } catch (RemoteException ex) {
            System.out.println("[CLIENT-WARNING] Failed to update server heartbeat");

            if (++_errors >= _maxErrors) {
                clientInstance.disconnectFromServer();
                clientInstance.displayDialog("Lost Connection", "Connection to server has been lost, your code is no longer being synchronised", JOptionPane.ERROR_MESSAGE);
            }

            // TODO: Warn client about failure to update server with client status
        }
    }

}
