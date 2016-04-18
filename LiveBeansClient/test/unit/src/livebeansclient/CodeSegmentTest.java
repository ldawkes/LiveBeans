/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package livebeansclient;

import java.rmi.RemoteException;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Luke Dawkes
 */
public class CodeSegmentTest
{

    private final CodeSegment instance;

    public CodeSegmentTest() throws RemoteException
    {
        instance = new CodeSegment();
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test of setDocumentOffset method, of class CodeSegment.
     *
     * @throws java.rmi.RemoteException
     */
    @Test
    public void testPropertyDocumentOffset() throws RemoteException
    {
        System.out.println("\r\nsetDocumentOffset");

        int newOffset = 0;
        instance.setDocumentOffset(newOffset);

        int result = instance.getDocumentOffset();

        assertNotNull(result);
        assertEquals(newOffset, result);
    }

    /**
     * Test of setCodeText method, of class CodeSegment.
     *
     * @throws java.rmi.RemoteException
     */
    @Test
    public void testPropertyCodeText() throws RemoteException
    {
        System.out.println("\r\nsetCodeText");

        String newCodeText = "Test";
        instance.setCodeText(newCodeText);

        String result = instance.getCodeText();

        assertNotNull(result);
        assertEquals(newCodeText, result);
    }

    /**
     * Test of setAuthorID method, of class CodeSegment.
     *
     * @throws java.rmi.RemoteException
     */
    @Test
    public void testPropertyAuthorID() throws RemoteException
    {
        System.out.println("\r\nsetAuthorID");

        int newAuthorID = 0;
        instance.setAuthorID(newAuthorID);

        int result = instance.getAuthorID();

        assertNotNull(result);
        assertEquals(newAuthorID, result);
    }

    /**
     * Test of setCodeLength method, of class CodeSegment.
     *
     * @throws java.rmi.RemoteException
     */
    @Test
    public void testPropertyCodeLength() throws RemoteException
    {
        System.out.println("\r\nsetCodeLength");

        int codeLength = 0;
        instance.setCodeLength(codeLength);

        int result = instance.getCodeLength();

        assertNotNull(result);
        assertEquals(codeLength, result);
    }

    /**
     * Test of setDocumentName method, of class CodeSegment.
     *
     * @throws java.rmi.RemoteException
     */
    @Test
    public void testPropertyDocumentName() throws RemoteException
    {
        System.out.println("\r\nsetDocumentName");

        String newDocumentName = "";
        instance.setDocumentName(newDocumentName);

        String result = instance.getDocumentName();

        assertNotNull(result);
        assertEquals(newDocumentName, result);
    }

    /**
     * Test of setProject method, of class CodeSegment.
     *
     * @throws java.rmi.RemoteException
     */
    @Test
    public void testPropertyProject() throws RemoteException
    {
        System.out.println("\r\nsetProject");

        String newProjectName = "";
        instance.setProjectName(newProjectName);

        String result = instance.getProjectName();

        assertNotNull(result);
        assertEquals(newProjectName, result);
    }

}
