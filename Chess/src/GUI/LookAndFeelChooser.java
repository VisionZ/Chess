package GUI;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

public final class LookAndFeelChooser {
    
    private LookAndFeelChooser() {
        
    }
    
    public static final void selectLookAndFeel() {
        try {
            LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
            List<String> list = new ArrayList<>(installedLookAndFeels.length);
            for (int index = 0; index != installedLookAndFeels.length; ++index) {
                LookAndFeelInfo lookAndFeel = installedLookAndFeels[index];
                if (lookAndFeel != null) {
                    list.add(lookAndFeel.getName());
                }
            }
            String selectedLookAndFeel = (String) JOptionPane.showInputDialog(null, "Select a Look & Feel:", "Look & Feel", JOptionPane.QUESTION_MESSAGE, null, list.toArray(), null);
            if (selectedLookAndFeel != null) {
                for (int index = 0; index != installedLookAndFeels.length; ++index) {
                    LookAndFeelInfo lookAndFeel = installedLookAndFeels[index];
                    if (lookAndFeel != null && selectedLookAndFeel.equals(lookAndFeel.getName())) {
                        UIManager.setLookAndFeel(lookAndFeel.getClassName());
                        break;
                    }
                }
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {

        }
    }

    public static final void useNimbusLookAndFeel() {
        LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
        try {
            for (int index = 0; index != installedLookAndFeels.length; ++index) {
                LookAndFeelInfo lookAndFeel = installedLookAndFeels[index];
                if (lookAndFeel != null && "Nimbus".equals(lookAndFeel.getName())) {
                    UIManager.setLookAndFeel(lookAndFeel.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {

        }
    }
}