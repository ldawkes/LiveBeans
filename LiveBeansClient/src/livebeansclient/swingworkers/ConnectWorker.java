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
            System.out.println("[CLIENT-WARNING] Error Contacting Server:\r\n " + ex);
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
