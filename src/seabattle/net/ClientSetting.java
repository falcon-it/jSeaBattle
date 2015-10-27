/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.net;

public final class ClientSetting extends ConnectionSetting {
    public final String address;
    public final int port;
    
    public ClientSetting(String address, int port) {
        this.address = address;
        this.port = port;
    }
    
    @Override
    protected ConnectionProvider createProvider() {
        return new TCPClient(this);
    }
    
    @Override
    public String toString() {
        return String.format("client: %1$s: %2$d", address, port);
    }
}
