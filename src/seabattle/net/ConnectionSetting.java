/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.net;

public abstract class ConnectionSetting {
    private ConnectionProvider m_currentProvider;
    
    protected abstract ConnectionProvider createProvider();
    
    public ConnectionProvider getProvider() {
        if(m_currentProvider == null) {
            m_currentProvider = createProvider();
        }
        
        return m_currentProvider;
    }
    
    public void destroyProvider() {
        if(m_currentProvider != null) {
            m_currentProvider.destroy();
            m_currentProvider = null;
        }
    }
}
