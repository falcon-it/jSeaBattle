/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle;

import seabattle.gui.*;

public final class SeaBattle {
    public static final SeaBattle g_Instance;
    
    static {
        g_Instance = new SeaBattle();
    }

    public enum State {
        empty,//стартовое состояние
        start,//окно приведствия
        construct,//конструирование поля
        net_address,//поле сконструировано -> ввод настроек подключения
        battle,//игра
        battle_exit//результат игры
    }
    
    private State m_state;
    private seabattle.gui.MainWindow m_mainWindow;
    private seabattle.model.BattleField m_myField, m_enemyField;
    private seabattle.net.Connection m_Connection;
    
    private SeaBattle() {
        m_state = State.empty;
        m_myField = new seabattle.model.BattleField();
        m_enemyField = new seabattle.model.BattleField();
        m_Connection = new seabattle.net.Connection();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
		if ("Nimbus".equals(info.getName())) {
	            javax.swing.UIManager.setLookAndFeel(info.getClassName());
	            break;
	        }
	    }
	} catch (Exception e) { }
        
        java.awt.EventQueue.invokeLater(() -> {
                SeaBattle.g_Instance.m_mainWindow = new seabattle.gui.MainWindow();
                SeaBattle.g_Instance.Next();
                SeaBattle.g_Instance.m_mainWindow.setVisible(true);
            }
        );
    }
    
    public final void PrevStart() {
        m_state = State.start;
        m_Connection.destroyConnection();
        m_mainWindow.InitilizeStartState((seabattle.gui.PaintPanel viewPanel) -> {
            StartInitializer.Initialize(viewPanel);
        });
    }
    
    public final void PrevConstruct() {
        if(m_state == State.battle) {
            m_Connection.getSetting().destroyProvider();
        }
        
        m_state = State.construct;
        m_mainWindow.InitilizeConstructState(
                (seabattle.gui.PaintPanel viewPanel, 
                        seabattle.gui.ConstructInitializer.IConstructionStatus cb) -> {
                    ConstructInitializer.Initialize(viewPanel, m_myField, cb);
                });
    }

    public final void PrevNetOptions() {
        if(m_state == State.battle) {
            m_Connection.getSetting().destroyProvider();
        }
        
        m_state = State.net_address;
        m_mainWindow.InitilizeNetAddressState(
                (seabattle.gui.PaintPanel viewPanel, 
                        NetAddressInitializer.NetAddressValidatorState v) -> {
                    NetAddressInitializer.Initialize(viewPanel, m_Connection, v);
                });
    }
    
    public final void Prev() {
        switch(m_state) {
            case construct:
                m_state = State.start;
                m_mainWindow.InitilizeStartState((seabattle.gui.PaintPanel viewPanel) -> {
                    StartInitializer.Initialize(viewPanel);
                });
                break;
        }
    }
    
    public final void Next() {
        switch(m_state) {
            case empty:
                m_state = State.start;
                m_mainWindow.InitilizeStartState((seabattle.gui.PaintPanel viewPanel) -> {
                    StartInitializer.Initialize(viewPanel);
                });
                break;
            case start:
                m_state = State.construct;
                m_mainWindow.InitilizeConstructState(
                        (seabattle.gui.PaintPanel viewPanel, 
                                seabattle.gui.ConstructInitializer.IConstructionStatus cb) -> {
                            ConstructInitializer.Initialize(viewPanel, m_myField, cb);
                        });
                break;
            case construct:
                m_state = State.net_address;
                m_mainWindow.InitilizeNetAddressState(
                        (seabattle.gui.PaintPanel viewPanel, 
                                NetAddressInitializer.NetAddressValidatorState v) -> {
                            NetAddressInitializer.Initialize(viewPanel, m_Connection, v);
                        });
                break;
            case net_address:
                m_state = State.battle;
                m_mainWindow.InitializeBattleState((seabattle.gui.PaintPanel viewPanel, 
                        javax.swing.JPanel statePanel, 
                        MainWindow.IBattleConnectInitializeSuccess hideCallback) -> {
                    BattleInitializer.Initialize(viewPanel, 
                            m_myField, m_enemyField, m_Connection, statePanel, hideCallback);
                });
                break;
        }
    }
    
    public final void Exit() {
        m_Connection.destroyConnection();
        System.exit(0);
    }
}
