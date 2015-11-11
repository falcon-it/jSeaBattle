/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.model;

public final class BattleShipExMy {
    public final BattleShip realShip;
    
    public enum State {
        none,
        wounded,//ранен
        killed
    }
    
    private State m_State;
    
    public BattleShipExMy(BattleShip bs) {
        realShip = bs;
        m_State = State.none;
    }
    
    public final State getState() {
        return m_State;
    }
    
    public final State checkShotResult(CellCords scc, BattleField my) {
        CellCords _scc[] = realShip.getCords();
        boolean _mark_state = false;
        
        for(CellCords _cc : _scc) {
            if(CellCords.isEquallyCords(_cc, scc)) {
                _mark_state = true;
                m_State = State.killed;

                my.setCellState(scc.getX(), scc.getY(), BattleField.CellState.hit);
                
                for(CellCords _cc0 : _scc) {
                    if(my.getCellState(_cc0.getX(), _cc0.getY()) != BattleField.CellState.hit) {
                        m_State = State.wounded;
                        break;
                    }
                }
                
                break;
            }
        }
        
        return (_mark_state ? m_State : State.none);
    }
    
    @Override
    public String toString() {
        switch(m_State) {
            case wounded:
                return String.format("[!] %1$s", realShip.toString());
            case killed:
                return String.format("[X] %1$s", realShip.toString());
        }
        
        return realShip.toString();
    }
}
