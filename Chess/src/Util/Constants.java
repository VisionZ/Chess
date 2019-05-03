package Util;

import com.sun.security.auth.module.NTSystem;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

//class for common operations
public final class Constants {
    
    public static final int NEGATIVE_INFINITY = -2147483647;
    public static final int POSITIVE_INFINITY = 2147483647;
    public static final String SPACE = " ";
    public static final String UTF_8 = "UTF-8";
    public static final Runtime RUNTIME = Runtime.getRuntime();
    public static final NTSystem SYSTEM = new NTSystem();
    public static final GraphicsEnvironment GRAPHICS_ENVIRONMENT = GraphicsEnvironment.getLocalGraphicsEnvironment();
    public static final GraphicsConfiguration GRAPHICS_CONFIGURATION = GRAPHICS_ENVIRONMENT.getDefaultScreenDevice().getDefaultConfiguration();
    public static final Rectangle SCREEN_BOUNDS = GRAPHICS_ENVIRONMENT.getMaximumWindowBounds();
    public static final int APP_HEIGHT = getSystemApplicationHeight();
    private static final String FONT = "Arial";
    
    //class variables are intialized from top to bottom
    //so it would be illegal to put
    //SCREEN_BOUNDS before GRAPHICS_ENVIRONMENT
    //it's an illegal forward reference
    
    private Constants() {
        
    }

    //string functions
    
    public static String removeAll(String str, String remove) {
        if (str == null || remove == null) {
            throw new NullPointerException();
        }
        if (str.isEmpty() || remove.isEmpty()) {
            return "";
        }
        int length = remove.length();
        StringBuilder sb = new StringBuilder(str);
        int index = sb.indexOf(remove);
        while (index >= 0) {
            sb.delete(index, index + length);
            index = sb.indexOf(remove);
        }
        return sb.toString();
    }
    
    public static String replaceAll(String str, String remove, String replace) {
        if (str == null || remove == null || replace == null) {
            throw new NullPointerException();
        }
        if (replace.isEmpty()) {
            return removeAll(str, remove);
        }
        if (remove.isEmpty()) {
            return str;
        }
        StringBuilder builder = new StringBuilder(str);
        int length = remove.length();
        int index = builder.indexOf(remove);
        while (index >= 0) {
            builder.replace(index, index + length, replace);
            index = builder.indexOf(remove);
        }
        return builder.toString();
    }
    
    //deep equality method
    @SuppressWarnings("null")
    public static final boolean equals(Object a, Object b) {
        boolean aIsNull = (a == null);
        boolean bIsNull = (b == null);
        return (!aIsNull && !bIsNull) ? a.equals(b) : aIsNull == bIsNull;
    }

    /**
     * Silently closes a closeable object and does
     * not throw any exceptions.
     * @param closeable The closeable object to close.
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            }
            catch (IOException ex) {

            }
        }
    }
    
    public static void removeActionListeners(JButton button) {
        for (ActionListener listener : button.getActionListeners()) {
            button.removeActionListener(listener);
        }
    }
    
    public static void removeKeyListeners(JTextField textField) {
        for (KeyListener listener : textField.getKeyListeners()) {
            textField.removeKeyListener(listener);
        } 
    }
    
    public static void removeFocusListeners(JTextField textField) {
        for (FocusListener listener : textField.getFocusListeners()) {
            textField.removeFocusListener(listener);
        }
    }

    @SuppressWarnings("UnusedAssignment")
    private static int getSystemApplicationHeight() {
        JFrame frame = new JFrame();
        frame.setLocation(Integer.MAX_VALUE, Integer.MAX_VALUE);
        frame.setVisible(true);
        int defaultHeight = frame.getHeight();
        frame.setVisible(false);
        frame.setEnabled(false);
        frame.dispose();
        frame = null;
        return defaultHeight;
    }

    public static void moveToCenterScreen(JFrame frame) {
        if (frame == null) {
            return;
        }
        try {
            Toolkit kit = frame.getToolkit();
            Insets screen = kit.getScreenInsets(GRAPHICS_CONFIGURATION);
            Dimension area = kit.getScreenSize();
            //kit.beep(); //useful for using native system sounds.
            frame.setLocation((int) ((area.width - screen.left - screen.right) - frame.getWidth()) / 2, (int) ((area.height - screen.top - screen.bottom) - frame.getHeight()) / 2);
        } 
        catch (HeadlessException ex) {
            frame.setLocation(0, 0);
        }
    }

    public static float getStringWidth(String str, Font font, FontRenderContext fontRenderContext) {
        return (float) font.getStringBounds(str, fontRenderContext).getWidth();
    }

    public static float getStringHeight(String str, Font font, FontRenderContext fontRenderContext) {
        return (float) font.getStringBounds(str, fontRenderContext).getHeight();
    }
    
    private static final Map<String, Font> SAVED_FONTS = new HashMap<>(4);

    //works perfectly only when FontRenderContext is unchanged
    public static Font getFont(String text, int width, int height, FontRenderContext fontRenderContext) {
        if (SAVED_FONTS.containsKey(text)) {
            return SAVED_FONTS.get(text);
        }
        int size = 0;
        Font current = new Font(FONT, Font.BOLD, size);
        for (;;) {
            Rectangle2D currentStringBounds = current.getStringBounds(text, fontRenderContext);
            if (currentStringBounds.getWidth() <= width && currentStringBounds.getHeight() <= height) {
                Font next = new Font(FONT, Font.BOLD, ++size);
                Rectangle2D nextStringBounds = next.getStringBounds(text, fontRenderContext);
                if (nextStringBounds.getWidth() <= width && nextStringBounds.getHeight() <= height) {
                    current = next;
                    continue;
                }
                SAVED_FONTS.put(text, current);
                return current;
            }
            throw new IllegalArgumentException("Could not generate a suitable font.");
        }
    }

    public static Font getAbsoluteFont(String text, int width, int height, FontRenderContext fontRenderContext) {
        LinkedList<Font> list = new LinkedList<>();
        for (int size = 0;; ++size) {
            Font current = new Font(FONT, Font.BOLD, size);
            Rectangle2D currentStringBounds = current.getStringBounds(text, fontRenderContext);
            if (currentStringBounds.getWidth() <= width && currentStringBounds.getHeight() <= height) {
                list.add(current);
            }
            else {
                //sentinel condition reached...
                break;
            }
        }
        if (list.isEmpty()) {
            return null;
        }
        Font font = list.getLast();
        list.clear();
        return font;
    }

    /**
     * Returns the greater of 2 given integers.
     * @param a The first integer.
     * @param b The second integer.
     * @return The larger integer of {@code a} and {@code b}.
     */
    public static int max(int a, int b) {
        return (a > b) ? a : b;
    }

    /**
     * Returns the lesser of 2 given integers.
     * @param a The first integer.
     * @param b The second integer.
     * @return The smaller integer of {@code a} and {@code b}.
     */
    public static int min(int a, int b) {
        return (a < b) ? a : b;
    }
    
    /**
     * Returns the absolute value of a given integer.
     * @param n The given integer.
     * @return The absolute value of a given integer.
     */
    public static int abs(int n) {
        return (n < 0) ? -n : n;
    }
    
    /**
     * Flips the sign of a given integer.
     * @param n The given integer.
     * @return {@code -n}
     */
    public static int flipSigns(int n) {
        return -n;
    }
}