/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.net;

public class RouterClientSetting extends ConnectionSetting {
    public static final int failID = -1;
    
    public final String address;
    public final int port, id;
    
    public RouterClientSetting(String address, int port, int id) {
        this.address = address;
        this.port = port;
        this.id = id;
    }
    
    @Override
    protected ConnectionProvider createProvider() {
        return new RouterTcpClient(this);
    }

    @Override
    public String toString() {
        return String.format("router: %1$s: %2$d [%3$d]", address, port, id);
    }
}
