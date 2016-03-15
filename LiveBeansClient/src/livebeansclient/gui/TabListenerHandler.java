/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package livebeansclient.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.TopComponent.Registry;
import org.openide.windows.WindowManager;

/**
 *
 * @author Luke Dawkes
 */
public class TabListenerHandler implements PropertyChangeListener
{

    private static TabListenerHandler _instance;

    /**
     * Gets the singleton instance of TabListenerHandler
     *
     * @return TabListenerHandler instance
     */
    public static TabListenerHandler GetInstance()
    {
        if (_instance == null)
        {
            _instance = new TabListenerHandler();
        }

        return _instance;
    }
    private final WindowManager _defaultWindowManager;
    private TopComponent _currentTab;
    private StyledDocument _currentTabDocument;

    private TabListenerHandler()
    {
        _defaultWindowManager = WindowManager.getDefault();

        Registry reg = TopComponent.getRegistry();
        reg.addPropertyChangeListener(this);
        _currentTab = reg.getActivated();
    }

    /**
     * Sets up the listeners for all opened editors
     */
    public void setUpListeners()
    {
        for (TopComponent tc : getCurrentOpenedEditors())
        {
            System.out.println(String.format("[CLIENT-INFO] Found Component: %s", tc.getDisplayName()));

            if (tc.getActivatedNodes().length == 0)
            {
                continue;
            }

            System.out.println("[CLIENT-INFO] Component Has:");

            for (Node node : tc.getActivatedNodes())
            {
                System.out.println("    |-> " + node.getLookup().lookup(EditorCookie.class).getOpenedPanes()[0].getDocument());
            }
        }
    }

    /**
     * Gets a collection of currently opened NetBeans editors
     *
     * @return Collection of TopComponents
     * @see Collection
     * @see TopComponent
     */
    private Collection<TopComponent> getCurrentOpenedEditors()
    {
        final ArrayList<TopComponent> openedEditors = new ArrayList<>();
        final WindowManager windowManager = WindowManager.getDefault();
        for (Mode mode : windowManager.getModes())
        {
            if (windowManager.isEditorMode(mode))
            {
                openedEditors.addAll(Arrays.asList(windowManager.getOpenedTopComponents(mode)));
            }
        }
        return openedEditors;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        TopComponent activeComponent = TopComponent.getRegistry().getActivated();

        if (activeComponent == _currentTab)
        {
            return;
        }

        try
        {
            if (_currentTabDocument != null)
            {
                _currentTabDocument.removeDocumentListener(TabListener.getInstance());
            }

            if (activeComponent.getActivatedNodes().length == 0)
            {
                return;
            }

            _currentTab = activeComponent;

            // Do a check to see if they actually focused on an editor window we can listen to
            if (_currentTab.getActivatedNodes()[0].getLookup().lookup(EditorCookie.class) == null)
            {
                System.out.println("[CLIENT-INFO] Did not focus on an editor window");
            } else
            {
                _currentTabDocument = _currentTab.getActivatedNodes()[0].getLookup().lookup(EditorCookie.class).getDocument();
                System.out.println("[CLIENT-INFO] New Tab Name: " + _currentTab.getActivatedNodes()[0].getDisplayName());

                DataObject documentStream = (DataObject) _currentTabDocument.getProperty(Document.StreamDescriptionProperty);
                FileObject documentFileObject = documentStream.getPrimaryFile();

                TabListener listenerInstance = TabListener.getInstance();
                listenerInstance.setCurrentDocument(_currentTabDocument);
                listenerInstance.setCurrentDocumentName(_currentTab.getActivatedNodes()[0].getDisplayName());
                listenerInstance.setCurrentProject(getTabProject(documentFileObject));

                System.out.println("[CLIENT-INFO] Current Tab Document: " + _currentTabDocument);

                _currentTabDocument.addDocumentListener(listenerInstance);
            }
        } catch (RemoteException | NullPointerException ex)
        {
            System.out.println(String.format("[CLIENT-WARNING] Caught a %s error:\r\n%s", ex.getClass().getName(), ex.toString()));
        }

    }

    /**
     * Returns a project if the file is associated with one
     *
     * @param file The file to find the project for
     * @return Null if no project is associated, Project if there is one
     * associated
     * @see Project
     */
    public Project getTabProject(FileObject file)
    {
        Project[] openProjects = OpenProjects.getDefault().getOpenProjects();

        if (openProjects.length == 0)
        {
            System.out.println("[CLIENT-INFO] No open projects");
            return null;
        }

        List<String> parentFolders = Arrays.asList(file.getPath().split("/"));
        System.out.println(String.format("[ClIENT-INFO] Parent folders:\r\n%s", Arrays.toString(parentFolders.toArray())));

        for (Project project : openProjects)
        {
            String projectName = project.getLookup().lookup(ProjectInformation.class).getName();
            System.out.println("[CLIENT-INFO] Project name: " + projectName);

            if (parentFolders.contains(projectName))
            {
                System.out.println("[CLIENT-INFO] File is part of project: " + projectName);
                return project;
            }
        }

        System.out.println("[CLIENT-INFO] Failed to find project");
        return null;
    }
}
