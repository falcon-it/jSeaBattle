/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.net;

public final class Connection {
    private ConnectionSetting m_setting;
    
    public final ConnectionSetting getSetting() {
        return m_setting;
    }
    
    public final boolean setSetting(ConnectionSetting setting) {
        if((m_setting == null) && 
                (setting != null)) {
            m_setting = setting;
            return true;
        }
        
        return false;
    }
    
    public final void destroyConnection() {
        if(m_setting != null) {
            m_setting.destroyProvider();
            m_setting = null;
        }
    }
}
