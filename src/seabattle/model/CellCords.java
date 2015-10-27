/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.model;

public class CellCords {
    public static final String[] gridLabel = 
            new String[] {"А", "Б", "В", "Г", "Д", "Е", "Ё", "Ж", "З", "И"};
    public static final String defaultErrorCordsString = "XX",
            defaultCordsSplit = "-";
    
    private int m_x, m_y;
    
    public CellCords() {
        m_x = -1;
        m_y = -1;
    }
    
    public CellCords(int x, int y) {
        m_x = x;
        m_y = y;
    }
    
    public CellCords(String cords) {
        this();
        
        String[] _cc = cords.split(CellCords.defaultCordsSplit);
        
        if(_cc.length == 2) {
            for(int i = 0; i < CellCords.gridLabel.length; ++i) {
                if(CellCords.gridLabel[i].equals(_cc[0])) {
                    m_x = i;
                    break;
                }
            }
            
            try {
                m_y = Integer.parseInt(_cc[1]) - 1;
            }
            catch(Exception e) {
                
            }
        }
    }
    
    public boolean isFail() {
        return ((m_x < 0) || (m_x > 9) || (m_y < 0) || (m_y > 9));
    }
    
    public int getX() {
        return m_x;
    }
    
    public int getY() {
        return m_y;
    }
    
    @Override
    public String toString() {
        return !isFail() ? 
                String.format("%1$s%3$s%2$d", CellCords.gridLabel[m_x], m_y + 1, 
                        CellCords.defaultCordsSplit) : 
                CellCords.defaultErrorCordsString;
    }
    
    public static boolean isEquallyCords(CellCords cc1, CellCords cc2) {
        return ((cc1.getX() == cc2.getX()) && (cc1.getY() == cc2.getY()));
    }
}
