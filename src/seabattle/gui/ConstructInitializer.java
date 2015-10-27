/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.gui;

import java.awt.Color;
import seabattle.model.*;

public final class ConstructInitializer {
   private ConstructInitializer() {}
   
   public static java.awt.Rectangle getFieldRect(javax.swing.JComponent c) {
       int side = c.getWidth() > c.getHeight() ? c.getHeight() : c.getWidth();
       side -= 20;
       return new java.awt.Rectangle(10, 10, side, side);
   }
   
   public interface IConstructionStatus {
       void completed();
       void extend();
   }
   
   public static void Initialize(seabattle.gui.PaintPanel viewPanel, 
           BattleField myBattleField, IConstructionStatus cb) {
        viewPanel.setLayout(new java.awt.BorderLayout());
       
        seabattle.gui.PaintPanel _mainPanel = new seabattle.gui.PaintPanel();
        seabattle.gui.PaintPanel _infoPanel = new seabattle.gui.PaintPanel();

        _infoPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.JLabel _cords = new javax.swing.JLabel();
        _cords.setText(BattleField.formatCords());
        _cords.setHorizontalAlignment(javax.swing.JLabel.RIGHT);
        _cords.setFont(new java.awt.Font("Verdana", java.awt.Font.BOLD, 18));
        _cords.setBorder(new javax.swing.border.EmptyBorder(5, 5, 5, 5));
        _infoPanel.add(_cords, java.awt.BorderLayout.SOUTH);

        javax.swing.DefaultListModel _list_model = myBattleField.shipList;
        javax.swing.JList _shipList = new javax.swing.JList(_list_model);
        _shipList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        _shipList.setFixedCellWidth(150);
        _shipList.setBackground(new Color(javax.swing.UIManager.getDefaults().getColor("control").getRGB()));
        javax.swing.JScrollPane _scrollPane = new javax.swing.JScrollPane(_shipList);
        _infoPanel.add(_scrollPane, java.awt.BorderLayout.CENTER);
        
        ConstructBattleField constructBattleField = new ConstructBattleField(myBattleField, _list_model, cb);

        _shipList.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                if(java.awt.event.KeyEvent.VK_DELETE == (int)e.getKeyChar()) {
                    //javax.swing.JOptionPane.showMessageDialog(null, "Удалить это нах");
                    constructBattleField.deleteShipByIndex(_shipList.getSelectedIndex());
                    _mainPanel.repaint();
                }
            }
        });
       
       
        _mainPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                CellCords cc1 = BattleField.getGridCellOnCords(
                        ConstructInitializer.getFieldRect(viewPanel), 
                        e.getX(), e.getY());
                constructBattleField.setCurrentPosition(cc1);
                _mainPanel.repaint();
                _cords.setText(constructBattleField.getStateText(cc1));
            }
            
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                CellCords cc1 = BattleField.getGridCellOnCords(
                        ConstructInitializer.getFieldRect(viewPanel), 
                        e.getX(), e.getY());
                constructBattleField.setCurrentPosition(cc1);
                _mainPanel.repaint();
                _cords.setText(constructBattleField.getStateText(cc1));
            }
        });
        
        _mainPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                _cords.setText(constructBattleField.getStateText());
                constructBattleField.setPosition(new CellCords());
                _mainPanel.repaint();
            }
           
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                CellCords cc1 = BattleField.getGridCellOnCords(
                        ConstructInitializer.getFieldRect(viewPanel), 
                        e.getX(), e.getY());
                if(!cc1.isFail()) {
                    constructBattleField.setPosition(cc1);
                    _mainPanel.repaint();
                }
                _cords.setText(constructBattleField.getStateText(cc1));
            }
           
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                CellCords cc1 = BattleField.getGridCellOnCords(
                        ConstructInitializer.getFieldRect(viewPanel), 
                        e.getX(), e.getY());
                constructBattleField.setPosition(cc1);
                _mainPanel.repaint();
                _cords.setText(constructBattleField.getStateText(cc1));
            }
        });
       
       viewPanel.add(_mainPanel, java.awt.BorderLayout.CENTER);
       viewPanel.add(_infoPanel, java.awt.BorderLayout.EAST);

       _mainPanel.setPainter((java.awt.Graphics g) -> {
           constructBattleField.paintFieldState(g, ConstructInitializer.getFieldRect(viewPanel));
       });
   }
}
