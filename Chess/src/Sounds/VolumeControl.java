package Sounds;

import GUI.Chess;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public final class VolumeControl extends JSlider {

    private static final int MIN = 0;
    private static final int MID = 50;
    private static final int MAX = 100;
  
    @SuppressWarnings("Convert2Lambda")
    public VolumeControl(Chess parent, int x, int y, int width, int height) {
        super(JSlider.HORIZONTAL, MIN, MAX, MID);
        super.setBounds(x, y, width, height);
        Dimension size = new Dimension(width, height);
        super.setMinimumSize(size);
        super.setPreferredSize(size);
        super.setSize(width, height);
        super.setToolTipText("Volume Control");
        super.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        super.setPaintTicks(true);
        super.setAutoscrolls(true);
        @SuppressWarnings("UseOfObsoleteCollectionType")
        java.util.Hashtable<Integer, JLabel> labels = new java.util.Hashtable<>(1);
        labels.put(MID, new JLabel("Volume"));
        super.setLabelTable(labels);
        super.setPaintLabels(true);
        super.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                //dividing with decimal place changes to double precision
                parent.getChild().setVolume(getValue() / 100.0);
            }
        });
        super.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent ce) { 
                repaint();
            }

            @Override
            public void componentMoved(ComponentEvent ce) {
                repaint();
            }

            @Override
            public void componentShown(ComponentEvent ce) {
                repaint();
            }

            @Override
            public void componentHidden(ComponentEvent ce) {
                
            }
        });
        super.validate();
    }
}