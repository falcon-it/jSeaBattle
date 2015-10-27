/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.model;

import java.awt.Color;
import java.awt.Font;

public final class ConstructBattleField {
    public static final Color selectCurColor, selectCurFailColor, currentCellColor;
    
    static {
        selectCurColor = BattleField.correctiveColor(Color.YELLOW, 
                new Color(javax.swing.UIManager.getDefaults().getColor("control").getRGB()));
        currentCellColor = BattleField.correctiveColor(Color.GREEN, 
                new Color(javax.swing.UIManager.getDefaults().getColor("control").getRGB()));
        selectCurFailColor = BattleField.correctiveColor(Color.RED, 
                new Color(javax.swing.UIManager.getDefaults().getColor("control").getRGB()));
    }
    
    private final BattleField m_battleField;
    private final javax.swing.DefaultListModel<BattleShip> m_model;
    private final seabattle.gui.ConstructInitializer.IConstructionStatus m_StatusEventInfo;
    
    public enum State {
        idle,
        set_start,
        set_start_fail
    }
    
    private State m_State;
    private CellCords m_StartCords, m_CurCell;
    
    public ConstructBattleField(BattleField bf, 
            javax.swing.DefaultListModel<BattleShip> model, 
            seabattle.gui.ConstructInitializer.IConstructionStatus cb) {
        m_battleField = bf;
        m_battleField.eraseCells();
        m_State = State.idle;
        m_CurCell = new CellCords();
        m_model = model;
        m_StatusEventInfo = cb;
    }
    
    public final State getState() {
        return m_State;
    }
    
    private final boolean checkNewShipLength(int newShipLength) {
        boolean _valid_insert = true;
        
        int _ship_class_count = 0;
        for(int i = 0; i < m_model.getSize(); ++i) {
            BattleShip item = m_model.getElementAt(i);
            if(item.getLength() == newShipLength) {
                ++_ship_class_count;
            }
        }

        switch(newShipLength) {
            case 4:
                if(_ship_class_count >= 1) {
                    _valid_insert = false;
                }
                break;
            case 3:
                if(_ship_class_count >= 2) {
                    _valid_insert = false;
                }
                break;
            case 2:
                if(_ship_class_count >= 3) {
                    _valid_insert = false;
                }
                break;
            case 1:
                if(_ship_class_count >= 4) {
                    _valid_insert = false;
                }
                break;
            default:
                _valid_insert = false;
                break;
        }
        
        return _valid_insert;
    }
    
    public final void paintFieldState(java.awt.Graphics g, java.awt.Rectangle rect) {
        m_battleField.paintFieldState(g, rect);
        
        int _side = 0;
        int _round = 0;
        
        if(!m_CurCell.isFail() || 
                (m_State == State.set_start) || 
                (m_State == State.set_start_fail)) {
            _side = (int)BattleField.getGridCellSide(rect);
            _round = (int)BattleField.getGridCellRound(rect);
        }

        Color _old_color = g.getColor();
        Font _old_font = g.getFont();
        
        
        
        switch(m_State) {
            case set_start:
                if(!m_CurCell.isFail()) {
                    int _length = BattleShip.getLetgth(m_StartCords, m_CurCell);
                    if(checkNewShipLength(_length)) {
                        CellCords[] _ccs = BattleShip.getAllCellCords(m_StartCords, m_CurCell);
                        if(_ccs != null) {
                            for(CellCords _ccc : _ccs) {
                                java.awt.Point _p = BattleField.getGridCellCoords(rect, 
                                        _ccc.getX(), _ccc.getY());
                                g.setColor(ConstructBattleField.selectCurColor);
                                g.fillRoundRect(_p.x, _p.y, _side, _side, _round, _round);
                            }
                        }
                    }
                }
                else {
                    java.awt.Point _p = BattleField.getGridCellCoords(rect, 
                            m_StartCords.getX(), m_StartCords.getY());
                    g.setColor(ConstructBattleField.selectCurColor);
                    g.fillRoundRect(_p.x, _p.y, _side, _side, _round, _round);
                }
                break;
            case set_start_fail:
                java.awt.Point _p = BattleField.getGridCellCoords(rect, 
                        m_StartCords.getX(), m_StartCords.getY());
                g.setColor(ConstructBattleField.selectCurFailColor);
                g.fillRoundRect(_p.x, _p.y, _side, _side, _round, _round);
                break;
        }
        
        
        if(!m_CurCell.isFail()) {
            java.awt.Point _pc = 
                    BattleField.getGridCellCoords(rect, 
                            m_CurCell.getX(), m_CurCell.getY());
            java.awt.Graphics2D _g2d = (java.awt.Graphics2D)g;
            java.awt.Stroke _old_s = _g2d.getStroke();
            _g2d.setStroke(new java.awt.BasicStroke((int)_round / 2));
            g.setColor(ConstructBattleField.currentCellColor);
            g.drawRoundRect(_pc.x, _pc.y, _side, _side, _round, _round);
            _g2d.setStroke(_old_s);
        }
        
        g.setColor(_old_color);
        g.setFont(_old_font);
    }
    
    public final String getStateText() {
        return getStateText(new CellCords());
    }
    
    private final void sortShipsList() {
        if(m_model.getSize() > 1) {
            BattleShip[] _ships = new BattleShip[m_model.getSize()];
            int _c_pos = 0;
            
            for(int l = 1; l <= 4; ++l) {
                for(int i = 0; i < m_model.getSize(); ++i) {
                    BattleShip item = m_model.getElementAt(i);
                    if(item.getLength() == l) {
                        _ships[_c_pos] = item;
                        ++_c_pos;
                    }
                }
            }
            
            m_model.clear();
            
            for(BattleShip item : _ships) {
                m_model.addElement(item);
            }
        }
        
        switch(m_model.getSize()) {
            case 10:
                m_StatusEventInfo.completed();
                break;
            default:
                m_StatusEventInfo.extend();
                break;
        }
    }
    
    public final String getStateText(CellCords ccc) {
        String _state = "";
        
        switch(m_State) {
            case idle:
                _state = BattleField.formatCords(ccc.toString());
                break;
            case set_start:
                _state = String.format("%1$s->%2$s", 
                        BattleField.formatCords(m_StartCords.toString()), 
                        BattleField.formatCords(ccc.toString()));
                break;
            case set_start_fail:
                _state = String.format("!%1$s->%2$s", 
                        BattleField.formatCords(m_StartCords.toString()), 
                        BattleField.formatCords(ccc.toString()));
                break;
        }
        
        return _state;
    }

    public final void setPosition(CellCords cc) {
        switch(m_State) {
            case idle:
                if(!cc.isFail()) {
                    m_StartCords = cc;
                    m_State = m_battleField.validateStartPosition(cc) ? State.set_start : State.set_start_fail;
                }
                break;
            case set_start:
                if(!cc.isFail()) {
                    BattleShip _new_battle_ship = new BattleShip(m_StartCords, cc);
                    if(!_new_battle_ship.isFail()) {
                        boolean _valid_insert = true;
                        for(CellCords _ccc : _new_battle_ship.getCords()) {
                            if(!m_battleField.validateStartPosition(_ccc)) {
                                _valid_insert = false;
                                break;
                            }
                        }

                        if(_valid_insert) {
                            _valid_insert = checkNewShipLength(_new_battle_ship.getLength());

                            if(_valid_insert) {
                                m_model.addElement(_new_battle_ship);

                                for(CellCords _ccc : _new_battle_ship.getCords()) {
                                    m_battleField.setCellState(_ccc.getX(), _ccc.getY(), BattleField.CellState.ship);
                                }
                                
                                sortShipsList();
                            }
                        }
                    }
                }
                
                m_State = State.idle;
                break;
            case set_start_fail:
                m_State = State.idle;
                break;
        }
    }
    
    public final void setCurrentPosition(CellCords cc) {
        m_CurCell = cc;
    }
    
    public final void deleteShipByIndex(int index) {
        if((index > -1) && (index < m_model.getSize())) {
            for(CellCords cc : m_model.getElementAt(index).getCords()) {
                m_battleField.setCellState(cc.getX(), cc.getY(), BattleField.CellState.empty);
            }
            
            m_model.remove(index);
        }
    }
}
