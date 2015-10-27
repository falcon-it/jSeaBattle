/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.model;

public final class MyBattleField {
    public final BattleField m_my;
    
    public enum ShotResult {
        miss,//промах
        wounded,//ранен
        killed//капец
    }
    
    public enum BattleState {
        battle,
        defeat
    }
    
    private BattleState m_battleState;
    
    public MyBattleField(BattleField my) {
        m_my = my;
        m_battleState = BattleState.battle;
    }

    public final void paintFieldState(java.awt.Graphics g, java.awt.Rectangle rect) {
        m_my.paintFieldState(g, rect);
    }
    
    public final BattleState getBattleState() {
        return m_battleState;
    }
    
    private final void updateBattleState(
            javax.swing.DefaultListModel<BattleShipExMy> shipList) {
        int _c = 0;
        for(int i = 0; i < shipList.getSize(); ++i) {
            BattleShipExMy _bs = shipList.getElementAt(i);
            CellCords _cc[] = _bs.realShip.getCords();
            if(m_my.getCellState(_cc[0].getX(), _cc[0].getY()) == 
                    BattleField.CellState.killed) {
                ++_c;
            }
        }
        
        m_battleState = (_c == shipList.getSize()) ? 
                BattleState.defeat : BattleState.battle;
    }
    
    public final class ShotResultInfo {
        public final ShotResult shotResult;
        public final CellCords shotCoords;
        public final CellCords[] hitCords;
        
        private ShotResultInfo(ShotResult sr, CellCords sc, CellCords[] hc) {
            shotResult = sr;
            shotCoords = sc;
            hitCords = hc;
        }
        
        public final BattleState getBattleState() {
            return m_battleState;
        }
    }

    public final ShotResultInfo executeEnemyShot(CellCords scc, 
            javax.swing.DefaultListModel<BattleShipExMy> shipList) {
        if(m_my.getCellState(scc.getX(), scc.getY()) != BattleField.CellState.empty) {
            for(int i = 0; i < shipList.getSize(); ++i) {
                BattleShipExMy _bs = shipList.getElementAt(i);
                
                if(_bs.checkShotResult(scc, m_my) == BattleShipExMy.State.wounded) {
                    m_my.setCellState(scc.getX(), scc.getY(),
                            BattleField.CellState.hit);
                    return new ShotResultInfo(ShotResult.wounded, scc, null);
                }
                
                if(_bs.checkShotResult(scc, m_my) == BattleShipExMy.State.killed) {
                    CellCords _cc[] = _bs.realShip.getCords();
                    
                    for(CellCords _ccc : _cc) {
                        m_my.setCellState(_ccc.getX(), _ccc.getY(), 
                                BattleField.CellState.killed);
                    }
                    
                    updateBattleState(shipList);
                    
                    return new ShotResultInfo(ShotResult.killed, scc, 
                            _bs.realShip.getCords());
                }
            }
        }
        
        m_my.setCellState(scc.getX(), scc.getY(), BattleField.CellState.bloomer);
        return new ShotResultInfo(ShotResult.miss, scc, null);//промах
    }
}
