/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package livebeansclient;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.text.Document;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.TopComponent.Registry;
import org.openide.windows.WindowManager;

/**
 *
 * @author ooddl
 */
public class TabListenerHandler implements PropertyChangeListener
{

    private static TabListenerHandler _instance;
    private final WindowManager _defaultWindowManager;
    private TopComponent _currentTab;
    private Document _currentTabDocument;

    private TabListenerHandler()
    {
        _defaultWindowManager = WindowManager.getDefault();

        Registry reg = TopComponent.getRegistry();
        reg.addPropertyChangeListener(this);
        _currentTab = reg.getActivated();
    }

    public static TabListenerHandler GetInstance()
    {
        if (_instance == null)
        {
            _instance = new TabListenerHandler();
        }

        return _instance;
    }

    public void setUpListeners()
    {
        for (TopComponent tc : getCurrentOpenedEditors())
        {
            System.out.println(String.format("Found Component!\r\n______________________\r\n%s\r\n______________________", tc.getDisplayName()));

            if (tc.getActivatedNodes().length == 0)
            {
                continue;
            }

            System.out.println("Component Has:\r\n");

            for (Node node : tc.getActivatedNodes())
            {
                System.out.println("    |-> " + node.getLookup().lookup(EditorCookie.class).getOpenedPanes()[0].getDocument());
            }
        }
    }

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

            TopComponent newTab = TopComponent.getRegistry().getActivated();

            if (newTab.getActivatedNodes().length == 0)
            {
                return;
            }

            _currentTab = newTab;

            _currentTabDocument = _currentTab.getActivatedNodes()[0].getLookup().lookup(EditorCookie.class).getOpenedPanes()[0].getDocument();

            TabListener listenerInstance = TabListener.getInstance();
            listenerInstance.setCurrentDocument(_currentTabDocument);

            _currentTabDocument.addDocumentListener(listenerInstance);
        } catch (RemoteException ex)
        {
            Exceptions.printStackTrace(ex);
        }

    }
}
