package qgame.gui;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import qgame.observer.IGameObserver;
import qgame.observer.QGameObserver;

public class ButtonPanel extends JPanel {
    
    public ButtonPanel(IGameObserver observer) {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        JButton previous = new JButton("previous");
        JButton next = new JButton("next");
        JButton save = new JButton("save");

        JTextField filepathField = new JTextField("filepath");

        previous.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                observer.previous();
            }
        });

        next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                observer.next();
            }
        });

        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) throws IllegalArgumentException {
                String filepath = filepathField.getText();
                observer.save(filepath);               
            }
        });

        this.add(previous);
        this.add(next);
        this.add(save);
        filepathField.setMaximumSize(new Dimension(100, 50));
        this.add(filepathField);
        
    }
}
