package Engine;

import java.util.HashMap;
import java.util.Map;

//used to save evaluation scores of positions, to avoid redudant recalculations
//could use a databasee not just in the evaluation functions
//but in the tree search, save a grid&depth as a key and a score as a value
//which may elminate entire branches!

//maybe encode bitboards or strings
public final class Database {

    private final Map<Grid, Integer> SAVED_POSITION_VALUES = new HashMap<>(1000);

    public Database() {

    }

    public void putEntry(Grid grid, int score) {
        SAVED_POSITION_VALUES.put(grid, score);
    }
    
    public void putEntryIfAbsent(Grid grid, int score) {
        SAVED_POSITION_VALUES.putIfAbsent(grid, score);
    }

    public boolean containsEntry(Grid grid) {
        return SAVED_POSITION_VALUES.containsKey(grid);
    }

    public int getValue(Grid grid) {
        return SAVED_POSITION_VALUES.get(grid);
    }
    
    public int size() {
        return SAVED_POSITION_VALUES.size();
    }
    
    public void clear() {
        SAVED_POSITION_VALUES.clear();
    }
}