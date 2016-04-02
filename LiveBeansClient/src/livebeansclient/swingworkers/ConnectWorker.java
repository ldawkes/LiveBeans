/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package livebeansclient.swingworkers;

import java.awt.Color;
import java.rmi.RemoteException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import livebeansclient.LiveBeansClient;
import livebeansclient.gui.ConnectionDialog;

/**
 *
 * @author Luke Dawkes
 */
public class ConnectWorker extends SwingWorker
{

    private final LiveBeansClient _clientInstance;
    private final ConnectionDialog _connectionDialog;
    private final String _clientName, _serverIP;

    public ConnectWorker(String clientName, String serverIP)
    {
        this._clientName = clientName;
        this._serverIP = serverIP;

        this._clientInstance = (LiveBeansClient) LiveBeansClient.getInstance();
        this._connectionDialog = ConnectionDialog.getInstance();
    }

    @Override
    protected Object doInBackground() throws Exception
    {
        try
        {
            _clientInstance.setName(_clientName.trim());
            _clientInstance.connectToServer(_serverIP.trim());

            return true;
        }
        catch (RemoteException ex)
        {
            System.out.println("[CLIENT-WARNING] Error Contacting Server:\r\n " + ex.getMessage());
        }

        return false;
    }

    @Override
    protected void done()
    {
        if (_clientInstance.isConnected())
        {
            _connectionDialog.setVisible(false);
            _connectionDialog.dispose();

            _clientInstance.displayDialog("Connected", "Successfully connected to server", JOptionPane.INFORMATION_MESSAGE);
            _clientInstance.postConnect();
        }
        else
        {
            _clientInstance.displayDialog("Failed to connect", "There was an error contacting the server (are you connected to a network?)", JOptionPane.ERROR_MESSAGE);
            _connectionDialog.setInfo("Not Connected", Color.BLACK);
            _connectionDialog.setClickables(true);
        }
    }

}
