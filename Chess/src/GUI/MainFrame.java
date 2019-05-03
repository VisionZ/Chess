package GUI;

import Util.Constants;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

@Deprecated
public class MainFrame extends JFrame {
    
    private JMenuBar menuBar;
    private JMenu file;
    
    public MainFrame() {
        super("Chess");
        
        final int width = Constants.SCREEN_BOUNDS.width / 2;
        final int height = Constants.SCREEN_BOUNDS.height;
        final Dimension frameArea = new Dimension(width, height);
        
        super.setSize(frameArea);
        super.setPreferredSize(frameArea);
        super.setMinimumSize(frameArea);
        super.setMaximumSize(frameArea);
        super.setLocation((Constants.SCREEN_BOUNDS.width / 2) - (Constants.SCREEN_BOUNDS.width / 4), (Constants.SCREEN_BOUNDS.height / 2) - (height / 2));
        super.setResizable(false);
        super.setVisible(true);
        
        super.setJMenuBar(menuBar = new JMenuBar());
        
        file = new JMenu("File");
        menuBar.add(file);
        for (int i = 0; i < 10; i++) {
            menuBar.add(new JMenu("File" + i));
        }

        Container contentPane = super.getContentPane();
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(null);
     
        
        

        {
            JPanel panel = new JPanel(null);
            panel.setBounds(2, 2, contentPane.getWidth() - 4, 50);
            panel.setBackground(Color.BLACK);
            contentPane.add(panel);
            
            JPanel next = new JPanel(null);
            next.setBounds(2, panel.getHeight() + 4, panel.getWidth(), 50); 
            next.setBackground(Color.RED);
            contentPane.add(next);
        }

        super.setVisible(true);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        System.out.println(menuBar.getBounds());
        System.out.println(contentPane.getSize());
    }
    
    public static void main(String[] args) {
        LookAndFeelChooser.selectLookAndFeel();
        new MainFrame();
    }
    
    @Override
    public final Insets getInsets() {
        Insets superClass = super.getInsets();
        //System.out.println("Super Class: " + superClass.toString());
        Insets proposed = new Insets(superClass.top, 0, 0, 0);
        //System.out.println("Proposed Override: " + proposed);
        return superClass;
    }
}