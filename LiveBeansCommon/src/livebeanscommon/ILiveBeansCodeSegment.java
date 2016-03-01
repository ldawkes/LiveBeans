/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package livebeanscommon;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

/**
 *
 * @author Luke Dawkes
 */
public interface ILiveBeansCodeSegment extends Remote
{

    void setDocumentOffset(int documentOffset) throws RemoteException;

    void setCodeLength(int codeLength) throws RemoteException;

    void setCodeText(String code) throws RemoteException;

    void setAuthorID(int authorID) throws RemoteException;

    int getDocumentOffset() throws RemoteException;

    int getCodeLength() throws RemoteException;

    String getCodeText() throws RemoteException;

    int getAuthorID() throws RemoteException;

    Date getAuthorDate() throws RemoteException;
}
