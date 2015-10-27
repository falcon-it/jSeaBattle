/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.gui;

public class PaintPanel extends javax.swing.JPanel {
    public interface IPaint {
        void paint(java.awt.Graphics g);
    }
    
    protected IPaint m_painter;
    
    public void setPainter(IPaint p) {
        m_painter = p;
    }
    
    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        if(m_painter!= null) {
            m_painter.paint(g);
        }
    }
}
