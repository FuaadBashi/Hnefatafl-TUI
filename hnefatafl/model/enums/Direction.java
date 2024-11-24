package ws.aperture.hnefatafl.model.enums;

import java.util.HashMap;

public enum Direction {
    RIGHT,
    UP,
    LEFT,
    DOWN;

    private static final HashMap<Direction, int[]> DELTAS_MAP;
    private static final int[][] DELTAS = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}}; // UP, RIGHT, DOWN, LEFT

    static {
        DELTAS_MAP = new HashMap<Direction, int[]>();

        DELTAS_MAP.put( UP, DELTAS[0]);
        DELTAS_MAP.put( RIGHT, DELTAS[1]);
        DELTAS_MAP.put( DOWN, DELTAS[2]);
        DELTAS_MAP.put( LEFT, DELTAS[3]);

        // DELTAS_MAP.put( RIGHT, DELTAS[0]);
        // DELTAS_MAP.put( UP, DELTAS[1]);
        // DELTAS_MAP.put( LEFT, DELTAS[2]);
        // DELTAS_MAP.put( DOWN, DELTAS[3]);
    }

    public static Direction opposite( Direction d) {
        switch (d) {
            case RIGHT:
                return LEFT;
            case LEFT:
                return RIGHT;
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            default:
                return UP;
        }
    }

    public static int[] getDelta( Direction d ) {
        return DELTAS_MAP.get(d);
    }

    /*
     *  Should only be called if onEdge == true
     * 
     */
    public static Direction getInner( int row, int col ) {
        if ( row == 0 ) {
            return UP;
        } else if ( row == 10 ) {
            return DOWN;
        } else if ( col == 0) {
            return RIGHT;
        } else if ( col == 10) {
            return LEFT;
        } else {
            return UP;
        }
    }

    public static Direction getOuter( int row, int col)  {
        return Direction.opposite( getInner(row, col) );
    }
    
}
