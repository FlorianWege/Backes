package misc;

import core.Level;
import core.LevelRun;
import core.RobotAlgo;
import javafx.util.Pair;
import org.testng.Assert;
import org.testng.annotations.Test;
import util.Matrix;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class LevelRunTest {
    @Test()
    public void Test() {
        Boolean[][] switchMap = Matrix.transpose(new Boolean[][] {
                {false, false, false, false, false},
                {false, false, false, false, false},
                {false, false, false, false, true},
                {false, false, false, false, false},
                {false, false, false, false, false}
        });

        Integer[][] heightMap = Matrix.transpose(new Integer[][] {
                {5, 1, 0, 0, 0},
                {0, 1, 0, 0, 0},
                {0, 1, 0, 0, 0},
                {0, 1, 0, 0, 0},
                {0, 1, 0, 0, 0}
        });

        Level level = new Level(getClass().getSimpleName(), switchMap, heightMap, new Pair<>(0, 2), LevelRun.DIR_RIGHT);

        RobotAlgo algo = new RobotAlgo(new String[]{"l", "m", "r", "j", "1", "2"}, new String[]{"j", "m", "m"}, new String[]{"r", "m", "s"});

        final boolean[] fail = {true};

        LevelRun levelRun = new LevelRun(level, algo, new LevelRun.ActionHandler() {
            @Override
            public void result(boolean success) {
                fail[0] = !success;

                System.out.println(level.getName() + (success ? " solved successfully." : " not solved."));
            }

            @Override
            public void action() {
            }

            @Override
            public void turn(int newDir, int oldDir) {
            }

            @Override
            public void move(int newX, int newY, int oldX, int oldY) {
            }

            @Override
            public void useSwitch(int x, int y) {
            }
        });

        levelRun.exec();

        Assert.assertTrue(!fail[0]);
    }

    @Test()
    public void TestExampleAlgo() throws IOException {
        String pathS = "RobotAlgos/ExampleAlgo.txt";

        URL url = getClass().getClassLoader().getResource(pathS);

        assert url != null : pathS + " not found";

        RobotAlgo algo = new RobotAlgo(new File(url.getFile()));

        String levelName = algo.getAssociatedLevelName();

        assert levelName != null;

        Boolean[][] switchMap = Matrix.transpose(new Boolean[][] {
                {false, false, false, false, false},
                {false, false, false, false, false},
                {false, false, false, false, true},
                {false, false, false, false, false},
                {false, false, false, false, false}
        });

        Integer[][] heightMap = Matrix.transpose(new Integer[][] {
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
        });

        Level level = new Level(algo.getAssociatedLevelName(), switchMap, heightMap, new Pair<>(0, 2), LevelRun.DIR_RIGHT);

        LevelRun levelRun = new LevelRun(level, algo, new LevelRun.ActionHandler() {
            @Override
            public void result(boolean success) {
            }

            @Override
            public void action() {
            }

            @Override
            public void turn(int newDir, int oldDir) {
            }

            @Override
            public void move(int newX, int newY, int oldX, int oldY) {
            }

            @Override
            public void useSwitch(int x, int y) {
            }
        });

        levelRun.exec();
    }
}
