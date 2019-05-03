package Util;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

//frame that displays a bunch of text
public class TextFrame extends JFrame {
    
    private final JScrollPane scroll;
    private final JEditorPane editor;
    private final JButton button;
    
    @SuppressWarnings("Convert2Lambda")
    protected TextFrame(Component parent, Image iconImage, String title, String info) {
        super.setTitle(title);
        super.setIconImage(iconImage);
        super.setLocationRelativeTo(parent);
        scroll = new JScrollPane();
        editor = new JEditorPane();
        button = new JButton();
        
        button.setText("Close");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                TextFrame.this.dispose();
            }
        });

        Container contentPane = super.getContentPane();

        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout) contentPane.getLayout()).columnWidths = new int[]{10, 0, 65, 5, 0};
        ((GridBagLayout) contentPane.getLayout()).rowHeights = new int[]{10, 0, 30, 5, 0};
        ((GridBagLayout) contentPane.getLayout()).columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 1.0E-4};
        ((GridBagLayout) contentPane.getLayout()).rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 1.0E-4};

        editor.setText(info);
        editor.setEditable(false);
        scroll.setViewportView(editor);

        contentPane.add(scroll, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
                new Insets(0, 0, 5, 5), 0, 0));

        contentPane.add(button, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        if (parent != null) {
            super.setBounds(new Rectangle(parent.getX() + parent.getWidth() / 4, parent.getY() + parent.getHeight() / 3, parent.getWidth() / 2, parent.getHeight() / 2));
        }
        else {
            super.setSize(600, 600);
        }
        super.setVisible(true);
        super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static final void showTextFrame(Component parent, Image iconImage, String title, String body) {
        new TextFrame(parent, iconImage, title, body);
    }
}