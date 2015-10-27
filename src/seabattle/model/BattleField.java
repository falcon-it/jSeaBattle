/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.model;

import java.awt.Color;
import java.awt.Font;

public final class BattleField {
    public static final Color gridBackgroundColor, gridLabelColor;
    
    public static Color correctiveColor(Color srcColor, Color bgColor) {
        int _rc = srcColor.getRed(),
                _gc = srcColor.getGreen(),
                _bc = srcColor.getBlue();
        
        if(java.lang.Math.abs(_rc + _gc + _bc - bgColor.getRed() - bgColor.getGreen() - bgColor.getBlue()) < 15) {
            if(java.lang.Math.abs(_rc - bgColor.getRed()) < 10) {
                int __rc = _rc + 10;
                _rc = (__rc > 255) ? _rc - 10 : __rc;
            }
            if(java.lang.Math.abs(_gc - bgColor.getGreen()) < 10) {
                int __gc = _gc + 10;
                _gc = (__gc > 255) ? _gc - 10 : __gc;
            }
            if(java.lang.Math.abs(_bc - bgColor.getBlue()) < 10) {
                int __bc = _bc + 10;
                _bc = (__bc > 255) ? _bc - 10 : __bc;
            }
        }
        
        return new Color(_rc, _gc, _bc);
    }
    
    public static String formatCords() {
        return String.format("[%1$s]", CellCords.defaultErrorCordsString);
    }
    
    public static String formatCords(String c) {
        return String.format("[%1$s]", c);
    }

    public static String formatCords(String c1, String c2) {
        return String.format("[%1$s, %2$s]", c1, c2);
    }
    
    static {
        gridBackgroundColor = BattleField.correctiveColor(Color.white, 
                new Color(javax.swing.UIManager.getDefaults().getColor("control").getRGB()));
        gridLabelColor = BattleField.correctiveColor(Color.blue, 
                new Color(javax.swing.UIManager.getDefaults().getColor("control").getRGB()));
    }
    
    public enum CellState {
        error,//????????????
        empty,//пусто
        ship,//корабль
        shot,//выстрел
        bloomer,//промах
        hit,//подбит
        killed,//убит
        emptiness//пустота вокруг подбитого корабля
    }
    
    public final CellState[][] m_state;
    public final javax.swing.DefaultListModel<BattleShip> shipList;
    
    public BattleField() {
        m_state = new CellState[10][10];
        shipList = new javax.swing.DefaultListModel<BattleShip>();
        eraseCells();
    }
    
    public void eraseCells() {
        for(int i = 0; i < 10; ++i) {
            for(int j = 0; j < 10; ++j) {
               m_state[i][j] = CellState.empty;
            }
        }
        
        shipList.removeAllElements();
    }
    
    public CellState getCellState(int row, int column) {
        if((row < 0) ||
                (row > 9) ||
                (column < 0) ||
                (column > 9)) {
            return CellState.error;
        }
        
        return m_state[row][column];
    }
    
    public void setCellState(int row, int column, CellState state) {
        if((row > -1) ||
                (row < 10) ||
                (column > -1) ||
                (column < 10)) {
            m_state[row][column] = state;
        }
    }
    
    public static int getTitleOffset(java.awt.Rectangle rect) {
        return rect.width / 15;
    }
    
    public static double getGridSpace(java.awt.Rectangle rect) {
        return ((double)(rect.width - BattleField.getTitleOffset(rect))) / 120;
    }
    
    public static double getGridCellSide(java.awt.Rectangle rect) {
        return ((double)(rect.width - BattleField.getTitleOffset(rect) - (9 * BattleField.getGridSpace(rect))) / 10);
    }
    
    public static double getGridCellRound(java.awt.Rectangle rect) {
        return BattleField.getGridCellSide(rect) / 5;
    }
    
    public static boolean isGridCellCordsFail(java.awt.Point p) {
        return ((p.x == -1) || (p.y == -1));
    }
    
    public static java.awt.Point getGridCellCoords(java.awt.Rectangle rect, int i, int j) {
        if((i < 0) || (i > 9) || (j < 0) || (j > 9)) {
            return new java.awt.Point(-1, -1);
        }
        
        int _lo = BattleField.getTitleOffset(rect);
        double _bsp = BattleField.getGridSpace(rect);
        double _side = BattleField.getGridCellSide(rect);
        
        return new java.awt.Point((int)(i * (_side + _bsp) + rect.x + _lo), (int)(j * (_side + _bsp) + rect.y + _lo));
    }
    
    public static CellCords getGridCellOnCords(java.awt.Rectangle rect, int cx, int cy) {
        int _lo = BattleField.getTitleOffset(rect);
        double _bsp = BattleField.getGridSpace(rect);
        double _side = BattleField.getGridCellSide(rect);
        
        if((cx >= (_lo + rect.x)) && 
                (cy >= (_lo + rect.y)) &&
                (cx < (_lo + rect.x + _side * 10 + _bsp * 9)) &&
                (cy < (_lo + rect.y + _side * 10 + _bsp * 9))) {
            
            int _x = (int)((cx - _lo - rect.x + _bsp) / (_side + _bsp));
            int _lim_min_x = (int)(_lo + rect.x + _x * (_side + _bsp));
            int _y = (int)((cy - _lo - rect.y + _bsp) / (_side + _bsp));
            int _lim_min_y = (int)(_lo + rect.y + _y * (_side + _bsp));
            
            if((cx >= _lim_min_x) && 
                    (cx <= (_lim_min_x + (int)_side)) &&
                    (cy >= _lim_min_y) && 
                    (cy <= (_lim_min_y + (int)_side))) {
                return new CellCords(_x, _y);
            }
        }
        
        return new CellCords(-1, -1);
    }
    
    public final boolean validateStartPosition(CellCords cc) {
        if(!cc.isFail()) {
            int _column1 = cc.getX() - 1,
                    _column2 = cc.getX() + 1,
                    _row1 = cc.getY() - 1,
                    _row2 = cc.getY() + 1;
            
            if(_column1 < 0) {
                _column1 = 0;
            }
            
            if(_column2 > 9) {
                _column2 = 9;
            }

            if(_row1 < 0) {
                _row1 = 0;
            }
            
            if(_row2 > 9) {
                _row2 = 9;
            }
            
            for(int i = _column1; i <= _column2; ++i) {
                for(int j = _row1; j <= _row2; ++j) {
                    if(getCellState(i, j) != CellState.empty) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    public void paintFieldState(java.awt.Graphics g, java.awt.Rectangle rect) {
        Color _old_color = g.getColor();
        Font _old_font = g.getFont();
        
        //m_state[0][0] = CellState.ship;
        
        /*m_state[0][0] = CellState.bloomer;
        m_state[0][1] = CellState.emptiness;
        m_state[0][2] = CellState.empty;
        m_state[0][3] = CellState.error;
        m_state[0][4] = CellState.hit;
        m_state[0][5] = CellState.killed;
        m_state[0][6] = CellState.ship;
        m_state[0][7] = CellState.shot;*/
        
        int _lo = BattleField.getTitleOffset(rect);
        double _bsp = BattleField.getGridSpace(rect);
        double _side = BattleField.getGridCellSide(rect);
        double _round = BattleField.getGridCellRound(rect);
        
        Font font = new Font("Verdana", Font.PLAIN, (int)(_lo * 0.7));
        g.setFont(font);
        
        for(int i = 0; i < 10; ++i) {
            g.setColor(BattleField.gridLabelColor);
            g.drawString(CellCords.gridLabel[i], (int)(i * (_side + _bsp) + _side / 3 + rect.x + _lo), rect.y + _lo * 2 / 3);
            g.drawString(Integer.toString(i + 1), rect.x + _lo / 30, (int)(i * (_side + _bsp) + _side / 1.5 + rect.y + _lo));
            
            for(int j = 0; j < 10; ++j) {
                int _x = (int)(i * (_side + _bsp) + rect.x + _lo),
                        _y = (int)(j * (_side + _bsp) + rect.y + _lo);
                
                switch(m_state[i][j]) {
                    case error:
                        g.setColor(Color.RED);
                        g.drawRoundRect(_x, _y, (int)_side, (int)_side, (int)_round, (int)_round);
                        g.drawString("!", (int)(_x + _side * 5 / 11), (int)(_y + _side / 1.5));
                        break;
                    case empty:
                        g.setColor(BattleField.gridBackgroundColor);
                        g.fillRoundRect(_x, _y, (int)_side, (int)_side, (int)_round, (int)_round);
                        break;
                    case ship:
                        g.setColor(Color.BLUE);
                        g.fillRoundRect(_x, _y, (int)_side, (int)_side, (int)_round, (int)_round);
                        break;
                    case shot:
                        g.setColor(Color.RED);
                        g.fillOval(_x + (int)(_side / 4), _y + (int)(_side / 4), (int)(_side / 2), (int)(_side / 2));
                        break;
                    case bloomer:
                        g.setColor(Color.RED);
                        g.drawOval(_x + (int)(_side / 4), _y + (int)(_side / 4), (int)(_side / 2), (int)(_side / 2));
                        g.setColor(Color.WHITE);
                        g.fillOval(_x + (int)(_side * 3 / 8), _y + (int)(_side * 3 / 8), (int)(_side / 4), (int)(_side / 4));
                        break;
                    case hit:
                        g.setColor(Color.BLUE);
                        g.fillRoundRect(_x, _y, (int)_side, (int)_side, (int)_round, (int)_round);
                        g.setColor(Color.RED);
                        g.fillOval(_x + (int)(_side / 4), _y + (int)(_side / 4), (int)(_side / 2), (int)(_side / 2));
                        break;
                    case killed:
                        g.setColor(Color.RED);
                        g.fillRoundRect(_x, _y, (int)_side, (int)_side, (int)_round, (int)_round);
                        break;
                    case emptiness:
                        g.setColor(Color.WHITE);
                        g.drawOval(_x + (int)(_side / 4), _y + (int)(_side / 4), (int)(_side / 2), (int)(_side / 2));
                        break;
                }
                
                
            }
        }
        
        g.setColor(_old_color);
        g.setFont(_old_font);
    }
}

