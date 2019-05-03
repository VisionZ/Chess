package GUI;

import Util.ChessConstants;
import Util.Constants;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class StatusDisplayer extends JPanel {
    
    private static final String WHITE_TURN = "White's Turn"; 
    private static final String BLACK_TURN = "Black's Turn"; 
    
    private boolean turn;
    private String stateDisplay;
    
    public StatusDisplayer(int x, int y, int width, int height) {
        super(null);
        super.setBounds(x, y, width, height);
        super.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        super.setFocusable(true);
    }
    
    public void setTurnDisplay(boolean turn) {
        this.turn = turn;
    }

    public void setStateDisplay(String stateDisplay) {
        this.stateDisplay = stateDisplay;
    }
   
    private int width;
    private int height;
    private BufferedImage offscreenBuffer;
    private Graphics2D offscreenGraphics;
    private FontRenderContext offscreenFontRenderContext;
    
    @Override
    protected void paintComponent(Graphics window) {
        super.paintComponent(window);
        
        if (offscreenBuffer == null) {
            offscreenFontRenderContext = (offscreenGraphics = (offscreenBuffer = (BufferedImage) createImage(width = getWidth(), height = getHeight())).createGraphics()).getFontRenderContext();
        }
        
        offscreenGraphics.setColor(Color.GRAY.brighter());
        offscreenGraphics.fillRect(getX(), getY(), width, height);
        
        final float turnDisplayHeight;
        
        if (turn) {
            offscreenGraphics.setFont(Constants.getFont(WHITE_TURN, width, height / 2, offscreenFontRenderContext));
            offscreenGraphics.setColor(Color.WHITE);
            offscreenGraphics.drawString(WHITE_TURN, (width / 2) - Constants.getStringWidth(WHITE_TURN, offscreenGraphics.getFont(), offscreenFontRenderContext) / 2, turnDisplayHeight = Constants.getStringHeight("White's Turn", offscreenGraphics.getFont(), offscreenFontRenderContext));
        }
        else {
            offscreenGraphics.setFont(Constants.getFont(BLACK_TURN, width, height / 2, offscreenFontRenderContext));
            offscreenGraphics.setColor(Color.BLACK);
            offscreenGraphics.drawString(BLACK_TURN, (width / 2) - Constants.getStringWidth(BLACK_TURN, offscreenGraphics.getFont(), offscreenFontRenderContext) / 2, turnDisplayHeight = Constants.getStringHeight("Black's Turn", offscreenGraphics.getFont(), offscreenFontRenderContext));
        }
        
        if (stateDisplay != null) {
            offscreenGraphics.setColor(stateDisplay.startsWith(ChessConstants.WHITE) ? Color.WHITE : Color.BLACK);
            offscreenGraphics.setFont(Constants.getFont(stateDisplay, width, height / 2, offscreenFontRenderContext));
            offscreenGraphics.drawString(stateDisplay, (width / 2) - (Constants.getStringWidth(stateDisplay, offscreenGraphics.getFont(), offscreenFontRenderContext) / 2), turnDisplayHeight + Constants.getStringHeight(stateDisplay, offscreenGraphics.getFont(), offscreenFontRenderContext));
            stateDisplay = null;
        }
        
        window.drawImage(offscreenBuffer, 0, 0, this);
    }
}