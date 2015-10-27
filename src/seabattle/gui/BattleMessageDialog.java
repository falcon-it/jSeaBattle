/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.gui;

import java.awt.*;
import java.awt.event.*;
import seabattle.net.*;
import javax.swing.*;
import java.util.LinkedList;
import java.util.Date;

public final class BattleMessageDialog extends JDialog {
    public static abstract class MessageSender {
        protected abstract void _sendMessage(FormatMessage msg);
        
        public void sendMessage(String msg) {
            _sendMessage(new FormatMessage(FormatMessage.Command.SEND_MESSAGE, msg));
        }
    }
    
    private static final class MessageItem {
        private enum whose {
            my,
            enemy
        }
        
        public final String message;
        public final whose type;
        public final Date date;
        
        public MessageItem(String _message, whose _type) {
            message = _message;
            type = _type;
            date = new Date();
        }
    }
    
    private final MessageSender m_messager;
    private final JEditorPane m_messageLog;
    private final LinkedList<MessageItem> m_messageList;
    
    public BattleMessageDialog(MessageSender messager) {
        super((JFrame)null, "Пэйджер", true);

        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        
        m_messager = messager;
        m_messageList = new LinkedList<MessageItem>();
        
        Toolkit t = Toolkit.getDefaultToolkit();
        Dimension ss = t.getScreenSize();
        setSize((int)(ss.getWidth() / 3), (int)(ss.getHeight() / 2));
        setLocation((int)(ss.getWidth() / 2), (int)(ss.getHeight() / 4));
        setMinimumSize(new Dimension((int)ss.getWidth() / 6, (int)ss.getHeight() / 4));
        
        setLayout(new BorderLayout());
        
        JPanel _c = new JPanel();
        getContentPane().add(_c, BorderLayout.CENTER);
        _c.setBorder(new javax.swing.border.EmptyBorder(5, 5, 5, 5));
        _c.setLayout(new BorderLayout());
        
        JPanel _editorPanel = new JPanel();
        _editorPanel.setLayout(new BorderLayout());
        
        JTextArea _textField = new JTextArea();
        _textField.setRows(5);
        JScrollPane __textFieldScroll = new JScrollPane(_textField);
        _editorPanel.add(__textFieldScroll, BorderLayout.CENTER);
        
        JButton _sendButton = new JButton("Отправить");
        _sendButton.addActionListener(
            (ActionEvent e) -> { 
                String _s = _textField.getText();
                if((_s != null) && (_s.length() > 0)) {
                    _s = _s.replaceAll("\n", "<br />");
                    m_messager.sendMessage(_s);
                    addMessage(_s, MessageItem.whose.my);
                }
                _textField.setText("");
            }
        );
        _editorPanel.add(_sendButton, BorderLayout.EAST);
        
        _c.add(_editorPanel, BorderLayout.SOUTH);
        
        m_messageLog = new JEditorPane();
        m_messageLog.setEditable(false);
        m_messageLog.setBackground(new Color(218, 218, 218));
        m_messageLog.setContentType("text/html");
        JScrollPane _allMsgFieldScroll = new JScrollPane(m_messageLog);
        _c.add(_allMsgFieldScroll, BorderLayout.CENTER);
        
        revalidate();
    }
    
    protected void addMessage(String _msg, MessageItem.whose type) {
        if((_msg != null) || (_msg.length() > 0)) {
            m_messageList.add(new MessageItem(_msg, type));
            
            StringBuilder _result = new StringBuilder();
            for(int i = 0; i < m_messageList.size(); ++i) {
                MessageItem _it = m_messageList.get(i);
                
                _result.append("<div hspace=\"4\" vspace=\"4\" bgcolor=\"");
                switch(_it.type) {
                    case my:
                        _result.append("yellow");
                        break;
                    case enemy:
                        _result.append("silver");
                        break;
                }
                _result.append("\"><div color=\"navy\" hspace=\"4\" vspace=\"4\">");
                _result.append(_it.date.toString());
                _result.append("</div><pre bgcolor=\"");
                
                switch(_it.type) {
                    case my:
                        _result.append("red");
                        break;
                    case enemy:
                        _result.append("blue");
                        break;
                }

                _result.append("\" color=\"");
                
                switch(_it.type) {
                    case my:
                        _result.append("lime");
                        break;
                    case enemy:
                        _result.append("white");
                        break;
                }

                _result.append("\" hspace=\"4\" vspace=\"4\">");
                _result.append(_it.message);
                _result.append("</pre></div>");
            }

            m_messageLog.setText(_result.toString());
        }
    }
    
    public void addMessage(String _msg) {
        addMessage(_msg, MessageItem.whose.enemy);
    }
}
