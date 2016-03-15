/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package livebeansclient.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Bugtracking",
        id = "livebeansclient.LiveBeansToolbarAction"
)
@ActionRegistration(
        iconBase = "livebeansclient/sprites/LiveBeans_Connect_16.png",
        displayName = "#CTL_LiveBeansToolbarAction"
)
@ActionReference(path = "Toolbars/File", position = 500)
@Messages("CTL_LiveBeansToolbarAction=Connect to LiveBeans server")
public final class LiveBeansToolbarAction implements ActionListener
{

    private ConnectionDialog _connectionDialog;

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (_connectionDialog == null)
        {
            _connectionDialog = new ConnectionDialog(new JFrame(), true);
            _connectionDialog.setLocationRelativeTo(null);
        }

        _connectionDialog.setVisible(true);
    }
}
