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
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 *
 * @author Luke Dawkes
 */
public class TabListener implements DocumentListener
{

    private static TabListener _instance;
    private final LiveBeansClient _currentClient;
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
    public synchronized void insertUpdate(DocumentEvent e)
    {
        try
        {
            String text = _currentDocument.getText(e.getOffset(), e.getLength());

            System.out.println("[CLIENT-INFO] Inserted Text: " + text);
        } catch (BadLocationException ex)
        {
            System.out.println("[CLIENT-WARNING] Attemped to grab text from invalid point in document");
        }
    }

    @Override
    public synchronized void removeUpdate(DocumentEvent e)
    {
        System.out.println(String.format("[CLIENT-INFO] Text Removed: (Offset: %s) (Length: %s)", e.getOffset(), e.getLength()));
    }

    /*
        I've tested various circumstances where this method should logically fire,
        however it does not;
            Replacing a section of text by copy+pasting instead causes
            a removeUpdate event, followed by an insertUpdate event
     */
    @Override
    public synchronized void changedUpdate(DocumentEvent e)
    {
        System.out.println("Changed In Document: " + Arrays.toString(e.getDocument().getRootElements()));
    }
}
