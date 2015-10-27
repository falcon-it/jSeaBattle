/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.gui;

import java.awt.Color;

public final class StartInitializer {
    private static final java.awt.image.BufferedImage logo;
    
    static {
        java.awt.image.BufferedImage _logo = null;

        try {
            _logo = javax.imageio.ImageIO.read(
                    StartInitializer.class.getResource("logo.png"));
        }
        catch(Exception e) {
            
        }
        
        logo = _logo;
    }
    
    private StartInitializer() {}
    
    public static void Initialize(seabattle.gui.PaintPanel viewPanel) {
        viewPanel.setPainter((java.awt.Graphics g) -> {
            if(StartInitializer.logo != null) {
                g.drawImage(StartInitializer.logo, 0, 0, viewPanel);
            }
            
            java.awt.Font f = g.getFont();
            java.awt.Font font = new java.awt.Font("Verdana", java.awt.Font.PLAIN, f.getSize() * 5);
            g.setFont(font);

            g.drawString("Морской бой!",
                            ((StartInitializer.logo != null) ? StartInitializer.logo.getWidth() : 0) + 20, 100);
            
            g.setFont(f);
        });
    }
}
