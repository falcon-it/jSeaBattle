/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.net;

import java.net.InetAddress;
import java.net.Socket;
import java.io.IOException;

public final class TCPClient extends ConnectionProvider {
    public final ClientSetting setting;
    private SocketWrapper m_socket_w;
    
    public TCPClient(ClientSetting s) {
        super();
        setting = s;
    }
    
    @Override
    public boolean isConnected() {
        synchronized(m_synch) {
            return (m_socket_w != null);
        }
    }
    
    @Override
    protected void initializeProvider() {
        new Thread(() -> {
            try {
                InetAddress _ia = InetAddress.getByName(setting.address);
                Socket _s = new Socket(_ia, setting.port);
                synchronized(m_synch) {
                    m_connectedInfo = String.format("%1$s:%2$s [client]", _s.getInetAddress().getHostName(), _s.getPort());
                }
                
                synchronized(m_synch) {
                    m_socket_w = new SocketWrapper(_s, m_listener, m_synch);
                    m_fClose = false;
                }
                
                m_socket_w.readCycle();
            }
            catch(IOException e) {
                synchronized(m_synch) {
                    if(m_listener != null) {
                        java.awt.EventQueue.invokeLater(() -> {
                            m_listener.onDisconnect();
                        });
                    }
                }
            }
            catch(Exception e) {
                synchronized(m_synch) {
                    if(m_listener != null) {
                        if(!m_fClose) {
                            java.awt.EventQueue.invokeLater(() -> {
                                m_listener.onError(e);
                            });
                        }
                        else {
                            java.awt.EventQueue.invokeLater(() -> {
                                m_listener.onDisconnect();
                            });
                        }
                    }
                }
            }
        }).start();
    }
    
    @Override
    public void destroy() {
        synchronized(m_synch) {
            if(m_socket_w != null) {
                
                m_fClose = true;
                        
                try {
                    m_socket_w.destroy();
                }
                catch(Exception e) {
                    
                }
                
                m_socket_w = null;
            }
        }
    }
    
    @Override
    public void send(FormatMessage msg) {
        m_sender.add(() -> {
            synchronized(m_synch) {
                if(m_socket_w != null) {
                    try {
                        m_socket_w.send(msg.toBinaryMessage());
                    }
                    catch(Exception e) {
                        
                    }
                }
            }
        });
    }
}
