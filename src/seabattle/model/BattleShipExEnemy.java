/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.model;

import java.util.LinkedList;

public final class BattleShipExEnemy {
    private BattleShip realShip = null;
    private BattleShipExMy.State m_State;
    private LinkedList<CellCords> cords;
    
    public BattleShipExEnemy(CellCords cc, boolean killed) {
        if(killed) {
            m_State = BattleShipExMy.State.killed;
            realShip = new BattleShip(cc, cc);
        }
        else {
            m_State = BattleShipExMy.State.wounded;
            cords = new LinkedList<>();
            cords.add(cc);
        }
    }

    public final BattleShipExMy.State getState() {
        return m_State;
    }
    
    public final CellCords[] getCords() {
        CellCords[] _ccl = null;
        
        switch(m_State) {
            case wounded:
                _ccl = new CellCords[cords.size()];
                _ccl = cords.toArray(_ccl);
                break;
            case killed:
                _ccl = realShip.getCords();
                break;
        }
        
        return _ccl;
    }
    
    public final boolean isHitThisShip(CellCords cc) {
        if(m_State == BattleShipExMy.State.wounded) {
            for(CellCords _cc : cords) {
                if((Math.abs(_cc.getX() - cc.getX()) + 
                        Math.abs(_cc.getY() - cc.getY())) == 1) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public final void includeHit(CellCords cc, 
            MyBattleField.ShotResult ccs, 
            BattleShipExEnemy meShip) {
        cords.add(cc);
        
        if(meShip != null) {
            CellCords[] _mscc = meShip.getCords();
            for(CellCords _ccc : _mscc) {
                cords.add(_ccc);
            }
        }
        
        java.util.Collections.sort(cords, 
                (CellCords o1, CellCords o2) -> {
                    int _dx = o1.getX() - o2.getX(),
                            _dy = o1.getY() - o2.getY();
                    return Math.abs(_dx) > Math.abs(_dy) ? _dx : _dy;
                });
        
        if(ccs == MyBattleField.ShotResult.killed) {
            m_State = BattleShipExMy.State.killed;
            realShip = new BattleShip(
                    cords.get(0), 
                    cords.get(cords.size() - 1));
            cords = null;
        }
    }

    public final void includeHit(CellCords cc, 
            MyBattleField.ShotResult ccs) {
        includeHit(cc, ccs, null);
    }
    
    public final void updateBattleFieldState(BattleField bf) {
        switch(m_State) {
            case wounded:
                for(CellCords _cc : cords) {
                    bf.setCellState(
                            _cc.getX(), 
                            _cc.getY(), 
                            BattleField.CellState.hit);
                }
                break;
            case killed:
                for(CellCords _cc : getCords()) {
                    bf.setCellState(
                            _cc.getX(), 
                            _cc.getY(), 
                            BattleField.CellState.killed);
                }
                

                CellCords[] _kccl = getCords();
                CellCords _min, _max;
                
                if((_kccl[0].getX() <= _kccl[_kccl.length - 1].getX()) && 
                        (_kccl[0].getY() <= _kccl[_kccl.length - 1].getY())) {
                    _min = _kccl[0];
                    _max = _kccl[_kccl.length - 1];
                }
                else {
                    _min = _kccl[_kccl.length - 1];
                    _max = _kccl[0];
                }
                
                //top line
                int _c_bgn = _min.getX() - 1,
                        _c_end = _max.getX() + 1,
                        _c_line = _min.getY() - 1;
                
                _c_bgn = _c_bgn < 0 ? 0 : _c_bgn;
                _c_end = _c_end > 9 ? 9 : _c_end;
                
                if(((_c_end - _c_bgn) >= 0) && 
                        (_c_line >= 0)) {
                    for(int i = _c_bgn; i <= _c_end; ++i) {
                        bf.setCellState(
                                i, 
                                _c_line, 
                                BattleField.CellState.emptiness);
                    }
                }

                //bootom line
                _c_line = _max.getY() + 1;
                
                if(((_c_end - _c_bgn) >= 0) && 
                        (_c_line <= 9)) {
                    for(int i = _c_bgn; i <= _c_end; ++i) {
                        bf.setCellState(
                                i, 
                                _c_line, 
                                BattleField.CellState.emptiness);
                    }
                }
                
                //left line
                _c_bgn = _min.getY();
                _c_end = _max.getY();
                _c_line = _min.getX() - 1;
                
                if(((_c_end - _c_bgn) >= 0) &&
                        (_c_line >= 0)) {
                    for(int i = _c_bgn; i <= _c_end; ++i) {
                        bf.setCellState(
                                _c_line, 
                                i, 
                                BattleField.CellState.emptiness);
                    }
                }
                
                //right line
                _c_bgn = _min.getY();
                _c_end = _max.getY();
                _c_line = _max.getX() + 1;
                
                if(((_c_end - _c_bgn) >= 0) &&
                        (_c_line <= 9)) {
                    for(int i = _c_bgn; i <= _c_end; ++i) {
                        bf.setCellState(
                                _c_line, 
                                i, 
                                BattleField.CellState.emptiness);
                    }
                }
                
                
                break;
        }
    }
    
    public final boolean includeHit(CellCords cc, BattleShipExMy.State ccs) {
        boolean _r = false;
        
        if(m_State == BattleShipExMy.State.wounded) {
            for(CellCords _cc : cords) {
                if((Math.abs(_cc.getX() - cc.getX()) + 
                        Math.abs(_cc.getY() - cc.getY())) == 1) {
                    cords.add(cc);
                    _r = true;
                    break;
                }
            }
            
            if(_r && (cords.size() > 1)) {
                java.util.Collections.sort(cords, 
                        (CellCords o1, CellCords o2) -> {
                            int _dx = o1.getX() - o2.getX(),
                                    _dy = o1.getY() - o2.getY();
                            return Math.abs(_dx) > Math.abs(_dy) ? _dx : _dy;
                        });
                
                if(m_State != ccs) {
                    //значит потопили
                    m_State = ccs;
                    realShip = new BattleShip(
                            cords.get(0), 
                            cords.get(cords.size() - 1));
                    cords = null;
                }
            }
        }
        
        return _r;
    }
    
    @Override
    public String toString() {
        String _r;
        
        switch(m_State) {
            case wounded:
                int _idx = cords.size();
                String _name = BattleShip.ShipNames.length > _idx ? 
                        BattleShip.ShipNames[_idx] : "fail";
                _r = cords.size() == 1 ? 
                        String.format("[!] ?%2$s [%1$s]", 
                                cords.get(0).toString(), _name) :
                        String.format("[!] ?%2$s [%1$s]->[%3$s]", 
                                cords.get(0).toString(), _name, 
                                cords.get(cords.size() - 1).toString());
                break;
            case killed:
                _r = String.format("[X] %1$s", realShip.toString());
                break;
            default:
                _r = "fail";
                break;
        }
        
        return _r;
    }
}
