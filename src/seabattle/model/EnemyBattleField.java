/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.model;

import java.awt.Color;

public final class EnemyBattleField {
    public final BattleField enemyBattleField;
    
    public enum FieldState {
        waitConnection,
        setShotCoords,
        waitShotResult,
        waitEnemyShot
    }
    
    private FieldState m_State;
    private CellCords m_currentCell, m_downCell;
    
    public EnemyBattleField(BattleField enemy) {
        enemyBattleField = enemy;
        m_State = FieldState.waitConnection;
        m_currentCell = new CellCords();
        m_downCell = new CellCords();
    }

    public final void paintFieldState(java.awt.Graphics g, java.awt.Rectangle rect) {
        enemyBattleField.paintFieldState(g, rect);
        
        switch(m_State) {
            case waitConnection:
                Color _old_cl = g.getColor();
                g.setColor(new Color(Color.lightGray.getRed(), 
                        Color.lightGray.getGreen(), Color.lightGray.getBlue(), 150));
                g.fillRect(rect.x, rect.y, rect.width, rect.height);
                g.setColor(_old_cl);
                break;
            case setShotCoords:
                if(!m_currentCell.isFail() || 
                        !m_downCell.isFail()) {
                    java.awt.Graphics2D _g2d = (java.awt.Graphics2D)g;
                    Color _old_1cl = g.getColor();
                    java.awt.Stroke _old_s = _g2d.getStroke();
                    double _s = BattleField.getGridCellSide(rect);
                    double _r = BattleField.getGridCellRound(rect);
                    java.awt.Point _p = null;
                    _g2d.setStroke(new java.awt.BasicStroke((int)_r / 2));
                    
                    if(!m_currentCell.isFail()) {
                        g.setColor(ConstructBattleField.currentCellColor);
                        _p = BattleField.getGridCellCoords(
                            rect, m_currentCell.getX(), m_currentCell.getY());
                        g.drawRoundRect((int)_p.x, (int)_p.y, 
                                (int)_s, (int)_s, (int)_r, (int)_r);
                    }
                    
                    if(!m_downCell.isFail()) {
                        _g2d.setStroke(new java.awt.BasicStroke((int)_r / 4));
                        g.setColor(Color.RED);
                        _p = BattleField.getGridCellCoords(
                                rect, m_downCell.getX(), m_downCell.getY());
                        
                        g.drawLine((int)(_p.x + _r), (int)(_p.y + _s / 2), 
                                (int)(_p.x + _s - _r), (int)(_p.y + _s / 2));
                        g.drawLine((int)(_p.x + _s / 2), (int)(_p.y + _r), 
                                (int)(_p.x + _s / 2), (int)(_p.y + _s - _r));
                        
                        if(CellCords.isEquallyCords(m_currentCell, m_downCell)) {
                            g.setColor(Color.BLUE);
                            g.drawOval((int)(_p.x + _r * 1.2), (int)(_p.y + _r * 1.2), 
                                    (int)(_s - 2.4 * _r), (int)(_s - 2.4 * _r));
                            g.setColor(Color.BLACK);
                            g.fillOval((int)(_p.x + _r * 2.0), (int)(_p.y + _r * 2.0), 
                                    (int)(_s - 4.0 * _r), (int)(_s - 4.0 * _r));
                        }
                        else {
                            g.drawOval((int)(_p.x + _r * 1.2), (int)(_p.y + _r * 1.2), 
                                    (int)(_s - 2.4 * _r), (int)(_s - 2.4 * _r));
                            g.fillOval((int)(_p.x + _r * 2.0), (int)(_p.y + _r * 2.0), 
                                    (int)(_s - 4.0 * _r), (int)(_s - 4.0 * _r));
                        }
                        
                    }
                    
                    g.setColor(_old_1cl);
                    _g2d.setStroke(_old_s);
                }
                break;
            case waitEnemyShot:
                //ничего не делать
                break;
        }
    }
    
    public final FieldState getState() {
        return m_State;
    }

    public final void setState(FieldState s) {
        m_State = s;
    }
    
    public final CellCords getCurrentCell() {
        return m_currentCell;
    }
    
    public final void setCurrentCell(CellCords cell) {
        m_currentCell = cell;
    }
    
    public final CellCords getDownCell() {
        return m_downCell;
    }
    
    public final void setDownCell(CellCords cell) {
        m_downCell = cell;
    }
    
    public final String getInputState() {
        if(!m_downCell.isFail()) {
            return (!m_currentCell.isFail() && 
                    CellCords.isEquallyCords(m_currentCell, m_downCell)) ? 
                        String.format("{->%1$s}", BattleField.formatCords(m_downCell.toString())) :
                    String.format("{->%1$s}-%2$s", 
                            BattleField.formatCords(m_downCell.toString()), 
                            BattleField.formatCords(m_currentCell.toString()));
        }
        
        return BattleField.formatCords(m_currentCell.toString());
    }
    
    public final void markFireResult(
            CellCords mcc, 
            MyBattleField.ShotResult shotResult,
            javax.swing.DefaultListModel<BattleShipExEnemy> enemyShipList) {
        if(shotResult == MyBattleField.ShotResult.miss) {
            enemyBattleField.setCellState(
                    mcc.getX(), mcc.getY(), 
                    BattleField.CellState.bloomer);
            return;
        }
        
        int _neighbor1_idx = -1,
                _neighbor2_idx = -1;
        
        for(int i = 0; i < enemyShipList.size(); ++i) {
            if(enemyShipList.get(i).isHitThisShip(mcc)) {
                if(_neighbor1_idx == -1) {
                    _neighbor1_idx = i;
                    continue;
                }

                if(_neighbor2_idx == -1) {
                    _neighbor2_idx = i;
                    break;
                }
            }
        }
        
        if(_neighbor1_idx != -1) {
            if(_neighbor2_idx != -1) {
                enemyShipList.get(
                        _neighbor1_idx).includeHit(
                                mcc, 
                                shotResult, 
                                enemyShipList.get(_neighbor2_idx));
                enemyShipList.remove(_neighbor2_idx);
            }
            else {
                enemyShipList.get(_neighbor1_idx).includeHit(mcc, shotResult);
            }
            
            enemyShipList.get(
                    _neighbor1_idx).updateBattleFieldState(
                            enemyBattleField);
        }
        else {
            BattleShipExEnemy _new_enemy_ship = 
                    new BattleShipExEnemy(
                            mcc, 
                            shotResult == MyBattleField.ShotResult.killed);
            enemyShipList.addElement(_new_enemy_ship);
            _new_enemy_ship.updateBattleFieldState(enemyBattleField);
        }
    }
}
