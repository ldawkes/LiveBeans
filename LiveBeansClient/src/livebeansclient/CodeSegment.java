/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package livebeansclient;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Date;
import livebeanscommon.ILiveBeansClient;
import livebeanscommon.ILiveBeansCodeSegment;

/**
 *
 * @author Luke Dawkes
 */
public class CodeSegment implements ILiveBeansCodeSegment, Serializable
{

    private ILiveBeansClient _author;
    private Date _authorDate;
    private int _documentOffset;
    private String _codeText;

    public CodeSegment()
    {
        _authorDate = new Date();
    }

    @Override
    public void SetDocumentOffset(int newOffset) throws RemoteException
    {
        _documentOffset = newOffset;
    }

    @Override
    public void SetCodeText(String newCodeText) throws RemoteException
    {
        _codeText = newCodeText;
    }

    @Override
    public void SetAuthor(ILiveBeansClient newAuthor) throws RemoteException
    {
        _author = newAuthor;
    }

    @Override
    public int GetDocumentOffset() throws RemoteException
    {
        return _documentOffset;
    }

    @Override
    public String GetCodeText() throws RemoteException
    {
        return _codeText;
    }

    @Override
    public ILiveBeansClient GetAuthor() throws RemoteException
    {
        return _author;
    }

    @Override
    public Date GetAuthorDate() throws RemoteException
    {
        return _authorDate;
    }
}
