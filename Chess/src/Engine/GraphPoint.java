package Engine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Ellipse2D;

public final class GraphPoint {
    
    public static final int DEFAULT_SIZE = 10;
    
    private Color color;
    
    //top left x and y
    private int x;
    private int y;
    private int size;
    private final Ellipse2D.Double circle;
    
    private final int value;
    
    public GraphPoint(int x, int y, int value, Color color) {
        this(x, y, DEFAULT_SIZE, value, color);
    }
    
    public GraphPoint(int x, int y, int size, int value, Color color) {
        if (color == null) {
            throw new NullPointerException();
        }
        this.color = color;
        int halfSize = (this.size = size) / 2;
        circle = new Ellipse2D.Double((this.x = x) - halfSize, (this.y = y) - halfSize, size, size);
        this.value = value;
    }
    
    public void render(Graphics context) {
        Color previous = context.getColor();
        context.setColor(color);
        int halfSize = size / 2;
        context.fillOval(x - halfSize, y - halfSize, size, size);
        context.setColor(previous);
    }
    
    public void setColor(Color color) {
        if (color == null) {
            throw new NullPointerException();
        }
        this.color = color;
    }
    
    public void setX(int x) {
        circle.x = (this.x = x) - size / 2;
    }
    
    public void setY(int y) {
        circle.y = (this.y = y) - size / 2;
    }
    
    public void setSize(int size) {
        circle.width = circle.height = this.size = size;
    }
    
    public Color getColor() {
        return color;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getSize() {
        return size;
    }
    
    public int getValue() {
        return value;
    }

    //0, 0, 10, 10
    //5, 5
    public boolean contains(Point point) {
        return circle.contains(point.x, point.y);
    }
}