/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package livebeanscommon;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author ooddl
 */
public interface ILiveBeansClient extends Remote
{

    void setID(int newID) throws RemoteException;

    void setName(String newName) throws RemoteException;

    void connectToServer(String ipAddress) throws RemoteException;

    void disconnectFromServer() throws RemoteException;

    void updateLocalCode(ILiveBeansCodeSegment newCodeSegment) throws RemoteException;

    void updateRemoteCode(ILiveBeansCodeSegment newCodeSegment) throws RemoteException;

    int getID() throws RemoteException;

    String getName() throws RemoteException;

    ILiveBeansServer getServer() throws RemoteException;
}
