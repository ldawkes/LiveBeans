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
    void SetID(int newID) throws RemoteException;
    void SetName(String newName) throws RemoteException;
    void ConnectToServer(String ipAddress) throws RemoteException;
    void DisconnectFromServer() throws RemoteException;
    void UpdateLocalCode(ILiveBeansCodeSegment newCodeSegment) throws RemoteException;
    void UpdateRemoteCode(ILiveBeansCodeSegment newCodeSegment) throws RemoteException;
    
    int GetID() throws RemoteException;
    String GetName() throws RemoteException;
    ILiveBeansServer GetServer() throws RemoteException;
}
