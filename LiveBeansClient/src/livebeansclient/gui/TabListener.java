/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package livebeansclient.gui;

import java.rmi.RemoteException;
import java.util.Arrays;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import livebeansclient.LiveBeansClient;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;

/**
 *
 * @author Luke Dawkes
 */
public class TabListener implements DocumentListener
{

    private static TabListener _instance;

    public static TabListener getInstance() throws RemoteException
    {
        if (_instance == null)
        {
            _instance = new TabListener();
        }

        return _instance;
    }
    private final LiveBeansClient _currentClient;
    private Document _currentDocument;
    private Project _currentProject;
    private ProjectInformation _currentProjectInformation;
    private String _currentDocumentName;

    private TabListener() throws RemoteException
    {
        _currentClient = (LiveBeansClient) LiveBeansClient.getInstance();
    }

    public void setCurrentDocument(Document newDocument)
    {
        _currentDocument = newDocument;
    }

    public void setCurrentDocumentName(String newDocumentName)
    {
        _currentDocumentName = newDocumentName;
    }

    public void setCurrentProject(Project newProject)
    {
        _currentProject = newProject;
        _currentProjectInformation = _currentProject.getLookup().lookup(ProjectInformation.class);
    }

    @Override
    public synchronized void insertUpdate(DocumentEvent e)
    {
        try
        {
            String code = _currentDocument.getText(e.getOffset(), e.getLength());

            System.out.println(String.format("[CLIENT-INFO] Inserted Text: %s", code));

            if (_currentProject == null)
            {
                _currentClient.addSegmentToBacklog(_currentDocumentName, code, e.getOffset());
            } else
            {
                _currentClient.addSegmentToBacklog(_currentDocumentName, _currentProjectInformation.getDisplayName(), code, e.getOffset());
            }
        } catch (BadLocationException ex)
        {
            System.out.println("[CLIENT-WARNING] Attempted to grab text from invalid point in document");
        } catch (RemoteException ex)
        {
            System.out.println("[CLIENT-WARNING] Caught RemoteException when adding code to backlog");
        }
    }

    @Override
    public synchronized void removeUpdate(DocumentEvent e)
    {
        System.out.println(String.format("[CLIENT-INFO] Text Removed: (Offset: %s) (Length: %s)", e.getOffset(), e.getLength()));

        try
        {
            if (_currentProject == null)
            {
                _currentClient.addSegmentToBacklog(_currentDocumentName, e.getOffset(), e.getLength());
            } else
            {
                _currentClient.addSegmentToBacklog(_currentDocumentName, _currentProjectInformation.getDisplayName(), e.getOffset(), e.getLength());
            }
        } catch (RemoteException ex)
        {
            System.out.println("[CLIENT-WARNING] Caught RemoteException when adding code to backlog");
        }
    }

    /*
        I've tested various circumstances where this method should logically fire,
        however it does not;
            Replacing a section of code by copy+pasting instead causes
            a removeUpdate event, followed by an insertUpdate event
     */
    @Override
    public synchronized void changedUpdate(DocumentEvent e)
    {
        System.out.println("[CLIENT-INFO] Changed In Document: " + Arrays.toString(e.getDocument().getRootElements()));
    }
}
