/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.net;

import java.io.IOException;
import java.net.*;

public final class TCPServer extends ConnectionProvider {
    public final ServerSetting setting;
    private SocketWrapper m_socket_w;
    private ServerSocket m_listenerSocket;
    private boolean m_manualCloseListener;
    
    public TCPServer(ServerSetting s) {
        super();
        setting = s;
        m_manualCloseListener = false;
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
                m_listenerSocket = new ServerSocket(setting.port);
                Socket _s = m_listenerSocket.accept();
                
                synchronized(m_synch) {
                    m_listenerSocket.close();
                    m_listenerSocket = null;
                }
                
                m_connectedInfo = String.format("%1$s:%2$s [server]", _s.getLocalAddress().getHostName(), _s.getLocalPort());
                
                synchronized(m_synch) {
                    m_socket_w = new SocketWrapper(_s, m_listener, m_synch);
                    m_fClose = false;
                }
                
                m_socket_w.readCycle();
            }
            catch(IOException e) {
                synchronized(m_synch) {
                    if((m_listener != null) &&
                            (!m_manualCloseListener)) {
                        java.awt.EventQueue.invokeLater(() -> {
                            m_listener.onDisconnect();
                        });
                    }
                }
            }
            catch(Exception e) {
                synchronized(m_synch) {
                    if((m_listener != null) &&
                            (!m_manualCloseListener)) {
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
        }, "Socker reader thread").start();
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
             
            if(m_listenerSocket != null) {
                try {
                    m_listenerSocket.close();
                    m_listenerSocket = null;
                    m_manualCloseListener = true;
                }
                catch(Exception e) {
                    
                }
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
