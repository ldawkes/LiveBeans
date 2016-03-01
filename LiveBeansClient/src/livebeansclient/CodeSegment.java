/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package livebeansclient;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Date;
import livebeanscommon.ILiveBeansCodeSegment;

/**
 *
 * @author Luke Dawkes
 */
public class CodeSegment implements ILiveBeansCodeSegment, Serializable
{

    private int _authorID;
    private Date _authorDate;
    private int _documentOffset, _codeLength;
    private String _codeText;

    public CodeSegment()
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
}
