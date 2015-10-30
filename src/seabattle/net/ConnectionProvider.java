/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.net;

public abstract class ConnectionProvider {
    protected static final String notConnectedInfo = "fail";
    
    protected final Object m_synch = new Object();
    protected IConnectionListener m_listener;
    protected threadQueue m_sender = new threadQueue();
    protected boolean m_fClose = false;
    protected String m_connectedInfo;

    public ConnectionProvider() {
        m_connectedInfo = ConnectionProvider.notConnectedInfo;
    }
    
    public abstract boolean isConnected();
    protected abstract void initializeProvider();
    public abstract void destroy();
    public abstract void send(FormatMessage msg);
    
    public void initialize(IConnectionListener l) {
        if(!isConnected()) {
            m_listener = l;
            initializeProvider();
        }
    }
    
    public final void initialize() {
        if(!isConnected()) {
            initializeProvider();
        }
    }
    
    public String getConnectedInformation() {
        synchronized(m_synch) {
            return m_connectedInfo;
        }
    }
}
