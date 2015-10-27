/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.net;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class FormatMessage {
    public static class IllegalMessageException extends Exception {
        
    }
    
    public enum CommandFiels {
        COMMAND,
        BODY,
        ID
    }
    
    public enum Command {
        /*
        * начало игры
        * бросаем жребий, кто первый ходит
        * в body число 0...1000000000
        * у кого больше, тот и ходит
        */
        TRY_YOUR_LUCK,
        /*
        * текстовое сообщение
        */
        SEND_MESSAGE,
        /*
        * синхронизация полей
        * в body массив с состояние полей
        */
        //QSY,
        //ASY,
        /*
        * выстрел
        * в body координата выстрела
        * в ответе состояние поля
        */
        FIRE,
        FIRE_MISS,//промах
        FIRE_WOUNDED,//попал - координаты точки
        FIRE_KILLED,//убит - массив координат корабля
        BATTLE_DEFEAD,//проиграг - конец всем кораблям
        /*
        * окночание битвы
        * запрос отпраляет проигравший
        */
        QBE,
        ABE
    }    
    
    public static final String escape = String.format("%1$c", (char)31),//,"\\r58gd5", 
            split1ch = "#", 
            split2ch = FormatMessage.split1ch + FormatMessage.split1ch;
    
    public Command command;
    public String body;
    public int id;
    
    public FormatMessage(Command command, String body, int id) {
        this.command = command;
        this.body = body;
        this.id = id;
    }
    
    public FormatMessage(FormatMessage fm, int id) {
        this(fm.command, fm.body, id);
    }
    
    public FormatMessage(Command c, String body) {
        this(c, body, RouterClientSetting.failID);
    }
    
    public FormatMessage(byte[] binMessage) throws IllegalMessageException {
        id = RouterClientSetting.failID;
        
        try {
            String _m = new String(binMessage, StandardCharsets.UTF_8);
            String[] _items = _m.split(FormatMessage.split2ch);
            for(String _i : _items) {
                String[] _val = _i.split(FormatMessage.split1ch);
                switch(CommandFiels.valueOf(_val[0])) {
                    case COMMAND:
                        command = Command.valueOf(_val[1]);
                        break;
                    case BODY:
                        body = _val[1].replaceAll(FormatMessage.escape, FormatMessage.split1ch);
                        break;
                    case ID:
                        id = Integer.valueOf(_val[1]);
                        break;
                }
            }
            
            if((body == null) || (body.length() == 0) || (command == null)) {
                throw new IllegalMessageException();
            }
        }
        catch(IllegalMessageException e) {
            throw e;
        }
        catch(Exception e) {
            throw new IllegalMessageException();
        }
    }
    
    @Override
    public String toString() {
        return ((id == RouterClientSetting.failID) ? 
                String.format("%1$s#%2$s##%3$s#%4$s", 
                        FormatMessage.CommandFiels.COMMAND, command, 
                        FormatMessage.CommandFiels.BODY, body) : 
                String.format("%1$s#%2$s##%3$s#%4$s##%5$s#%6$s", 
                        FormatMessage.CommandFiels.COMMAND, command, 
                        FormatMessage.CommandFiels.BODY, 
                        body.replaceAll(FormatMessage.split1ch, 
                                FormatMessage.escape), 
                        FormatMessage.CommandFiels.ID, id));
    }
    
    public final byte[] toBinaryMessage() {
        return toString().getBytes(StandardCharsets.UTF_8);
    }
}
