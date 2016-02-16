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
public interface ILiveBeansServer extends Remote
{
    boolean RegisterClient(ILiveBeansClient client) throws RemoteException;
    boolean UnRegisterClient(ILiveBeansClient client) throws RemoteException;
}
