/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seabattle.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import seabattle.net.*;

public final class NetAddressInitializer {
    private NetAddressInitializer() {}
    
    public static final class NetAddressValidatorState {
        public interface IValidator {
            boolean validate();
        }
        
        private IValidator m_Validator;
        
        public boolean isValid() {
            return m_Validator != null ? m_Validator.validate() : false;
        }
        
        public void setValidSate(IValidator v) {
            m_Validator = v;
        }
    }
    
    public static void Initialize(seabattle.gui.PaintPanel viewPanel, 
            seabattle.net.Connection conn, NetAddressValidatorState v) {
        viewPanel.setLayout(new GridBagLayout());
        
        Insets _is1 = new Insets(2, 2, 0, 2),
                _is2 = new Insets(0, 2, 2, 2);
        
        //углы
        GridBagConstraints _cc = new GridBagConstraints();
        _cc.gridx = 0;
        _cc.gridy = 0;
        _cc.weightx = 100;
        _cc.weighty = 100;
        viewPanel.add(new JPanel(), _cc);
        
        _cc.gridx = 2;
        _cc.gridy = 0;
        _cc.weightx = 100;
        _cc.weighty = 100;
        viewPanel.add(new JPanel(), _cc);
        
        _cc.gridx = 2;
        _cc.gridy = 2;
        _cc.weightx = 100;
        _cc.weighty = 100;
        viewPanel.add(new JPanel(), _cc);
        
        _cc.gridx = 0;
        _cc.gridy = 2;
        _cc.weightx = 100;
        _cc.weighty = 100;
        viewPanel.add(new JPanel(), _cc);
        
        //верх низ
        _cc.gridx = 1;
        _cc.gridy = 0;
        _cc.weightx = 0;
        _cc.weighty = 100;
        viewPanel.add(new JPanel(), _cc);

        _cc.gridx = 1;
        _cc.gridy = 2;
        _cc.weightx = 0;
        _cc.weighty = 100;
        viewPanel.add(new JPanel(), _cc);
        
        //бока
        _cc.gridx = 0;
        _cc.gridy = 1;
        _cc.weightx = 100;
        _cc.weighty = 0;
        viewPanel.add(new JPanel(), _cc);

        _cc.gridx = 2;
        _cc.gridy = 1;
        _cc.weightx = 100;
        _cc.weighty = 0;
        viewPanel.add(new JPanel(), _cc);
        
        JPanel _content = new JPanel();
        _cc.gridx = 1;
        _cc.gridy = 1;
        _cc.weightx = 0;
        _cc.weighty = 0;
        viewPanel.add(_content, _cc);
        _content.setBorder(BorderFactory.createTitledBorder("Настройки соединения"));

        _content.setLayout(new GridBagLayout());
        
        _cc.gridx = 0;
        _cc.gridy = 0;
        _cc.weightx = 0;
        _cc.weighty = 0;
        _cc.fill = GridBagConstraints.NONE;
        _cc.anchor = GridBagConstraints.EAST;
        _cc.insets = _is1;
        _content.add(new JLabel("Тип соединения:"), _cc);
        
        _cc.gridx = 1;
        _cc.gridy = 0;
        _cc.weightx = 100;
        _cc.weighty = 0;
        _cc.fill = GridBagConstraints.NONE;
        _cc.anchor = GridBagConstraints.WEST;
        JComboBox _typeSelector = new JComboBox();
        _typeSelector.addItem("Клиент");
        _typeSelector.addItem("Сервер");
        _typeSelector.addItem("Роутер");
        _content.add(_typeSelector, _cc);

        _cc.gridx = 0;
        _cc.gridy = 1;
        _cc.weightx = 0;
        _cc.weighty = 0;
        _cc.fill = GridBagConstraints.NONE;
        _cc.anchor = GridBagConstraints.EAST;
        JLabel _addressLabel = new JLabel("Адрес (DNS или IP):");
        _content.add(_addressLabel, _cc);
        
        _cc.gridx = 1;
        _cc.gridy = 1;
        _cc.weightx = 100;
        _cc.weighty = 0;
        _cc.fill = GridBagConstraints.HORIZONTAL;
        _cc.anchor = GridBagConstraints.CENTER;
        JTextField _address = new JTextField();
        _address.setColumns(25);
        _content.add(_address, _cc);

        _cc.gridx = 0;
        _cc.gridy = 2;
        _cc.weightx = 0;
        _cc.weighty = 0;
        _cc.fill = GridBagConstraints.NONE;
        _cc.anchor = GridBagConstraints.EAST;
        _content.add(new JLabel("Порт (1-65536):"), _cc);
        
        _cc.gridx = 1;
        _cc.gridy = 2;
        _cc.weightx = 100;
        _cc.weighty = 0;
        _cc.fill = GridBagConstraints.HORIZONTAL;
        _cc.anchor = GridBagConstraints.CENTER;
        JTextField _port = new JTextField();
        _port.setColumns(25);
        _content.add(_port, _cc);

        _cc.gridx = 0;
        _cc.gridy = 3;
        _cc.weightx = 0;
        _cc.weighty = 0;
        _cc.fill = GridBagConstraints.NONE;
        _cc.anchor = GridBagConstraints.EAST;
        JLabel _idLabel = new JLabel("ID (число):");
        _content.add(_idLabel, _cc);
        
        _cc.gridx = 1;
        _cc.gridy = 3;
        _cc.weightx = 100;
        _cc.weighty = 0;
        _cc.fill = GridBagConstraints.HORIZONTAL;
        _cc.anchor = GridBagConstraints.CENTER;
        JTextField _id = new JTextField();
        _id.setColumns(25);
        _content.add(_id, _cc);

        _idLabel.setVisible(false);
        _id.setVisible(false);
        
        Runnable _updateSelectorState = () -> {
            switch(_typeSelector.getSelectedIndex()) {
                case 0:
                    _addressLabel.setVisible(true);
                    _address.setVisible(true);
                    _idLabel.setVisible(false);
                    _id.setVisible(false);
                    break;
                case 1:
                    _addressLabel.setVisible(false);
                    _address.setVisible(false);
                    _idLabel.setVisible(false);
                    _id.setVisible(false);
                    break;
                case 2:
                    _addressLabel.setVisible(true);
                    _address.setVisible(true);
                    _idLabel.setVisible(true);
                    _id.setVisible(true);
                    break;
            }
        };
        
        _typeSelector.addActionListener(
                (ActionEvent e) -> {
            _updateSelectorState.run();
        });
        
        v.setValidSate(() -> {
            boolean _error = false;
            String _sa = null;
            int _ip = -1;
            int _iid = 0;
            
            if((_typeSelector.getSelectedIndex() == 0) ||
                    (_typeSelector.getSelectedIndex() == 2)) {
                _sa = _address.getText();
                if((_sa == null) || (_sa.length() == 0)) {
                    _address.setBackground(Color.red);
                    _error = true;
                }
            }
            
            String _sp = _port.getText();
            boolean _p_error = false;
            try {
                _ip = Integer.parseInt(_sp);
                _p_error = ((_ip < 1) || (_ip > 65536));
            }
            catch(Exception e) {
                _p_error = true;
            }
            
            if(_p_error) {
                _port.setBackground(Color.red);
                _error = true;
            }
            
            boolean _id_error = false;
            if(_typeSelector.getSelectedIndex() == 2) {
                String _si = _id.getText();
                try {
                    _iid = Integer.parseInt(_si);
                }
                catch(Exception e) {
                    _id_error = true;
                }
            }
            
            if(_id_error) {
                _id.setBackground(Color.red);
                _error = true;
            }
            
            if(!_error) {
                conn.destroyConnection();
                
                switch(_typeSelector.getSelectedIndex()) {
                    case 0:
                        conn.setSetting(new seabattle.net.ClientSetting(_sa, _ip));
                        break;
                    case 1:
                        conn.setSetting(new seabattle.net.ServerSetting(_ip));
                        break;
                    case 2:
                        conn.setSetting(new seabattle.net.RouterClientSetting(_sa, _ip, _iid));
                        break;
                }
            }
            
            return !_error;
        });
        
        ConnectionSetting _setting = conn.getSetting();
        
        if(_setting != null) {
            if(_setting instanceof ClientSetting) {
                ClientSetting _cs = (ClientSetting)_setting;
                _typeSelector.setSelectedIndex(0);
                _address.setText(_cs.address);
                _port.setText(Integer.toString(_cs.port));
            }

            if(_setting instanceof ServerSetting) {
                ServerSetting _cs = (ServerSetting)_setting;
                _typeSelector.setSelectedIndex(1);
                _port.setText(Integer.toString(_cs.port));
            }

            if(_setting instanceof RouterClientSetting) {
                RouterClientSetting _cs = (RouterClientSetting)_setting;
                _typeSelector.setSelectedIndex(2);
                _address.setText(_cs.address);
                _port.setText(Integer.toString(_cs.port));
                _id.setText(Integer.toString(_cs.id));
            }
            
            _updateSelectorState.run();
        }
    }
}
