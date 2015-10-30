/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.net;

public final class RouterTcpClient extends ConnectionProvider {
    public final RouterClientSetting setting;
    private ConnectionProvider realClient;    
    
    public RouterTcpClient(RouterClientSetting s) {
        setting = s;
        ClientSetting _cs = new ClientSetting(s.address, s.port);
        realClient = _cs.createProvider();
    }
    
    @Override
    public boolean isConnected() {
        return realClient.isConnected();
    }
    
    @Override
    protected void initializeProvider() {
        realClient.initialize();
    }
    
    @Override
    public void initialize(IConnectionListener l) {
        realClient.initialize(l);
    }
    
    @Override
    public void destroy() {    
        realClient.destroy();
    }
    
    @Override
    public void send(FormatMessage msg) {
        realClient.send(
                new FormatMessage(msg, setting.id));
    }
    
    @Override
    public String getConnectedInformation() {
        return realClient.getConnectedInformation().replaceAll("client", "router client");
    }
}
