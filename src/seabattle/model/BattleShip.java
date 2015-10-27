/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.model;

public final class BattleShip {
    public static final String[] ShipNames = {"Подлодка", "Эсминец", "Крейсер", "Линкор"};
    public static final int lengthFail = -1;
    
    private final CellCords[] m_ccs;
    
    public BattleShip(CellCords cc1, CellCords cc2) {
        m_ccs = BattleShip.getAllCellCords(cc1, cc2);
    }
    
    public final int getLength() {
        return m_ccs == null ? BattleShip.lengthFail : m_ccs.length;
    }
    
    public final boolean isFail() {
        return (getLength() == BattleShip.lengthFail);
    }
    
    public final String getName() {
        if(!isFail()) {
            return BattleShip.ShipNames[getLength() - 1];
        }
        
        return "!fail";
    }

    public final String getFullName() {
        if(!isFail()) {
            return (getLength() == 1) ? 
                    String.format("%1$s [%2$s]", BattleShip.ShipNames[getLength() - 1], m_ccs[0].toString()) :
                    String.format("%1$s [%2$s]->[%3$s]", 
                            BattleShip.ShipNames[getLength() - 1], 
                            m_ccs[0].toString(), m_ccs[m_ccs.length - 1].toString());
        }
        
        return "!fail";
    }
    
    public final CellCords[] getCords() {
        return m_ccs;
    }
    
    public static int getLetgth(CellCords cc1, CellCords cc2) {
        int _length = BattleShip.lengthFail;
        CellCords _cc1 = cc1, _cc2 = cc2;
        
        if((cc1.getX() > cc2.getX()) || 
                (cc1.getY() > cc2.getY())) {
            _cc1 = cc2;
            _cc2 = cc1;
        }
        
        if(_cc1.getX() == _cc2.getX()) {
            _length = Math.abs(_cc1.getY() - _cc2.getY());
        }
        else {
            if(_cc1.getY() == _cc2.getY()) {
                _length = Math.abs(_cc1.getX() - _cc2.getX());
            }
        }
        
        if((_length > -1) && (_length < 4)) {
            _length += 1;
        }
        else {
            _length = BattleShip.lengthFail;
        }
        
        return _length;
    }
    
    public static CellCords[] getAllCellCords(CellCords cc1, CellCords cc2) {
        int _length = BattleShip.getLetgth(cc1, cc2);
        
        if(_length != BattleShip.lengthFail) {
            CellCords[] _ccs = new CellCords[_length];
            CellCords _cc1 = cc1, _cc2 = cc2;

            if((cc1.getX() > cc2.getX()) || 
                    (cc1.getY() > cc2.getY())) {
                _cc1 = cc2;
                _cc2 = cc1;
            }
            
            for(int i = 0; i < _length; ++i) {
                if(_cc1.getX() == _cc2.getX()) {
                    _ccs[i] = new CellCords(_cc1.getX(), _cc1.getY() + i);
                }
                else {
                    _ccs[i] = new CellCords(_cc1.getX() + i, _cc1.getY());
                }
            }
            
            return _ccs;
        }
        
        return null;
    }
    
    public static boolean validateCords(CellCords cc1, CellCords cc2) {
        return (BattleShip.getLetgth(cc1, cc2) != BattleShip.lengthFail);
    }
    
    @Override
    public String toString() {
        return getFullName();
    }
}
