package GUI;

import Util.Constants;
import Util.EmptyList;
import Util.ImageUtils;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

@SuppressWarnings("EqualsAndHashcode")
public final class Tile extends Area {

    static final EmptyList<Tile> EMPTY_LIST = new EmptyList<>();

    private static final BufferedImage LIGHT_TILE = ImageUtils.readImage("LightTile", ImageUtils.JPG);
    private static final BufferedImage LIGHT_TILE_MOVE_TINT = ImageUtils.tintImage(LIGHT_TILE, Board.MOVE, 0.5f);
    private static final BufferedImage LIGHT_TILE_ATTACK_TINT = ImageUtils.tintImage(LIGHT_TILE, Board.ATTACK, 0.5f);
    private static final BufferedImage LIGHT_TILE_CASTLE_TINT = ImageUtils.tintImage(LIGHT_TILE, Board.CASTLE, 0.5f);
    private static final BufferedImage LIGHT_TILE_EN_PASSANT_TINT = ImageUtils.tintImage(LIGHT_TILE, Board.EN_PASSANT, 0.5f);
    private static final BufferedImage LIGHT_TILE_SELECTED_TINT = ImageUtils.tintImage(LIGHT_TILE, Board.SELECTED, 0.5f);

    private static final BufferedImage DARK_TILE = ImageUtils.readImage("DarkTile", ImageUtils.JPG);
    private static final BufferedImage DARK_TILE_MOVE_TINT = ImageUtils.tintImage(DARK_TILE, Board.MOVE, 0.5f);
    private static final BufferedImage DARK_TILE_ATTACK_TINT = ImageUtils.tintImage(DARK_TILE, Board.ATTACK, 0.5f);
    private static final BufferedImage DARK_TILE_CASTLE_TINT = ImageUtils.tintImage(DARK_TILE, Board.CASTLE, 0.5f);
    private static final BufferedImage DARK_TILE_EN_PASSANT_TINT = ImageUtils.tintImage(DARK_TILE, Board.EN_PASSANT, 0.5f);
    private static final BufferedImage DARK_TILE_SELECTED_TINT = ImageUtils.tintImage(DARK_TILE, Board.SELECTED, 0.5f);

    private Piece occupant;

    private boolean protectedByWhite;
    private boolean protectedByBlack;

    private Color tintColor;
    private Color outlineColor;

    private final boolean isLight;

    public Tile(int x, int y, int width, int height, int row, int column, Color edgeColor, boolean light) {
        super(x, y, width, height, row, column);
        protectedByWhite = protectedByBlack = false;
        outlineColor = edgeColor;
        isLight = light;
    }
    
    public Tile(Tile tile) {
        super(tile.getX(), tile.getY(), tile.getWidth(), tile.getHeight(), tile.getRow(), tile.getColumn());
        if (tile.occupant != null) {
            occupant = tile.occupant.clone();
        }
        protectedByWhite = tile.protectedByWhite;
        protectedByBlack = tile.protectedByBlack;
        tintColor = tile.tintColor;
        outlineColor = tile.outlineColor;
        isLight = tile.isLight;
    }
   
    @Override
    @Deprecated
    public final void setRow(int row) {
        throw new UnsupportedOperationException("Cannot change row in Tile.");
    }

    @Override
    @Deprecated
    public final void setColumn(int column) {
        throw new UnsupportedOperationException("Cannot change column in Tile.");
    }

    @Override
    @Deprecated
    public final void setLocation(int row, int column) {
        throw new UnsupportedOperationException("Cannot change location in Tile.");
    }

    public Color getOutline() {
        return outlineColor;
    }

    public void setOutline(Color color) {
        outlineColor = color;
    }

    public Color getTint() {
        return tintColor;
    }

    public void setTint(Color color) {
        tintColor = color;
    }

    public boolean isOccupied() {
        return occupant != null;
    }

    public boolean isOccupiedByWhite() {
        return occupant != null && occupant.isWhite();
    }

    public boolean isOccupiedByBlack() {
        return occupant != null && occupant.isBlack();
    }

    public Piece getOccupant() {
        return occupant;
    }

    public void setOccupant(Piece newOccupant) {
        occupant = null;
        occupant = newOccupant;
        occupant.setLocation(getRow(), getColumn());
    }

    public void removeOccupant() {
        occupant = null;
    }

    public void setProtectedBy(Piece piece) {
        if (piece.isWhite()) {
            protectedByWhite = true;
        }
        else {
            protectedByBlack = true;
        }
    }

    public boolean protectedByWhite() {
        return protectedByWhite;
    }

    public boolean protectedByBlack() {
        return protectedByBlack;
    }

    public boolean protectedByEnemy(Piece piece) {
        return (piece.isWhite()) ? protectedByBlack : protectedByWhite;
    }

    public void removeProtections() {
        protectedByWhite = protectedByBlack = false;
    }

    @Override
    public void render(Graphics context) {
        final int x = getX();
        final int y = getY();
        final int width = getWidth();
        final int height = getHeight();
        if (tintColor == null) {
            context.drawImage(isLight ? LIGHT_TILE : DARK_TILE, x, y, width, height, null);
        }
        else if (isLight) {
            if (Board.MOVE.equals(tintColor)) {
                context.drawImage(LIGHT_TILE_MOVE_TINT, x, y, width, height, null);
            }
            else if (Board.ATTACK.equals(tintColor)) {
                context.drawImage(LIGHT_TILE_ATTACK_TINT, x, y, width, height, null);
            }
            else if (Board.SELECTED.equals(tintColor)) {
                context.drawImage(LIGHT_TILE_SELECTED_TINT, x, y, width, height, null);
            }
            else if (Board.EN_PASSANT.equals(tintColor)) {
                context.drawImage(LIGHT_TILE_EN_PASSANT_TINT, x, y, width, height, null);
            }
            else {
                context.drawImage(LIGHT_TILE_CASTLE_TINT, x, y, width, height, null);
            }
        }
        else if (Board.MOVE.equals(tintColor)) {
            context.drawImage(DARK_TILE_MOVE_TINT, x, y, width, height, null);
        }
        else if (Board.ATTACK.equals(tintColor)) {
            context.drawImage(DARK_TILE_ATTACK_TINT, x, y, width, height, null);
        }
        else if (Board.SELECTED.equals(tintColor)) {
            context.drawImage(DARK_TILE_SELECTED_TINT, x, y, width, height, null);
        }
        else if (Board.EN_PASSANT.equals(tintColor)) {
            context.drawImage(DARK_TILE_EN_PASSANT_TINT, x, y, width, height, null);
        }
        else {
            context.drawImage(DARK_TILE_CASTLE_TINT, x, y, width, height, null);
        }
        if (occupant != null) {
            occupant.setSize(width / 2, height);
            occupant.setCoordinate(x + width / 4, y);
            occupant.render(context);
        }
        if (outlineColor != null) {
            Color originalColor = context.getColor();
            context.setColor(outlineColor);
            context.drawRect(x, y, width, height);
            context.setColor(originalColor);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tile)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final Tile other = (Tile) obj;
        return isLight == other.isLight
                && protectedByWhite == other.protectedByWhite
                && protectedByBlack == other.protectedByBlack
                && Constants.equals(tintColor, other.tintColor)
                && Constants.equals(outlineColor, other.outlineColor)
                && Constants.equals(occupant, other.occupant);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(isLight ? "Light Tile " : "Dark Tile ");
        result.append("Protected By White: ");
        result.append(protectedByWhite);
        result.append(" Protected By Black: ");
        result.append(protectedByBlack);
        result.append(" Tint Color: ");
        result.append(tintColor);
        result.append(" Outline Color: ");
        result.append(outlineColor);
        result.append(" Occupant: ");
        result.append(occupant);
        return result.append("\n").toString();
    }
}