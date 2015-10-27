/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.net;

public class ServerSetting extends ConnectionSetting {
    public final int port;
    
    public ServerSetting(int port) {
        this.port = port;
    }
    
    @Override
    protected ConnectionProvider createProvider() {
        return new TCPServer(this);
    }
    
    @Override
    public String toString() {
        return String.format("server: %1$d", port);
    }
}
