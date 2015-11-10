/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public final class MainWindow extends JFrame {
    public interface IPanelInitializer {
        void initialize(seabattle.gui.PaintPanel viewPanel);
    }
    
    public interface IPanelConstructInitializer {
        void initialize(PaintPanel viewPanel, 
                ConstructInitializer.IConstructionStatus cb);
    }
    
    public interface IPanelNetAddressInitialize {
        void initialize(PaintPanel viewPanel, 
                NetAddressInitializer.NetAddressValidatorState s);
    }
    
    public interface IPanelBattleInitializer {
        void initialize(PaintPanel viewPanel, JPanel statePanel, 
                IBattleConnectInitializeSuccess hideCallback);
    }
    
    public interface IBattleConnectInitializeSuccess {
        void hideButtons();
        void showButtons();
    }
    
    public MainWindow() {
        super();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                seabattle.SeaBattle.g_Instance.Exit();
            }
        });
        
        setTitle("Sea Battle");
        
        Toolkit t = Toolkit.getDefaultToolkit();
        Dimension ss = t.getScreenSize();
        setSize((int)(ss.getWidth() * 2 / 3), (int)(ss.getHeight() * 2 / 3));
        setLocation((int)(ss.getWidth() / 6), (int)(ss.getHeight() / 6));
        setMinimumSize(new Dimension((int)ss.getWidth() / 3, (int)ss.getHeight() / 3));
        
        setLayout(new BorderLayout());
        JPanel bottomPanel = new JPanel();
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        getContentPane().add(new JPanel(), BorderLayout.CENTER);
    }
    
    public final void InitilizeStartState(IPanelInitializer panelInitializer) {
        Container _c = getContentPane();
        _c.remove(1);

        JPanel _bottomPanel = (JPanel)_c.getComponent(0);
        _bottomPanel.removeAll();
        _bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton btn = new JButton("Начать >>");
        btn.addActionListener((ActionEvent e) -> { 
            seabattle.SeaBattle.g_Instance.Next(); 
        });
        _bottomPanel.add(btn);

        btn = new JButton("Выход");
        btn.addActionListener((ActionEvent e) -> { 
            seabattle.SeaBattle.g_Instance.Exit(); 
        });
        _bottomPanel.add(btn);
        
        seabattle.gui.PaintPanel _mainPanel = new seabattle.gui.PaintPanel();
        _c.add(_mainPanel, BorderLayout.CENTER);
        
        panelInitializer.initialize(
                (seabattle.gui.PaintPanel)getContentPane().getComponent(1));
        
        revalidate();
    }
    
    public final void InitilizeConstructState(
            IPanelConstructInitializer panelInitializer) {
        Container _c = getContentPane();
        _c.remove(1);

        JPanel _bottomPanel = (JPanel)_c.getComponent(0);
        _bottomPanel.removeAll();
        _bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btn = new JButton("<< Старт");
        btn.addActionListener((ActionEvent e) -> { 
            seabattle.SeaBattle.g_Instance.PrevStart(); 
        });
        _bottomPanel.add(btn);
        
        JButton btn0 = new JButton("Соединение >>");
        btn0.addActionListener((ActionEvent e) -> { 
            seabattle.SeaBattle.g_Instance.Next(); 
        });
        _bottomPanel.add(btn0);
        
        ConstructInitializer.IConstructionStatus cb = 
                new ConstructInitializer.IConstructionStatus() {
                    public final void completed() {
                        btn0.setEnabled(true);
                    }
                    public final void extend() {
                        btn0.setEnabled(false);//!!!debug
                    }
                };
        
        cb.extend();
        
        btn = new JButton("Выход");
        btn.addActionListener((ActionEvent e) -> { 
            seabattle.SeaBattle.g_Instance.Exit(); 
        });
        _bottomPanel.add(btn);

        seabattle.gui.PaintPanel _mainPanel = new seabattle.gui.PaintPanel();
        _c.add(_mainPanel, BorderLayout.CENTER);
            
        panelInitializer.initialize(
                (seabattle.gui.PaintPanel)getContentPane().getComponent(1), cb);
        
        revalidate();
    }
    
    public final void InitilizeNetAddressState(
            IPanelNetAddressInitialize panelInitializer) {
        Container _c = getContentPane();
        _c.remove(1);

        JPanel _bottomPanel = (JPanel)_c.getComponent(0);
        _bottomPanel.removeAll();
        _bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btn = new JButton("<<<< Старт");
        btn.addActionListener((ActionEvent e) -> { 
            seabattle.SeaBattle.g_Instance.PrevStart(); 
        });
        _bottomPanel.add(btn);
        
        btn = new JButton("<< Конструирование");
        btn.addActionListener((ActionEvent e) -> { 
            seabattle.SeaBattle.g_Instance.PrevConstruct(); 
        });
        _bottomPanel.add(btn);

        NetAddressInitializer.NetAddressValidatorState v = 
                new NetAddressInitializer.NetAddressValidatorState();
                
        btn = new JButton("Подключение >>");
        btn.addActionListener((ActionEvent e) -> { 
            if(v.isValid()) { 
                seabattle.SeaBattle.g_Instance.Next(); 
            } 
        });
        _bottomPanel.add(btn);
        
        btn = new JButton("Выход");
        btn.addActionListener((ActionEvent e) -> { 
            seabattle.SeaBattle.g_Instance.Exit(); 
        });
        _bottomPanel.add(btn);

        seabattle.gui.PaintPanel _mainPanel = new seabattle.gui.PaintPanel();
        _c.add(_mainPanel, BorderLayout.CENTER);
            
        panelInitializer.initialize(
                (seabattle.gui.PaintPanel)getContentPane().getComponent(1), v);
        
        revalidate();
    }
    
    public final void InitializeBattleState(
            IPanelBattleInitializer panelInitializer) {
        Container _c = getContentPane();
        _c.remove(1);

        JPanel _bottomPanel = (JPanel)_c.getComponent(0);
        _bottomPanel.removeAll();
        _bottomPanel.setLayout(new BorderLayout());
        
        JPanel _buttonPanel = new JPanel();
        _buttonPanel.setLayout(new FlowLayout());

        JButton btn = new JButton("<<<< Старт");
        btn.addActionListener((ActionEvent e) -> { 
            seabattle.SeaBattle.g_Instance.PrevStart(); 
        });
        _buttonPanel.add(btn);
        JButton btn_h3 = btn;

        btn = new JButton("<<< Конструирование");
        btn.addActionListener((ActionEvent e) -> { 
            seabattle.SeaBattle.g_Instance.PrevConstruct(); 
        });
        _buttonPanel.add(btn);
        JButton btn_h1 = btn;
        
        btn = new JButton("<< Соединение");
        btn.addActionListener((ActionEvent e) -> { 
            seabattle.SeaBattle.g_Instance.PrevNetOptions(); 
        });
        _buttonPanel.add(btn);
        JButton btn_h2 = btn;
        
        btn = new JButton("Выход");
        btn.addActionListener((ActionEvent e) -> { 
            seabattle.SeaBattle.g_Instance.Exit(); 
        });
        _buttonPanel.add(btn);
        
        JPanel _alignButtonPanel = new JPanel();
        _alignButtonPanel.setLayout(new BorderLayout());
        _alignButtonPanel.add(_buttonPanel, BorderLayout.SOUTH);
        
        _bottomPanel.add(_alignButtonPanel, BorderLayout.EAST);
        
        JPanel _statePanel = new JPanel();
        _bottomPanel.add(_statePanel, BorderLayout.WEST);
        
        seabattle.gui.PaintPanel _mainPanel = new seabattle.gui.PaintPanel();
        _c.add(_mainPanel, BorderLayout.CENTER);
        
        panelInitializer.initialize(
                (seabattle.gui.PaintPanel)getContentPane().getComponent(1), 
                _statePanel, new IBattleConnectInitializeSuccess() {
                    @Override
                    public void hideButtons() {
                        btn_h1.setVisible(false);
                        btn_h2.setVisible(false);
                        btn_h3.setText(btn_h3.getText().substring(2));
                    }
                    
                    @Override
                    public void showButtons() {
                        btn_h1.setVisible(true);
                        btn_h2.setVisible(true);
                        btn_h3.setText("<<" + btn_h3.getText());
                    }
                });
        
        revalidate();
    }
}
