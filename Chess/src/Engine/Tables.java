package Engine;

import static Util.ChessConstants.LENGTH;
import static Util.ChessConstants.LINEAR_LENGTH;
import static Util.Constants.SPACE;
import static Util.Constants.UTF_8;
import Util.Quotes;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public final class Tables {

    private Tables() {
        
    }

    static final int[] PAWN_POSITION_WHITE = new int[LINEAR_LENGTH];
    static final int[] KNIGHT_POSITION_WHITE = new int[LINEAR_LENGTH];
    static final int[] BISHOP_POSITION_WHITE = new int[LINEAR_LENGTH];
    static final int[] ROOK_POSITION_WHITE = new int[LINEAR_LENGTH];
    static final int[] QUEEN_POSITION_WHITE = new int[LINEAR_LENGTH];
    static final int[] KING_POSITION_WHITE = new int[LINEAR_LENGTH];

    static final int[] PAWN_POSITION_BLACK = new int[LINEAR_LENGTH];
    static final int[] KNIGHT_POSITION_BLACK = new int[LINEAR_LENGTH];
    static final int[] BISHOP_POSITION_BLACK = new int[LINEAR_LENGTH];
    static final int[] ROOK_POSITION_BLACK = new int[LINEAR_LENGTH];
    static final int[] QUEEN_POSITION_BLACK = new int[LINEAR_LENGTH];
    static final int[] KING_POSITION_BLACK = new int[LINEAR_LENGTH];
    
    private static final int[] LITTLE_ENDIAN_RANK_FILE_MAPPING = {
        56, 57, 58, 59, 60, 61, 62, 63,
        48, 49, 50, 51, 52, 53, 54, 55,
        40, 41, 42, 43, 44, 45, 46, 47,
        32, 33, 34, 35, 36, 37, 38, 39,
        24, 25, 26, 27, 28, 29, 30, 31,
        16, 17, 18, 19, 20, 21, 22, 23,
        8, 9, 10, 11, 12, 13, 14, 15,
        0, 1, 2, 3, 4, 5, 6, 7
    };

    //read from PieceSquareTables.dat
    @SuppressWarnings("CallToPrintStackTrace")
    private static void loadTables() {
        //put absurd numbers for a sanity check, to make sure tables are loaded properly
        for (int index = 0; index != LINEAR_LENGTH; ++index) {
            PAWN_POSITION_BLACK[index] = PAWN_POSITION_WHITE[index] = 100000;
            KNIGHT_POSITION_BLACK[index] = KNIGHT_POSITION_WHITE[index] = 100000;
            BISHOP_POSITION_BLACK[index] = BISHOP_POSITION_WHITE[index] = 100000;
            ROOK_POSITION_BLACK[index] = ROOK_POSITION_WHITE[index] = 100000;
            QUEEN_POSITION_BLACK[index] = QUEEN_POSITION_WHITE[index]= 100000;
            KING_POSITION_BLACK[index] = KING_POSITION_WHITE[index] = 100000;
        }
        InputStream tableFile;
        try {
            tableFile = Tables.class.getResourceAsStream("/Engine/PieceSquareTables.dat");
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        //could use Scanner with InputStream & UTF_8 instead
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(tableFile, UTF_8));
        }
        catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return;
        }
        try {
            for (String type = reader.readLine(); type != null; type = reader.readLine()) {
                int gridIndex = 0;
                switch (type) {
                    case "White King": {
                        for (int times = LENGTH; times > 0; --times) { //read 8 lines
                            String[] horizontal = reader.readLine().split(SPACE); //get current line data
                            for (int index = 0; index < LENGTH; ++index) {
                                KING_POSITION_WHITE[gridIndex++] = Integer.parseInt(horizontal[index]);
                            }
                        }
                        break;
                    }
                    case "White Queen": {
                        for (int times = LENGTH; times > 0; --times) { //read 8 lines
                            String[] horizontal = reader.readLine().split(SPACE); //get current line data
                            for (int index = 0; index < LENGTH; ++index) {
                                QUEEN_POSITION_WHITE[gridIndex++] = Integer.parseInt(horizontal[index]);
                            }
                        }
                        break;
                    }
                    case "White Rook": {
                        for (int times = LENGTH; times > 0; --times) { //read 8 lines
                            String[] horizontal = reader.readLine().split(SPACE); //get current line data
                            for (int index = 0; index < LENGTH; ++index) {
                                ROOK_POSITION_WHITE[gridIndex++] = Integer.parseInt(horizontal[index]);
                            }
                        }
                        break;
                    }
                    case "White Bishop": {
                        for (int times = LENGTH; times > 0; --times) { //read 8 lines
                            String[] horizontal = reader.readLine().split(SPACE); //get current line data
                            for (int index = 0; index < LENGTH; ++index) {
                                BISHOP_POSITION_WHITE[gridIndex++] = Integer.parseInt(horizontal[index]);
                            }
                        }
                        break;
                    }
                    case "White Knight": {
                        for (int times = LENGTH; times > 0; --times) { //read 8 lines
                            String[] horizontal = reader.readLine().split(SPACE); //get current line data
                            for (int index = 0; index < LENGTH; ++index) {
                                KNIGHT_POSITION_WHITE[gridIndex++] = Integer.parseInt(horizontal[index]);
                            }
                        }
                        break;
                    }
                    case "White Pawn": {
                        for (int times = LENGTH; times > 0; --times) { //read 8 lines
                            String[] horizontal = reader.readLine().split(SPACE); //get current line data
                            for (int index = 0; index < LENGTH; ++index) {
                                PAWN_POSITION_WHITE[gridIndex++] = Integer.parseInt(horizontal[index]);
                            }
                        }
                    }
                }
                reader.readLine();
            }
        }
        catch (IOException | NumberFormatException ex) {
            ex.printStackTrace();
        }
        try {
            reader.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static {
        loadTables();
        for (int index = 0; index != LINEAR_LENGTH; ++index) {
            final int mirrorIndex = LITTLE_ENDIAN_RANK_FILE_MAPPING[index];
            PAWN_POSITION_BLACK[mirrorIndex] = PAWN_POSITION_WHITE[index];
            KNIGHT_POSITION_BLACK[mirrorIndex] = KNIGHT_POSITION_WHITE[index];
            BISHOP_POSITION_BLACK[mirrorIndex] = BISHOP_POSITION_WHITE[index];
            ROOK_POSITION_BLACK[mirrorIndex] = ROOK_POSITION_WHITE[index];
            QUEEN_POSITION_BLACK[mirrorIndex] = QUEEN_POSITION_WHITE[index];
            KING_POSITION_BLACK[mirrorIndex] = KING_POSITION_WHITE[index];
        }
        mirroredColumnsEqual(toMatrix(PAWN_POSITION_WHITE), toMatrix(PAWN_POSITION_BLACK));
        mirroredColumnsEqual(toMatrix(BISHOP_POSITION_WHITE), toMatrix(BISHOP_POSITION_BLACK));
        mirroredColumnsEqual(toMatrix(KNIGHT_POSITION_WHITE), toMatrix(KNIGHT_POSITION_BLACK));
        mirroredColumnsEqual(toMatrix(ROOK_POSITION_WHITE), toMatrix(ROOK_POSITION_BLACK));
        mirroredColumnsEqual(toMatrix(QUEEN_POSITION_WHITE), toMatrix(QUEEN_POSITION_BLACK));
        mirroredColumnsEqual(toMatrix(KING_POSITION_WHITE), toMatrix(KING_POSITION_BLACK));
        System.out.println("Table Checking Complete.");
    }

    private static int[][] toMatrix(int[] nums) {
        int[][] matrix = new int[LENGTH][LENGTH];
        int row = 0;
        int column = 0;
        for (int n : nums) {
            if (column == LENGTH) {
                row++;
                column = 0;
            }
            matrix[row][column++] = n;
        }
        return matrix;
    }

    private static void mirroredColumnsEqual(int[][] mat1, int[][] mat2) {
        int column = 0;
        for (int times = 0; times < LENGTH; ++times) {
            List<Integer> vertical = new java.util.ArrayList<>(LENGTH);
            List<Integer> otherVertical = new java.util.ArrayList<>(LENGTH);
            for (int row = 0; row < LENGTH; ++row) {
                vertical.add(mat1[row][column]);
                otherVertical.add(mat2[row][column]);
            }
            java.util.Collections.reverse(otherVertical);
            if (!vertical.equals(otherVertical)) {
                throw new Error();
            }
            column++;
        }
    }
    
    private static void printMatrix(int[] array, String name) {
        System.out.println(name);
        for (int[] row : toMatrix(array)) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println();
    }

    public static void main(String[] args) {
        for (Field variables : Tables.class.getDeclaredFields()) {
            System.out.println("printMatrix(" + variables.getName() + ", " + Quotes.surroundWithDoubleQuotes(variables.getName()) + ");");
        }
        for (Field variables : Tables.class.getDeclaredFields()) {
            System.out.println("import static Engine.Tables." + variables.getName() + ";");
        }
        for (Field variables : Tables.class.getDeclaredFields()) {
            System.out.println("static final int[] " + variables.getName() + " = Engine.Tables." + variables.getName() + ";");
        }
        System.out.println();
        System.out.println();
        System.out.println();
        printMatrix(PAWN_POSITION_WHITE, "PAWN_POSITION_WHITE");
        printMatrix(KNIGHT_POSITION_WHITE, "KNIGHT_POSITION_WHITE");
        printMatrix(BISHOP_POSITION_WHITE, "BISHOP_POSITION_WHITE");
        printMatrix(ROOK_POSITION_WHITE, "ROOK_POSITION_WHITE");
        printMatrix(QUEEN_POSITION_WHITE, "QUEEN_POSITION_WHITE");
        printMatrix(KING_POSITION_WHITE, "KING_POSITION_WHITE");
        System.out.println("------------------------");
        printMatrix(PAWN_POSITION_BLACK, "PAWN_POSITION_BLACK");
        printMatrix(KNIGHT_POSITION_BLACK, "KNIGHT_POSITION_BLACK");
        printMatrix(BISHOP_POSITION_BLACK, "BISHOP_POSITION_BLACK");
        printMatrix(ROOK_POSITION_BLACK, "ROOK_POSITION_BLACK");
        printMatrix(QUEEN_POSITION_BLACK, "QUEEN_POSITION_BLACK");
        printMatrix(KING_POSITION_BLACK, "KING_POSITION_BLACK");
        System.out.println("------------------------");
        printMatrix(LITTLE_ENDIAN_RANK_FILE_MAPPING, "MIRROR");
    }
}