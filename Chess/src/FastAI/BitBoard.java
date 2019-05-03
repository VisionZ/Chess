package FastAI;

import static FastAI.Board.toBitBoardString;
import static Util.ChessConstants.LINEAR_LENGTH;

public final class BitBoard {
    
    private static final int SHIFT = LINEAR_LENGTH - 1;
    private long bits;
    
    public BitBoard() {
        
    }
    
    public BitBoard(long board) {
        bits = board;
    }
    
    public BitBoard(BitBoard board) {
        bits = board.bits;
    }
    
    //uses [0] to [63] positing
    public final void setBit(int index) {
        bits |= (1L << (SHIFT - index));
    }
    
    public final void unsetBit(int index) {
        bits &= ~(1L << (SHIFT - index));
    }
    
    public final boolean isBitSet(int index) {
        return (bits & (1L << (SHIFT - index))) != 0;
    }
    
    public final void setBits(long board) {
        bits = board;
    }
    
    public final long getBits() {
        return bits;
    }
    
    public BitBoard or(BitBoard other) {
        bits |= other.bits;
        return this;
    }
    
    public BitBoard and(BitBoard other) {
        bits &= other.bits;
        return this;
    }
    
    public void clear() {
        bits = 0L;
    }
    
    public void fill() {
        bits = -1L;
    }
    
    @Override
    public final String toString() {
        return Long.toString(bits);
    }
    
    public final String toBitString() {
        return toBitBoardString(bits);
    }
    
    @Override
    public final boolean equals(Object obj) {
        return (obj == this) ? true : !(obj instanceof BitBoard) ? false : bits == ((BitBoard) obj).bits;
    }
    
    public final boolean fastEquals(BitBoard other) {
        return bits == other.bits;
    }

    @Override
    public final int hashCode() {
        return Long.hashCode(bits);
    }
    
    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    public final BitBoard clone() {
        return new BitBoard(bits);
    }
    
    public static void main(String[] args) {
        BitBoard board = new BitBoard(-1L);
        for (int index = 0; index < LINEAR_LENGTH; index++) {
            String original = board.toBitString();
            System.out.println("Original:\n" + original);
            System.out.println();
            board.unsetBit(index);
            board.unsetBit(index);
            System.out.println(board.isBitSet(index));
            String current = board.toBitString();
            System.out.println("After Modifications:\n" + current);
            board.setBit(index);
            board.setBit(index);
            System.out.println(board.isBitSet(index));
            if (!original.equals(board.toBitString())) {
                throw new Error();
            }
            System.out.println("================================");
        }
    }
}