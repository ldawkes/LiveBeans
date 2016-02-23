/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package livebeansclient;

import java.rmi.RemoteException;
import java.util.Arrays;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/**
 *
 * @author ooddl
 */
public class TabListener implements DocumentListener
{

    private static TabListener _instance;
    private LiveBeansClient _currentClient;
    private Document _currentDocument;

    private TabListener() throws RemoteException
    {
        _currentClient = (LiveBeansClient) LiveBeansClient.getInstance();
    }

    public static TabListener getInstance() throws RemoteException
    {
        if (_instance == null)
        {
            _instance = new TabListener();
        }

        return _instance;
    }

    public void setCurrentDocument(Document newDocument)
    {
        _currentDocument = newDocument;
    }

    @Override
    public void insertUpdate(DocumentEvent e)
    {
        System.out.println("Inserted Into Document: " + Arrays.toString(e.getDocument().getRootElements()));
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
        System.out.println("Removed From Document: " + Arrays.toString(e.getDocument().getRootElements()));
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
        System.out.println("Changed In Document: " + Arrays.toString(e.getDocument().getRootElements()));
    }
}
