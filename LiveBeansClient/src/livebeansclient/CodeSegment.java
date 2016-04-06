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
