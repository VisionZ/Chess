package Engine;

import GUI.Chess;
import Util.Constants;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

final class GraphInfo extends JDialog {
    
    private final CartesianGraph graph;

    public GraphInfo(JFrame parent) {
        super(parent, "Graph", false);
        super.setIgnoreRepaint(true);
        super.setIconImage(parent.getIconImage());

        final Dimension windowSize = new Dimension(parent.getWidth(), parent.getHeight());

        super.setSize(windowSize);
        super.setMinimumSize(windowSize);
        super.setMaximumSize(windowSize);
        super.setPreferredSize(windowSize);
        
        super.getContentPane().add(graph = new CartesianGraph());

        super.setLocationRelativeTo(parent);
        super.setResizable(false);
        super.setVisible(true);
    }
    
    public void addScore(int score) {
        graph.addPoint(score);
    }
    
    public int[] getScores() {
        int size = graph.points.size();
        final int[] scores = new int[size];
        while (--size >= 0) {
            scores[size] = graph.points.get(size).getValue();
        }
        return scores;
    }
    
    public void clearScores() {
        graph.points.clear();
    }
    
    private static class CartesianGraph extends JPanel implements Runnable {
        
        private final List<GraphPoint> points = new ArrayList<>();
        
        @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
        private CartesianGraph() {
            super.setToolTipText("Graph");
            MouseTracker tracker = new MouseTracker();
            super.addMouseListener(tracker);
            super.addMouseMotionListener(tracker);
            new Thread(this, "Graph Thread").start();
        }
        
        private void addPoint(final int value) {
            final int width = getWidth();
            final int height = getHeight();
            int y = (height / 2) - (value / 10);
            Color color;
            if (y <= 0) {
                y = GraphPoint.DEFAULT_SIZE / 2;
                color = Color.GREEN;
            }
            else if (y >= height) {
                y = height - (GraphPoint.DEFAULT_SIZE / 2);
                color = Color.RED;
            }
            else {
                color = Color.WHITE;
            }
            points.add(new GraphPoint(0, y, value, color));
            final int numberOfPoints = points.size();
            int space = width / ((numberOfPoints == 1) ? 1 : numberOfPoints - 1);
            if (space <= 0) {
                space = 1;
            }
            for (int index = 0; index != numberOfPoints; ++index) {
                int newX = index * space;
                if (newX == 0) {
                    newX = GraphPoint.DEFAULT_SIZE / 2;
                }
                else if ((newX + GraphPoint.DEFAULT_SIZE) >= width) {
                    newX = width - (GraphPoint.DEFAULT_SIZE / 2);
                }
                points.get(index).setX(newX);
            }
        }

        //50 moves, width = 600
        //spaceing 600 / 50 
        private BufferedImage offscreenBuffer;
        private Graphics2D offscreenGraphics;

        @Override
        protected void paintComponent(Graphics window) {
            super.paintComponent(window);
            final int width = getWidth();
            final int height = getHeight();
            
            if (offscreenBuffer == null) {
                offscreenBuffer = (BufferedImage) (createImage(width, height));
                offscreenGraphics = offscreenBuffer.createGraphics();
            }
            
            offscreenGraphics.setColor(Color.BLACK);
            offscreenGraphics.fillRect(0, 0, width, height);
            offscreenGraphics.setColor(Color.WHITE);
            
            int xIntercept = height / 2;
            offscreenGraphics.drawLine(0, xIntercept, width, xIntercept);
            
            int numberOfPoints = points.size();
            
            for (int index = 0; index != numberOfPoints;) {
                GraphPoint point = points.get(index);
                if (++index < numberOfPoints) {
                    GraphPoint nextPoint = points.get(index);
                    //if the next point is above - green
                    //if the next point is below - red
                    //if the next point is the same y, then white
                    if (nextPoint.getY() < point.getY()) {
                        offscreenGraphics.setColor(Color.GREEN);
                    }
                    else if (nextPoint.getY() > point.getY()) {
                        offscreenGraphics.setColor(Color.RED);
                    }
                    else {
                        offscreenGraphics.setColor(Color.WHITE);
                    }
                    offscreenGraphics.drawLine(point.getX(), point.getY(), nextPoint.getX(), nextPoint.getY());
                }
                point.render(offscreenGraphics);
            }
            
            int overallTrend = (numberOfPoints != 0) ? points.get(numberOfPoints - 1).getValue() : 0;

            if (overallTrend > 0) {
                offscreenGraphics.setColor(Color.GREEN);
                String message = "AI is winning by " + overallTrend;
                offscreenGraphics.drawString(message, 0, Constants.getStringHeight(message, getFont(), offscreenGraphics.getFontRenderContext()));
            }
            else if (overallTrend < 0) {
                offscreenGraphics.setColor(Color.RED);
                String message = "You are winning by " + (-overallTrend);
                offscreenGraphics.drawString(message, 0, Constants.getStringHeight(message, getFont(), offscreenGraphics.getFontRenderContext()));
            }
            else {
                offscreenGraphics.setColor(Color.WHITE);
                String message = "You are evenly matched with the AI.";
                offscreenGraphics.drawString(message, 0, Constants.getStringHeight(message, getFont(), offscreenGraphics.getFontRenderContext()));
            }
            try {
                BufferedImage screen = new Robot().createScreenCapture(Constants.SCREEN_BOUNDS);
                offscreenGraphics.drawImage(screen, 0, 0, getWidth(), getHeight(), null);
            }
            catch (AWTException ex) {
                Logger.getLogger(GraphInfo.class.getName()).log(Level.SEVERE, null, ex);
            }

            window.drawImage(offscreenBuffer, 0, 0, this);
        }
        
        public void printData() {
            System.out.println(getHeight() / 2);
            for (int index = 0, size = points.size(); index != size; ++index) {
                GraphPoint point = points.get(index);
                System.out.println(point.getX() + " " + point.getY() + " " + point.getSize() + " " + point.getValue());
            }
        }

        @Override
        public void run() {
            try {
                for (;;) {
                    repaint();
                    TimeUnit.SECONDS.sleep(1);
                }
            }
            catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }

        private class MouseTracker implements MouseMotionListener, MouseListener {

            @Override
            public void mouseMoved(MouseEvent me) {
                Point mouse = me.getPoint();
                for (int index = 0, size = points.size(); index != size; ++index) {
                    GraphPoint point = points.get(index);
                    if (point.contains(mouse)) {
                        setToolTipText(Integer.toString(point.getValue()));
                        return;
                    }
                }
                setToolTipText("Graph");
            }

            @Override
            public void mouseDragged(MouseEvent me) {
                Point mouse = me.getPoint();
                for (int index = 0, size = points.size(); index != size; ++index) {
                    GraphPoint point = points.get(index);
                    if (point.contains(mouse)) {
                        setToolTipText(Integer.toString(point.getValue()));
                        return;
                    }
                }
                setToolTipText("Graph");
            }

            @Override
            public void mouseClicked(MouseEvent me) {
                Point mouse = me.getPoint();
                for (int index = 0, size = points.size(); index != size; ++index) {
                    GraphPoint point = points.get(index);
                    if (point.contains(mouse)) {
                        setToolTipText(Integer.toString(point.getValue()));
                        return;
                    }
                }
                setToolTipText("Graph");
            }

            @Override
            public void mousePressed(MouseEvent me) {
                Point mouse = me.getPoint();
                for (int index = 0, size = points.size(); index != size; ++index) {
                    GraphPoint point = points.get(index);
                    if (point.contains(mouse)) {
                        setToolTipText(Integer.toString(point.getValue()));
                        return;
                    }
                }
                setToolTipText("Graph");
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                
            }

            @Override
            public void mouseEntered(MouseEvent me) {
                
            }

            @Override
            public void mouseExited(MouseEvent me) {

            }
        }
    }

    public static void main(String[] args) {
        long bin = 0b1000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
        System.out.println(bin);
        GraphInfo graphInfo = new GraphInfo(Chess.getInstance());
        for (int i = 0; i < 15; i++) {
            int randomValue = (int) (Math.random() * 250);
            int negative = (int) (Math.random() * 2);
            if (negative == 1) {
                randomValue = -randomValue;
            }
            graphInfo.addScore(randomValue);
        }
        graphInfo.addScore(200000000);
        graphInfo.addScore(-250000000);
        graphInfo.setVisible(true);
        graphInfo.graph.printData();
        System.out.println(Arrays.toString(graphInfo.getScores()));
        double[] nums = new double[6];
        nums[0] = 2.0;
        nums[1] = 2.5;
        nums[2] = 3.0;
        nums[3] = 3.5;
        nums[4] = 4.0;
        nums[5] = 6.0;
        for (int i = 0; i < 6; i++) {
            nums[i] *= 1000000.0;
        }
        for (double n : nums) {
            System.out.println(1.0 / (n * n));
        }
    }

    public static String swap(String given, String first, String second) {
        int length = given.length();
        StringBuilder sb = new StringBuilder(length);
        for (int index = 0; index < length; ++index) {
            int jump = index + 5;
            if (jump <= length) {
                String current = given.substring(index, jump);
                String lower = current.toLowerCase();
                if (Character.isUpperCase(current.charAt(0))) {
                    if (lower.equals("white")) {
                        sb.append("Black");
                    }
                    else if (lower.equals("black")) {
                        sb.append("White");
                    }
                }
                else if (Character.isLowerCase(current.charAt(0))) {
                    if (lower.equals("white")) {
                        sb.append("black");
                    }
                    else if (lower.equals("black")) {
                        sb.append("white");
                    }
                }
                else {
                    sb.append(given.charAt(index));
                }
            }
            else {
                sb.append(given.charAt(index));
            }
        }
        return sb.toString();
    }
}
