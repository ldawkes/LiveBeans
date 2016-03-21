/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package livebeanscommon;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 *
 * @author Luke Dawkes
 */
public interface ILiveBeansServer extends Remote
{

    boolean registerClient(ILiveBeansClient client) throws RemoteException;

    boolean unRegisterClient(ILiveBeansClient client) throws RemoteException;

    void sendHeartbeat(int clientID) throws RemoteException;

    void distributeCodeSegments(List<ILiveBeansCodeSegment> codeSegments, int clientID) throws RemoteException;
}
