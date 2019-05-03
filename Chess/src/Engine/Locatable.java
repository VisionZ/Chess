package Engine;

import static Util.ChessConstants.LENGTH;

public interface Locatable {
    
    int getRow();
    
    int getColumn();
    
    default int getIndex() {
        return (getRow() << 3) + getColumn();
    }
    
    default String getNotationLocation() {
        return "[" + (LENGTH - getRow()) + "," + ((char) (getColumn() + 65)) + "]";
    }
    
    void setRow(int row);
    
    void setColumn(int column);
    
    void setLocation(int row, int column);
    
    default boolean sameLocation(Locatable other) {
        return getRow() == other.getRow() && getColumn() == other.getColumn();
    }
    
    @Override
    boolean equals(Object obj);
}