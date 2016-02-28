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

    void SetDocumentOffset(int documentOffset) throws RemoteException;

    void SetCodeText(String code) throws RemoteException;

    void SetAuthor(ILiveBeansClient author) throws RemoteException;

    int GetDocumentOffset() throws RemoteException;

    String GetCodeText() throws RemoteException;

    ILiveBeansClient GetAuthor() throws RemoteException;

    Date GetAuthorDate() throws RemoteException;
}
