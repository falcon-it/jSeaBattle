/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.net;

import java.io.*;
import java.net.*;

public final class SocketWrapper {
    private final Socket socket;
    private final IConnectionListener listener;
    private final Object monitor;
    private final BufferedInputStream input;
    private final byte[] inputBuffer;
    
    public SocketWrapper(Socket s, IConnectionListener l, Object synch) 
            throws IOException, SocketException {
        socket = s;
        listener = l;
        monitor = synch;
        
        input = new BufferedInputStream(socket.getInputStream());
        inputBuffer = new byte[socket.getReceiveBufferSize()];
        
        if(listener != null) {
            java.awt.EventQueue.invokeLater(() -> {
                listener.onConnect();
            });
        }
    }
    
    public final void readCycle() throws IOException {
        int _read = 0;
        while((_read = input.read(inputBuffer)) != -1) {
            byte[] _msg = new byte[_read];
            System.arraycopy(inputBuffer, 0, _msg, 0, _read);
            
            try {
                FormatMessage _m  = new FormatMessage(_msg);
                
                synchronized(monitor) {
                    if(listener != null) {
                        java.awt.EventQueue.invokeLater(() -> {
                            listener.onReceive(_m);
                        });
                    }
                }
            }
            catch(Exception e) {
                //ну просто пришло хреновое сообщение
            }
        }
        
        synchronized(monitor) {
            if(listener != null) {
                java.awt.EventQueue.invokeLater(() -> {
                    listener.onDisconnect();
                });
            }
        }
    }
    
    public final void send(byte[] msg) throws IOException {
        socket.getOutputStream().write(msg);
    }
    
    public final void destroy() throws IOException {
        socket.close();
    }
}
