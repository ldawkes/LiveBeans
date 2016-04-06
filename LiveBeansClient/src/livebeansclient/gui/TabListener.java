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
import org.netbeans.api.project.ProjectUtils;

/**
 *
 * @author Luke Dawkes
 */
public class TabListener implements DocumentListener
{

    private static TabListener _instance;
    private boolean _paused = false;

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
        if (newProject == null)
        {
            return;
        }

        _currentProject = newProject;
        _currentProjectInformation = ProjectUtils.getInformation(_currentProject);
    }

    @Override
    public synchronized void insertUpdate(DocumentEvent e)
    {
        if (_paused)
        {
            System.out.println("Paused on insert");
            return;
        }

        try
        {
            String code = _currentDocument.getText(e.getOffset(), e.getLength());

            System.out.println(String.format("[CLIENT-INFO] Inserted Text: %s", code));

            if (_currentProject == null)
            {
                _currentClient.addSegmentToBacklog(_currentDocumentName, code, e.getOffset());
            }
            else
            {
                _currentClient.addSegmentToBacklog(_currentDocumentName, _currentProjectInformation.getDisplayName(), code, e.getOffset());
            }
        }
        catch (BadLocationException ex)
        {
            System.out.println("[CLIENT-WARNING] Attempted to grab text from invalid point in document");
        }
        catch (RemoteException ex)
        {
            System.out.println("[CLIENT-WARNING] Caught RemoteException when adding code to backlog");
        }
    }

    @Override
    public synchronized void removeUpdate(DocumentEvent e)
    {
        if (_paused)
        {
            System.out.println("Paused on remove");
            return;
        }

        System.out.println(String.format("[CLIENT-INFO] Text Removed: (Offset: %s) (Length: %s)", e.getOffset(), e.getLength()));

        try
        {
            if (_currentProject == null)
            {
                _currentClient.addSegmentToBacklog(_currentDocumentName, e.getOffset(), e.getLength());
            }
            else
            {
                _currentClient.addSegmentToBacklog(_currentDocumentName, _currentProjectInformation.getDisplayName(), e.getOffset(), e.getLength());
            }
        }
        catch (RemoteException ex)
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
        if (_paused)
        {
            return;
        }

        System.out.println("[CLIENT-INFO] Changed In Document: " + Arrays.toString(e.getDocument().getRootElements()));
    }

    public void setPaused(boolean _paused)
    {
        this._paused = _paused;
    }
}
