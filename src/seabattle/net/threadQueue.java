/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.net;

import java.util.LinkedList;

public final class threadQueue implements AutoCloseable {
    private interface IExitThread extends Runnable {
        
    }
    
    private final LinkedList<Runnable> m_queue = new LinkedList<Runnable>();
    private Thread m_thread = null;
    private final Object m_synchThreadStarted = new Object(),
            m_synchThreadStart = new Object();
    
    public threadQueue() {
        synchronized(m_synchThreadStart) {
            m_thread = new Thread(() -> {
                synchronized(m_synchThreadStart) {
                    m_synchThreadStart.notifyAll();
                }
                
                try {
                    while(true) {
                        Runnable _task = null;

                        synchronized(m_queue) {
                            _task = m_queue.poll();
                        }

                        if(_task != null) {
                            if(_task instanceof IExitThread) {
                                break;
                            }
                            else {
                                _task.run();
                            }
                        }
                        else {
                            try {
                                synchronized(m_queue) {
                                    m_queue.wait();
                                }
                            }
                            catch(Exception e) {

                            }
                        }
                    }
                }
                finally {
                    synchronized(m_synchThreadStarted) {
                        m_synchThreadStarted.notifyAll();
                    }
                }
            }, "thread message queue");
            m_thread.start();
            
            try {
                m_synchThreadStart.wait();
            }
            catch(Exception e) {
                
            }
        }
    }
    
    public void add(Runnable task) {
        synchronized(m_queue) {
            m_queue.offer(task);
            m_queue.notify();
        }
    }
    
    @Override
    public void close() {
        add(new IExitThread() {
            @Override
            public void run() {
            
            }
        });
    }
    
    public boolean waitExitExecute() {
        synchronized(m_synchThreadStarted) {
            try {
                m_synchThreadStarted.wait();
                return true;
            }
            catch(Exception e) {

            }
        }
        
        return false;
    }
    
    public boolean waitExitExecute(long timeout) {
        synchronized(m_synchThreadStarted) {
            try {
                long _begin = System.currentTimeMillis();
                
                m_synchThreadStarted.wait(timeout);
                
                if((System.currentTimeMillis() - _begin) < timeout) {
                    return true;
                }
            }
            catch(Exception e) {
                
            }
        }
        
        return false;
    }

    public boolean waitExitExecute(long timeout, int nanos) {
        synchronized(m_synchThreadStarted) {
            try {
                long _begin = System.nanoTime();
                
                m_synchThreadStarted.wait(timeout, nanos);
                
                long _end = System.nanoTime();
                
                if((System.nanoTime() - _begin) < (1000000 * timeout + nanos)) {
                    return true;
                }
            }
            catch(Exception e) {
                
            }
        }
        
        return false;
    }
}
