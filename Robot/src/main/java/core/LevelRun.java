package core;

import javafx.util.Pair;

import javax.annotation.Nonnull;
import java.util.LinkedHashSet;
import java.util.Set;

public class LevelRun {
    private final Level _level;
    private final RobotAlgo _algo;

    private final Set<Pair<Integer, Integer>> _remainingSwitches = new LinkedHashSet<>();

    /**
     * Switches that still need to be activated for victory.
     *
     * @return      set of remaining switch locations (x/y pairs)
     */
    public Set<Pair<Integer, Integer>> getRemainingSwitches() {
        return new LinkedHashSet<>(_remainingSwitches);
    }

    private int _curX;

    public int getCurX() {
        return _curX;
    }

    private int _curY;

    public int getCurY() {
        return _curY;
    }

    public final static int DIR_LEFT = 0;
    public final static int DIR_DOWN = 1;
    public final static int DIR_RIGHT = 2;
    public final static int DIR_UP = 3;

    private int _dir;

    public int getDir() {
        return _dir;
    }

    private boolean _started = false;
    private boolean _over = false;

    public interface ActionHandler {
        /**
         * LevelRun has finished. Is called directly at the end of {@link #exec()} after the given algorithm has
         * been completed.
         *
         * @param success   all switched are activated
         */
        void result(boolean success);

        /**
         * Called after each action of the robot algorithm.
         */
        void action();

        /**
         * Called when a turn action took place.
         *
         * @param newDir    new direction
         * @param oldDir    old direction
         */
        void turn(int newDir, int oldDir);

        /**
         * Called when a move action took place.
         *
         * @param newX      new x
         * @param newY      new y
         * @param oldX      previous x
         * @param oldY      previous y
         */
        void move(int newX, int newY, int oldX, int oldY);

        /**
         * Called when a switch tile has been flipped (on/off).
         *
         * @param x         x coordinate of the switch
         * @param y         y coordinate of the switch
         */
        void useSwitch(int x, int y);
    }

    private final ActionHandler _actionHandler;

    private void finish(boolean success) {
        if (_over) return;

        _over = true;

        System.out.println(_level.getName() + (success ? " solved successfully." : " not solved."));

        _actionHandler.result(success);
    }

    private void moveForward(boolean jump) {
        int oldX = _curX;
        int oldY = _curY;

        int newX = oldX;
        int newY = oldY;

        switch (_dir) {
            case DIR_LEFT:
                // left
                if (newX > 0) newX--;

                break;
            case DIR_UP:
                // up
                if (newY > 0) newY--;

                break;
            case DIR_RIGHT:
                // right
                if (newX < _level.getWidth() - 1) newX++;

                break;
            case DIR_DOWN:
                // down
                if (newY < _level.getHeight() - 1) newY++;

                break;
        }

        int oldHeight = _level.getHeightMap()[oldX][oldY];
        int newHeight = _level.getHeightMap()[newX][newY];

        if ((!jump && newHeight == oldHeight) || (jump && Math.abs(newHeight - oldHeight) == 1)) {
            _curX = newX;
            _curY = newY;

            _actionHandler.move(_curX, _curY, oldX, oldY);
        } else {
            System.out.println("bump");
        }
    }

    private void turn(boolean right) {
        int oldDir = _dir;

        if (right) _dir = _dir == 0 ? 3 : _dir - 1;
        else _dir = _dir == 3 ? 0 : _dir + 1;

        _actionHandler.turn(_dir, oldDir);
    }

    private void applyAction(@Nonnull RobotAction action) {
        RobotAction.Type actionType = action.getType();

        switch (actionType) {
            case ROTATE_LEFT:
                turn(false);

                break;
            case ROTATE_RIGHT:
                turn(true);

                break;
            case MOVE:
                moveForward(false);

                break;
            case JUMP:
                moveForward(true);

                break;
            case SWITCH:
                if (_level.getSwitchMap()[_curX][_curY]) {
                    Pair<Integer, Integer> pos = new Pair<>(_curX, _curY);

                    if (_remainingSwitches.contains(pos)) {
                        _remainingSwitches.remove(new Pair<>(_curX, _curY));
                    } else {
                        _remainingSwitches.add(new Pair<>(_curX, _curY));
                    }

                    _actionHandler.useSwitch(_curX, _curY);
                }

                break;
            case F1:
                startGroup(_algo.getF1Group());

                break;
            case F2:
                startGroup(_algo.getF2Group());

                break;
        }

        _actionHandler.action();
    }

    private void startGroup(@Nonnull RobotAlgo.Group group) {
        for (RobotAction action : group.getActions()) {
            if (_over) break;

            applyAction(action);
        }
    }

    private void startAlgo(@Nonnull RobotAlgo algo) {
        startGroup(algo.getMainGroup());

        finish(_remainingSwitches.isEmpty());
    }

    /**
     * Executes the run. One-shot.
     */
    public void exec() {
        if (_started) throw new IllegalStateException("already started");

        _started = true;

        startAlgo(_algo);
    }

    /**
     * Constructs an individual exec of a level.
     *
     * @param      level            base level
     * @param      algo        algorithm (robot actions) to execute
     * @param      actionHandler    callback for various events during execution
     */
    public LevelRun(@Nonnull Level level, @Nonnull RobotAlgo algo, @Nonnull ActionHandler actionHandler) {
        _level = level;
        _algo = algo;
        _actionHandler = actionHandler;

        _curX = _level.getStartPos().getKey();
        _curY = _level.getStartPos().getValue();
        _dir = _level.getStartDir();

        Boolean[][] switchMap = level.getSwitchMap();

        for (int x = 0; x < _level.getWidth(); x++) {
            for (int y = 0; y < _level.getHeight(); y++) {
                if (switchMap[x][y]) {
                    _remainingSwitches.add(new Pair<>(x, y));
                }
            }
        }
    }
}
