/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.net;

public interface IConnectionListener {
    void onConnect();
    void onDisconnect();
    
    void onError(Exception e);
    
    void onReceive(FormatMessage msg);
}
