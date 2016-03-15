/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package livebeansclient;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import livebeanscommon.ILiveBeansCodeSegment;

/**
 *
 * @author Luke Dawkes
 */
public class CodeSegment extends UnicastRemoteObject implements ILiveBeansCodeSegment
{

    private int _authorID, _documentOffset, _codeLength;
    private final Date _authorDate;
    private String _codeText, _documentName, _projectName;

    public CodeSegment() throws RemoteException
    {
        _authorDate = new Date();
    }

    @Override
    public void setDocumentOffset(int newOffset) throws RemoteException
    {
        _documentOffset = newOffset;
    }

    @Override
    public void setCodeText(String newCodeText) throws RemoteException
    {
        _codeText = newCodeText;
    }

    @Override
    public void setAuthorID(int newAuthorID) throws RemoteException
    {
        _authorID = newAuthorID;
    }

    @Override
    public int getDocumentOffset() throws RemoteException
    {
        return _documentOffset;
    }

    @Override
    public String getCodeText() throws RemoteException
    {
        return _codeText;
    }

    @Override
    public int getAuthorID() throws RemoteException
    {
        return _authorID;
    }

    @Override
    public Date getAuthorDate() throws RemoteException
    {
        return _authorDate;
    }

    @Override
    public void setCodeLength(int codeLength) throws RemoteException
    {
        _codeLength = codeLength;
    }

    @Override
    public int getCodeLength() throws RemoteException
    {
        return _codeLength;
    }

    @Override
    public void setDocumentName(String newDocumentName) throws RemoteException
    {
        _documentName = newDocumentName;
    }

    @Override
    public void setProject(String newProjectName) throws RemoteException
    {
        _projectName = newProjectName;
    }

    @Override
    public String getDocumentName() throws RemoteException
    {
        return _documentName;
    }

    @Override
    public String getProjectName() throws RemoteException
    {
        return _projectName;
    }
}
