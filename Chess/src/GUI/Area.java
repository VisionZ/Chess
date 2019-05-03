package GUI;

import static Util.ChessConstants.LENGTH;
import java.awt.Graphics;
import java.awt.Point;

@SuppressWarnings("EqualsAndHashcode")
public abstract class Area implements Locatable, Renderable {
    
    private int x;
    private int y;
    private int width;
    private int height;
    private int row;
    private int column;
    
    public Area(int x, int y, int width, int height, int row, int column) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.row = row;
        this.column = column;
    }

    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public void setCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setCoordinate(Point p) {
        x = p.x;
        y = p.y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public int getRow() {
        return row;
    }
    
    @Override
    public void setRow(int row) {
        this.row = row;
    }
    
    @Override
    public int getColumn() {
        return column;
    }
    
    @Override
    public void setColumn(int column) {
        this.column = column;
    }
    
    public void setLocation(int row, int column) {
        this.row = row;
        this.column = column;
    }
    
    public boolean sameRow(Area other) {
        return row == other.row;
    }
    
    public boolean sameColumn(Area other) {
        return column == other.column;
    }
    
    public boolean sameLocation(Area other) {
        return (row == other.row) && (column == other.column);
    }
    
    public boolean contains(Point point) {
        return (point.x >= x) && (point.x <= (x + width)) && (point.y >= y) && (point.y <= (y + height));
    }
    
    @Override
    public abstract void render(Graphics context);
 
    @Override
    public String toString() {
        return "[" + x + "][" + y + "][" + width + "][" + height + "][" + row + "][" + column + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Area)) {
            return false;
        }
        Area area = (Area) obj;
        return (x == area.x) && (y == area.y) && (width == area.width) && (height == area.height) && (row == area.row) && (column == area.column);
    }

    public String getNotationLocation() {
        return "[" + (LENGTH - row) + "," + ((char) (column + 65)) + "]";
    }
}