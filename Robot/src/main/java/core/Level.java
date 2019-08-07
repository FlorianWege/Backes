package core;

import javafx.util.Pair;

import javax.annotation.Nonnull;

public class Level {
    private String _name;

    /**
     * Accessor method for the name property.
     *
     * @return      level name
     */
    @Nonnull
    public String getName() {
        return _name;
    }

    @Override
    public String toString() {
        return _name;
    }

    private final Boolean[][] _switchMap;
    private final Integer[][] _heightMap;

    /**
     * Exposes the vertical (y) dimension
     *
     * @return      amount of tiles in y direction
     */
    public int getHeight() {
        return _switchMap[0].length;
    }

    /**
     * Exposes the horizontal (x) dimension
     *
     * @return      amount of tiles in x direction
     */
    public int getWidth() {
        return _switchMap.length;
    }

    /**
     * Accessor method for the matrix of activatable tiles
     *
     * @return      matrix of activatable tiles for reading only
     */
    @Nonnull
    public Boolean[][] getSwitchMap() {
        return _switchMap.clone();
    }

    /**
     * Accessor method for the matrix of heights of each tile
     *
     * @return      matrix of tile heights
     */
    @Nonnull
    public Integer[][] getHeightMap() {
        return _heightMap.clone();
    }

    private final Pair<Integer, Integer> _startPos;

    /**
     * Accessor method for the start position (as given in {@link #Level(String, Boolean[][], Integer[][], Pair, int)}).
     *
     * @return      start position (x/y pair)
     */
    public Pair<Integer, Integer> getStartPos() {
        return _startPos;
    }

    private final int _startDir;

    /**
     * Accessor method for the (as given in {@link #Level(String, Boolean[][], Integer[][], Pair, int)}).
     *
     * @return      start direction ({@link LevelRun#DIR_LEFT}, {@link LevelRun#DIR_DOWN},
     *              {@link LevelRun#DIR_RIGHT}, {@link LevelRun#DIR_UP})
     */
    public int getStartDir() {
        return _startDir;
    }

    /**
     * Constructs a new level with the specified parameters.
     *
     * @param      name         level name
     * @param      switchMap    matrix of the activatable switches
     * @param      heightMap    matrix of the height level for each tile
     * @param      startPos     where the robot starts within the level in relation to the maps
     * @param      startDir     initial facing of the robot, see constants in LevelRun
     */
    public Level(@Nonnull String name, @Nonnull Boolean[][] switchMap, @Nonnull Integer[][] heightMap, @Nonnull Pair<Integer, Integer> startPos, int startDir) {
        _name = name;
        _switchMap = switchMap;
        _heightMap = heightMap;
        _startPos = startPos;
        _startDir = startDir;
    }
}
