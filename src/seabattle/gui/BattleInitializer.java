/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.gui;

import seabattle.model.*;
import seabattle.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public final class BattleInitializer {
    private static final int BorderMinOffset = 5, PanelSplit = 5;
    private static final ImageIcon wait, message, new_message, wait_enemy_panel;
    private static final String connectionLabel = "соединение...",
            connectionLabel2 = "Соединение",
            startLotLavel = "бросить жребий...",
            startLotLavel2 = "Кому повезёт?",
            waitShotLabel2 = "Ход врага",
            myShotLabel2 = "Ваш ход",
            waitShotResultLabel = "ждём результат...",
            waitShotResultLabel2 = "Ждём результат",
            winLabel = "УРА!!! Победа.",
            defeatLabel = "Фу-Фу-Фу...";
    
    private interface IFinalScreenSaver {
        void initialize(seabattle.gui.PaintPanel viewPanel, 
                java.awt.image.BufferedImage logo, 
                String result);
    }
    
    private static final IFinalScreenSaver screenSaverInitializer;
    
    static {
        ImageIcon _wait = null, _message = null, 
                _new_message = null, _wait_enemy_panel = null;

        try {
            _wait = new ImageIcon(
                    BattleInitializer.class.getResource("wait.gif"));
        }
        catch(Exception e) {
            
        }
        
        wait = _wait;
        
        try {
            _message = new ImageIcon(
                    BattleInitializer.class.getResource("msg.png"));
        }
        catch(Exception e) {
            
        }
        
        message = _message;

        try {
            _new_message = new ImageIcon(
                    BattleInitializer.class.getResource("new_msg.gif"));
        }
        catch(Exception e) {
            
        }
        
        new_message = _new_message;

        try {
            _wait_enemy_panel = new ImageIcon(
                    BattleInitializer.class.getResource("wait_enemy_panel.gif"));
        }
        catch(Exception e) {
            
        }
        
        wait_enemy_panel = _wait_enemy_panel;
        
        screenSaverInitializer = 
                (seabattle.gui.PaintPanel viewPanel, 
                        java.awt.image.BufferedImage logo, 
                        String result) -> {
                    viewPanel.removeAll();
                    viewPanel.setLayout(new BorderLayout());
                    PaintPanel _p = new PaintPanel();
                    viewPanel.add(_p, BorderLayout.CENTER);
                    _p.setPainter((java.awt.Graphics g) -> {
                        java.awt.Font f = g.getFont();
                        java.awt.Font font = 
                                new java.awt.Font(
                                        "Verdana", 
                                        java.awt.Font.PLAIN, 
                                        f.getSize() * 5);
                        g.setFont(font);
                        g.drawString(result, 20, 100);
                        g.setFont(f);
                        
                        if(logo != null) {
                            g.drawImage(
                                    logo, 
                                    50, 
                                    120 + font.getSize(), 
                                    viewPanel);
                        }
                        
                        //Container j = viewPanel.getParent();
                        //viewPanel.revalidate();
                    });
                };
    }

    private BattleInitializer() {}
    
    public static Rectangle getFieldRect(JComponent c) {
        int side = c.getWidth() > c.getHeight() ? c.getHeight() : c.getWidth();
        side -= 10;
        return new Rectangle(5, 5, side, side);
    }
    
    public static void Initialize(seabattle.gui.PaintPanel viewPanel, 
            BattleField my, BattleField enemy, Connection conn, JPanel statePanel, 
            MainWindow.IBattleConnectInitializeSuccess hideCallback) {
        viewPanel.setLayout(null);
        enemy.eraseCells();
        
        JPanel _myPanel = new JPanel();
        _myPanel.setBorder(BorderFactory.createTitledBorder("This is Я"));
        
        viewPanel.add(_myPanel);
        
        JPanel _enemyPanel = new JPanel();
        _enemyPanel.setBorder(BorderFactory.createTitledBorder("This is Негодяй"));
        
        viewPanel.add(_enemyPanel);
        
        _myPanel.setLayout(new BorderLayout());
        
        DefaultListModel<BattleShipExMy> _my_list_model = new DefaultListModel<>();
        for(int i = 0; i < my.shipList.getSize(); ++i) {
            _my_list_model.addElement(new BattleShipExMy(my.shipList.getElementAt(i)));
        }
        JList _myshipList = new JList(_my_list_model);
        _myshipList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        _myshipList.setFixedCellWidth(150);
        _myshipList.setBackground(new Color(UIManager.getDefaults().getColor("control").getRGB()));
        JScrollPane _myshipScrollList = new JScrollPane(_myshipList);
        _myPanel.add(_myshipScrollList, BorderLayout.WEST);
        
        seabattle.gui.PaintPanel _myPainPanel = new seabattle.gui.PaintPanel();
        _myPanel.add(_myPainPanel, BorderLayout.CENTER);
        
        seabattle.model.MyBattleField _my = new seabattle.model.MyBattleField(my);
        _myPainPanel.setPainter((Graphics g) -> {
            _my.paintFieldState(g, BattleInitializer.getFieldRect(_myPainPanel));
        });
        
        _enemyPanel.setLayout(new java.awt.BorderLayout());
        
        JPanel _enemylistPanel = new JPanel();
        _enemylistPanel.setLayout(new BorderLayout());
        
        DefaultListModel<BattleShipExEnemy> _enemy_list_model = new DefaultListModel();//enemy.shipList;
        JList _enemyshipList = new JList(_enemy_list_model);
        _enemyshipList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        _enemyshipList.setFixedCellWidth(150);
        _enemyshipList.setBackground(new Color(UIManager.getDefaults().getColor("control").getRGB()));
        _enemylistPanel.add(new JScrollPane(_enemyshipList), BorderLayout.CENTER);
        _enemyPanel.add(_enemylistPanel, BorderLayout.EAST);
        
        JLabel _enemyCords = new JLabel();
        _enemyCords.setText(BattleField.formatCords());
        _enemyCords.setHorizontalAlignment(JLabel.RIGHT);
        _enemyCords.setFont(new java.awt.Font("Verdana", Font.BOLD, 14));
        _enemyCords.setBorder(new javax.swing.border.EmptyBorder(5, 5, 5, 5));
        _enemylistPanel.add(_enemyCords, BorderLayout.SOUTH);
        
        JLabel _battleState = new JLabel();
        _battleState.setText(BattleInitializer.connectionLabel);
        _battleState.setBorder(new javax.swing.border.EmptyBorder(5, 5, 5, 5));
        _battleState.setFont(new java.awt.Font("Verdana", Font.BOLD, 14));
        _battleState.setHorizontalAlignment(JLabel.RIGHT);
        _battleState.setForeground(Color.BLUE);
        _enemylistPanel.add(_battleState, BorderLayout.NORTH);
        
        seabattle.gui.PaintPanel _enemyPainPanel = new seabattle.gui.PaintPanel();
        _enemyPanel.add(_enemyPainPanel, BorderLayout.CENTER);
        
        _enemyPainPanel.setLayout(new java.awt.BorderLayout());
        JLabel _enemyWaitConnected = new JLabel(BattleInitializer.wait_enemy_panel);
        _enemyPainPanel.add(_enemyWaitConnected, BorderLayout.CENTER);
        
        seabattle.model.EnemyBattleField _enemy = new seabattle.model.EnemyBattleField(enemy);
        _enemyPainPanel.setPainter((Graphics g) -> {
            _enemy.paintFieldState(g, BattleInitializer.getFieldRect(_enemyPainPanel));
        });
        
        viewPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int _max_vpw = (viewPanel.getWidth() - 
                        (BattleInitializer.BorderMinOffset * 2 + 
                            BattleInitializer.PanelSplit)) /2,
                        _max_vph = viewPanel.getHeight() - 
                        BattleInitializer.BorderMinOffset * 2;
                Insets _iss = _myPanel.getBorder().getBorderInsets(_myPanel);
                int _max_wbf = _max_vpw - 
                        (_iss.left + _iss.right + _myshipScrollList.getWidth()),
                        _max_hbf = _max_vph - (_iss.bottom + _iss.top);
                
                int _x_offset = BattleInitializer.BorderMinOffset,
                        _x_width = _max_vpw,
                        _y_offset = BattleInitializer.BorderMinOffset,
                        _y_height = _max_vph;
                
                if(_max_wbf < _max_hbf) {
                    //минимум по ширине
                    _y_height = _max_wbf + (_iss.bottom + _iss.top);
                    _y_offset = (viewPanel.getHeight() - _y_height) / 2;
                } 
                else {
                    //минимум по высоте
                    _x_width = _max_hbf + (_iss.left + _iss.right + _myshipScrollList.getWidth());
                    _x_offset = (viewPanel.getWidth() - (_x_width * 2 + BattleInitializer.PanelSplit)) / 2;
                }
                        
                _myPanel.setBounds(_x_offset, _y_offset, _x_width, _y_height);
                _enemyPanel.setBounds(
                        _x_offset + BattleInitializer.PanelSplit + _x_width, 
                        _y_offset, _x_width, _y_height);
                
                _enemyPanel.revalidate();
                _myPanel.revalidate();
            }
        });
        
        ConnectionSetting _st = conn.getSetting();
        ConnectionProvider _prov = _st.getProvider();
        
        BattleMessageDialog _bmd = new BattleMessageDialog(
            new BattleMessageDialog.MessageSender() {
                @Override
                protected void _sendMessage(FormatMessage msg) {
                    _prov.send(msg);
                }
            }
        );
            
        //_bmd.setVisible(true);//!!!debug
        
        JButton _msgButton = new JButton(BattleInitializer.message);
        _msgButton.setVisible(false);
        _msgButton.setToolTipText("Отправить сообщение врагу...");
        _msgButton.addActionListener(
            (ActionEvent e) -> { 
                _msgButton.setIcon(BattleInitializer.message);
                _bmd.setVisible(true);
            });
        statePanel.add(_msgButton);
        
        JPanel _idPanel = new JPanel();
        _idPanel.setVisible(false);
        _idPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        JLabel _idValueLabel = new JLabel();
        _idValueLabel.setForeground(Color.red);
        _idPanel.add(_idValueLabel);

        statePanel.add(_idPanel);
        
        JPanel _waitPanel = new JPanel();
        JLabel _waitIconLabel = new JLabel(BattleInitializer.wait);
        _waitPanel.add(_waitIconLabel);
        
        JLabel _waitTitleLabel = new JLabel(BattleInitializer.connectionLabel);
        _waitPanel.add(_waitTitleLabel);
        
        statePanel.add(_waitPanel);
        
        ConnectionState _cs = new ConnectionState(_prov) {
            @Override
            protected void initializeWaitConnection() {
                _waitPanel.setVisible(true);
                _idPanel.setVisible(false);
                _msgButton.setVisible(false);
                _enemyWaitConnected.setVisible(true);
                _enemy.setState(EnemyBattleField.FieldState.waitConnection);
                _waitTitleLabel.setText(String.format("%1$s {%2$s}", BattleInitializer.connectionLabel, _st.toString()));
                _battleState.setText(BattleInitializer.connectionLabel2);
            }

            @Override
            protected void initializeStartLot() {
                _waitPanel.setVisible(true);
                _idPanel.setVisible(true);
                _msgButton.setVisible(true);
                _enemyWaitConnected.setVisible(true);
                _enemy.setState(EnemyBattleField.FieldState.waitConnection);
                _waitTitleLabel.setText(BattleInitializer.startLotLavel);
                _battleState.setText(BattleInitializer.startLotLavel2);
                hideCallback.hideButtons();
            }
            
            @Override
            protected void initializeWaitShot() {
                _waitPanel.setVisible(false);
                _idPanel.setVisible(true);
                _msgButton.setVisible(true);
                _enemyWaitConnected.setVisible(false);
                _enemy.setState(EnemyBattleField.FieldState.waitEnemyShot);
                _battleState.setText(BattleInitializer.waitShotLabel2);
                _myPainPanel.revalidate();
                _enemyPainPanel.revalidate();
            }
            
            @Override
            protected void initializeMyShot() {
                _waitPanel.setVisible(false);
                _idPanel.setVisible(true);
                _msgButton.setVisible(true);
                _enemyWaitConnected.setVisible(false);
                _enemy.setState(EnemyBattleField.FieldState.setShotCoords);
                _battleState.setText(BattleInitializer.myShotLabel2);
                _myPainPanel.revalidate();
                _enemyPainPanel.revalidate();
            }
            
            @Override
            protected void initializeWaitMyShotResult() {
                _waitPanel.setVisible(true);
                _idPanel.setVisible(true);
                _msgButton.setVisible(false);
                _enemyWaitConnected.setVisible(false);
                _enemy.setState(EnemyBattleField.FieldState.waitShotResult);
                _waitTitleLabel.setText(BattleInitializer.waitShotResultLabel);
                _battleState.setText(BattleInitializer.waitShotResultLabel2);
                _myPainPanel.revalidate();
                _enemyPainPanel.revalidate();
            }
            
            @Override
            protected void initializeWin() {
                java.awt.image.BufferedImage logo = null;
                try {
                    logo = javax.imageio.ImageIO.read(
                            BattleInitializer.class.getResource("win.png"));
                    
                }
                catch(Exception e) {
                    
                }
                
                BattleInitializer.screenSaverInitializer.initialize(
                        viewPanel, logo, BattleInitializer.winLabel);
                viewPanel.revalidate();

                _waitPanel.setVisible(false);
                _idPanel.setVisible(true);
                _msgButton.setVisible(true);
                _enemyWaitConnected.setVisible(false);
            }
            
            @Override
            protected void initializeDefeat() {
                java.awt.image.BufferedImage logo = null;
                try {
                    logo = javax.imageio.ImageIO.read(
                            BattleInitializer.class.getResource("defeat.png"));
                    
                }
                catch(Exception e) {
                    
                }
                
                BattleInitializer.screenSaverInitializer.initialize(
                        viewPanel, logo, BattleInitializer.defeatLabel);
                viewPanel.revalidate();

                _waitPanel.setVisible(false);
                _idPanel.setVisible(true);
                _msgButton.setVisible(true);
                _enemyWaitConnected.setVisible(false);
            }
            
            @Override
            protected void receivedTextMessage(String message) {
                _bmd.addMessage(message);
                if(!_bmd.isVisible()) {
                    _msgButton.setIcon(BattleInitializer.new_message);
                }
            }
            
            @Override
            protected void processingEnemyShot(CellCords scc) {
                MyBattleField.ShotResultInfo _r = 
                        _my.executeEnemyShot(scc, _my_list_model);
                _myPainPanel.repaint();
                _myshipList.updateUI();
                switch(_r.shotResult) {
                    case miss:
                        sendEnemyShotMiss(_r.shotCoords);
                        break;
                    case wounded:
                        sendEnemyShotWounded(_r.shotCoords);
                        break;
                    case killed:
                        switch(_r.getBattleState()) {
                            case battle:
                                sendEnemyShotKilled(_r.shotCoords);
                                break;
                            case defeat:
                                sendEnemyShotDefeat();
                                break;
                        }
                        break;
                }
            }
            
            @Override
            protected void markMissCoord(CellCords scc) {
                _enemy.markFireResult(
                        scc, 
                        MyBattleField.ShotResult.miss, 
                        _enemy_list_model);
            }
            
            @Override
            protected void markWoundedCoord(CellCords scc) {
                _enemy.markFireResult(
                        scc, 
                        MyBattleField.ShotResult.wounded, 
                        _enemy_list_model);
                _enemyshipList.revalidate();
            }

            @Override
            protected void markKilledCoord(CellCords scc) {
                _enemy.markFireResult(
                        scc, 
                        MyBattleField.ShotResult.killed, 
                        _enemy_list_model);
                _enemyshipList.revalidate();
            }
        };
        
        _prov.initialize(new IConnectionListener() {
            @Override
            public void onConnect() {
                _idValueLabel.setText(_prov.getConnectedInformation());
                _cs.connectEvent();
            }
            
            @Override
            public void onDisconnect() {
                if(_bmd.isVisible()) {
                   _bmd.setVisible(false);
                }
                
                _cs.disconnectEvent();
            }

            @Override
            public void onError(Exception e) {
                if(_bmd.isVisible()) {
                   _bmd.setVisible(false);
                }
                
                _cs.errorEvent(e);
            }

            @Override
            public void onReceive(FormatMessage msg) {
                _cs.receiveEvent(msg);
            }
        });
        
        _enemyPainPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                if(_enemy.getState() == EnemyBattleField.FieldState.setShotCoords) {
                    CellCords cc1 = BattleField.getGridCellOnCords(
                            BattleInitializer.getFieldRect(_enemyPainPanel), 
                            e.getX(), e.getY());
                    _enemy.setCurrentCell(cc1);
                    _enemyPanel.repaint();
                    _enemyCords.setText(_enemy.getInputState());
                }
            }
            
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                if(_enemy.getState() == EnemyBattleField.FieldState.setShotCoords) {
                    CellCords cc1 = BattleField.getGridCellOnCords(
                            BattleInitializer.getFieldRect(_enemyPainPanel), 
                            e.getX(), e.getY());
                    _enemy.setCurrentCell(cc1);
                    _enemyPanel.repaint();
                    _enemyCords.setText(_enemy.getInputState());
                }
            }
        });
        
        _enemyPainPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                _enemy.setCurrentCell(new CellCords());
                _enemy.setDownCell(new CellCords());
                _enemyCords.setText(_enemy.getInputState());
                _enemyPanel.repaint();
            }
           
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if(_enemy.getState() == EnemyBattleField.FieldState.setShotCoords) {
                    CellCords cc1 = BattleField.getGridCellOnCords(
                            BattleInitializer.getFieldRect(_enemyPainPanel), 
                            e.getX(), e.getY());
                    if(!cc1.isFail() && 
                            (enemy.getCellState(cc1.getX(), cc1.getY()) == 
                                    BattleField.CellState.empty)) {
                        _enemy.setDownCell(cc1);
                        _enemyPanel.repaint();
                    }
                    _enemyCords.setText(_enemy.getInputState());
                }
            }
           
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if(_enemy.getState() == EnemyBattleField.FieldState.setShotCoords) {
                    CellCords cc1 = BattleField.getGridCellOnCords(
                            BattleInitializer.getFieldRect(_enemyPainPanel), 
                            e.getX(), e.getY());

                    if(!cc1.isFail()) {
                        if(CellCords.isEquallyCords(cc1, _enemy.getDownCell())) {
                            _cs.sendMyShotCords(cc1);
                            _enemy.enemyBattleField.setCellState(
                                    cc1.getX(), cc1.getY(), 
                                    BattleField.CellState.shot);
                        }
                    }
                    
                    _enemy.setDownCell(new CellCords());
                    _enemy.setCurrentCell(new CellCords());
                    _enemyCords.setText(_enemy.getInputState());
                    _enemyPanel.repaint();
                }
            }
        });
    }
}
