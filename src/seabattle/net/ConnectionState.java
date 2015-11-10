/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.net;

import java.util.Arrays;
import seabattle.model.*;

public abstract class ConnectionState {
    public final int luck;
    protected int dist_luck = 0;
    
    public enum State {
        /*
        * точка входа
        */
        entry,
        /*
        * устанавливаем при возникновении события Conneted
        * и бросаем жребий
        */
        startLot,
        /*
        * в середине игры разорвано подклюцение
        * ожидаем
        */
        waitConnection,
        /*
        * ожидаем выстрела противника
        */
        waitShot,
        /*
        * мой выстрел
        */
        myShot,
        /*
        * ожидаю результата моего выстрела
        */
        waitMyShotResult,
        /*
        * победа
        */
        win,
        /*
        * поражение
        */
        defeat
    }
    
    public enum LuckResult {
        empty,
        luck,
        fail
    }
    
    public enum ShutProcessingResult {
        miss,//промах
        wounded,//ранен
        killed,//капец
        defeat//поражение -> все убиты
    }
    
    private static final String CoordsDelimiter = "$";
    
    private State state;
    private LuckResult luckResult;
    private int my_counter, enemy_counter;
    private ConnectionProvider provider;
    
    public ConnectionState(ConnectionProvider p) {
        java.util.Random _rnd = new java.util.Random();
        luck = _rnd.nextInt();
        //debug
//        if(p instanceof TCPClient) {
//            luck = 100;
//        }
//        else {
//            luck = 200;
//        }
        state = State.entry;
        luckResult = LuckResult.empty;
        my_counter = 0;
        enemy_counter = 0;
        provider = p;
        initializeWaitConnection();
    }
    
    protected abstract void initializeWaitConnection();
    protected abstract void initializeStartLot();
    protected abstract void initializeWaitShot();
    protected abstract void initializeMyShot();
    protected abstract void initializeWaitMyShotResult();
    protected abstract void initializeWin();//??
    protected abstract void initializeDefeat();//??поражение
    protected abstract void receivedTextMessage(String message);
    //отработать выстрел противника
    protected abstract void processingEnemyShot(CellCords scc);
    //поменить поле - промах
    protected abstract void markMissCoord(CellCords scc);
    //поменить поле - ранен
    protected abstract void markWoundedCoord(CellCords scc);
    //поменить поле - убит
    protected abstract void markKilledCoord(CellCords scc);
    
    public final void connectEvent() {
        switch(state) {
            case entry:
                state = State.startLot;
                initializeStartLot();
                provider.send(
                        new FormatMessage(FormatMessage.Command.TRY_YOUR_LUCK, 
                                Integer.toString(luck)));
                break;
            case waitConnection:
                break;
        }
    }
    
    public final void disconnectEvent() {
        errorEvent(null);
    }
    
    public final void errorEvent(Exception e) {
        if(state == State.entry) {
            String _msg = "Не удалось соединится с сервером!\nПовторить попытку?";
            
            if(e != null) {
                _msg = e.getLocalizedMessage() + "\n\n" + _msg;
            }
            
            switch(javax.swing.JOptionPane.showConfirmDialog(
                    null, 
                    _msg, 
                    "Внимание!", 
                    javax.swing.JOptionPane.YES_NO_OPTION)) {
                case javax.swing.JOptionPane.YES_OPTION:
                    provider.destroy();
                    provider.initialize();
                    break;
                case javax.swing.JOptionPane.NO_OPTION:
                    seabattle.SeaBattle.g_Instance.PrevNetOptions();
                    break;
            }
        }
        else {
            String _msg = "Соединение с сервером разорвано!\nИгра завершена!";
            
            if(e != null) {
                _msg = e.getLocalizedMessage() + "\n\n" + _msg;
            }
            
            javax.swing.JOptionPane.showMessageDialog(
                    null, 
                    _msg);
            seabattle.SeaBattle.g_Instance.PrevStart();
        }
    }

    public final void receiveEvent(FormatMessage msg) {
        if(state == State.startLot) {
            if(msg.command == FormatMessage.Command.TRY_YOUR_LUCK) {
                try {
                    int _enemy_luck = Integer.parseInt(msg.body);
                    
                    if((luck > _enemy_luck) || 
                            ((luck == _enemy_luck) && 
                                (provider instanceof TCPServer))) {
                        luckResult = LuckResult.luck;
                        state = State.myShot;
                        initializeMyShot();
                    }
                    else {
                        luckResult = LuckResult.fail;
                        state = State.waitShot;
                        initializeWaitShot();
                    }

                    dist_luck = _enemy_luck;
                }
                catch(Exception e) {
                    provider.send(
                            new FormatMessage(
                                    FormatMessage.Command.TRY_YOUR_LUCK, 
                                    Integer.toString(luck)));
                }
            }
        }
        
        if(msg.command == FormatMessage.Command.SEND_MESSAGE) {
            receivedTextMessage(msg.body);
        }
        
        if((state == State.waitShot) && 
                (msg.command == FormatMessage.Command.FIRE)) {
            processingEnemyShot(new CellCords(msg.body));
            ++enemy_counter;
        }
        
        if(state == State.waitMyShotResult) {
            switch(msg.command) {
                case FIRE_MISS:
                    markMissCoord(new CellCords(msg.body));
                    initializeWaitShot();//переход хода
                    state = State.waitShot;
                    break;
                case FIRE_WOUNDED:
                    markWoundedCoord(new CellCords(msg.body));
                    initializeMyShot();
                    state = State.myShot;
                    break;
                case FIRE_KILLED:
                    markKilledCoord(new CellCords(msg.body));
                    initializeMyShot();
                    state = State.myShot;
                    break;
                case BATTLE_DEFEAD:
                    initializeWin();
                    state = State.win;
                    break;
            }
        }
    }
    
    public final void sendMyShotCords(CellCords scc) {
        initializeWaitMyShotResult();
        state = State.waitMyShotResult;
        provider.send(
                new FormatMessage(
                        FormatMessage.Command.FIRE, 
                        scc.toString()));
        ++my_counter;
    }
    
    public final void sendEnemyShotMiss(CellCords scc) {
        initializeMyShot();
        state = State.myShot;//переход хода
        provider.send(
                new FormatMessage(
                        FormatMessage.Command.FIRE_MISS, 
                        scc.toString()));
    }
    
    public final void sendEnemyShotWounded(CellCords scc) {
        initializeWaitShot();
        state = State.waitShot;
        provider.send(
                new FormatMessage(
                        FormatMessage.Command.FIRE_WOUNDED, 
                        scc.toString()));
    }
    
    public final void sendEnemyShotKilled(CellCords kcc) {
        initializeWaitShot();
        state = State.waitShot;
        provider.send(
                new FormatMessage(
                        FormatMessage.Command.FIRE_KILLED, 
                        kcc.toString()));
    }
    
    public final void sendEnemyShotDefeat() {
        initializeDefeat();
        state = State.defeat;
        provider.send(
                new FormatMessage(
                        FormatMessage.Command.BATTLE_DEFEAD, 
                        "ASS"));
    }
}
