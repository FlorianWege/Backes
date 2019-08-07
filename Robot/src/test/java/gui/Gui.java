package gui;

import core.Level;
import core.LevelRun;
import core.RobotAlgo;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import util.Matrix;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Set;

public class Gui extends Application {
    private final Set<Thread> _threads = new LinkedHashSet<>();

    private final Group _root = new Group();

    private class GameScene extends Scene {
        private Level _level;
        private LevelRun _levelRun;

        private class Arrow extends Group {
            private Tile _curTile = null;

            private final Group _inner;

            private void setTile(@Nullable Tile tile) {
                if (_curTile != null) _curTile.getChildren().remove(this);

                _curTile = tile;

                if (_curTile != null) {
                    setTranslateZ(_curTile.getBox().getTranslateZ() - _curTile.getBox().getDepth() / 2 - 20);

                    _curTile.getChildren().add(this);
                }
            }

            private void setDir(int dir) {
                _inner.setRotate((1 - dir) * 90);
            }

            private Arrow() {
                Cylinder cylinder = new Cylinder();

                cylinder.setHeight(20);
                cylinder.setRadius(2);

                cylinder.setMaterial(new PhongMaterial(Color.GREEN));

                Group tip = new Group();

                tip.setTranslateY(10);

                Cylinder left = new Cylinder();

                tip.getChildren().add(left);

                left.setRotate(-25);

                left.setHeight(15);
                left.setRadius(2);

                left.setMaterial(new PhongMaterial(Color.GREEN));

                left.setTranslateX(-5);

                Cylinder right = new Cylinder();

                tip.getChildren().add(right);

                right.setRotate(25);

                right.setHeight(15);
                right.setRadius(2);

                right.setMaterial(new PhongMaterial(Color.GREEN));

                right.setTranslateX(5);

                _inner = new Group();

                _inner.getChildren().add(cylinder);
                _inner.getChildren().add(tip);

                _inner.setRotationAxis(new Point3D(0, 0, 1));

                getChildren().add(_inner);
            }
        }

        private final Arrow _arrow;

        private void updateArrow() {
            if (_level == null) _arrow.setTile(null);

            if (_level != null) {
                if (_levelRun == null) {
                    _arrow.setTile(_tiles[_level.getStartPos().getKey()][_level.getStartPos().getValue()]);

                    _arrow.setDir(_level.getStartDir());
                } else {
                    _arrow.setTile(_tiles[_levelRun.getCurX()][_levelRun.getCurY()]);

                    _arrow.setDir(_levelRun.getDir());
                }
            }
        }

        private class Tile extends Group {
            private final Box _box = new Box();

            public Box getBox() {
                return _box;
            }

            public void setSwitch(boolean active) {
                _box.setMaterial(new PhongMaterial(active ? Color.YELLOW : Color.BLUE));
            }

            private Tile(int x, int y) {
                _box.setWidth(TILE_SIZE - 3);
                _box.setHeight(TILE_SIZE - 3);
                _box.setDepth(20 + _level.getHeightMap()[x][y] * TILE_DEPTH);

                _box.setTranslateX(0);
                _box.setTranslateY(0);
                _box.setTranslateZ(-_box.getDepth()/2);

                PhongMaterial material = new PhongMaterial();

                material.setDiffuseColor((_level.getSwitchMap()[x][y]) ? Color.BLUE : Color.WHITE);

                _box.setMaterial(material);

                getChildren().add(_box);
            }
        }

        private Tile[][] _tiles;

        private final int TILE_SIZE = 50;
        private final int TILE_DEPTH = 35;

        private void setLevel(@Nonnull Level level) {
            _level = level;
            int width = ((level.getWidth() - 1) * TILE_SIZE);
            int height = ((level.getHeight() - 1) * TILE_SIZE);

            _tiles = new Tile[level.getWidth()][level.getHeight()];

            Group base = new Group();

            _root.getChildren().add(base);

            base.setTranslateZ(-25);

            for (int y = 0; y < level.getHeight(); y++) {
                for (int x = 0; x < level.getWidth(); x++) {
                    Tile tile = new Tile(x, y);

                    _tiles[x][y] = tile;

                    int x2 = TILE_SIZE * x - width / 2;
                    int y2 = TILE_SIZE * y - height / 2;

                    tile.setTranslateX(x2);
                    tile.setTranslateY(y2);

                    base.getChildren().add(tile);
                }
            }

            Group camGroup = new Group();

            camGroup.setTranslateX(0);
            camGroup.setTranslateY(0);
            camGroup.setTranslateZ(200);

            _root.getChildren().add(camGroup);

            Camera cam = new PerspectiveCamera(true);

            camGroup.getChildren().add(cam);

            cam.setRotationAxis(new Point3D(1, 0, 0));

            /*Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> cam.setRotate(cam.getRotate()+1)));

            timeline.setCycleCount(Timeline.INDEFINITE);

            timeline.play();*/

            //cam.setRotate(15);

            cam.setTranslateX(0);
            //cam.setTranslateY(200);
            cam.setTranslateZ(-1250);

            cam.setFarClip(10000);
            cam.setNearClip(0);

            //_root.getChildren().add(cam);

            setCamera(cam);

            PointLight light = new PointLight(Color.WHITE);

            light.setTranslateZ(-165);

            {
                final double[] colorAngle = {0D};

                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(125), event -> {
                    colorAngle[0] += 1D;

                    colorAngle[0] %= 360D;

                    //light.setColor(Color.hsb(colorAngle[0], 1D, 1D));
                }));

                timeline.setCycleCount(Timeline.INDEFINITE);

                timeline.play();
            }

            base.getChildren().add(light);
        }

        private void run() {
            RobotAlgo algo = new RobotAlgo(new String[]{"l", "m", "r", "m", "1", "2"}, new String[]{"m", "m", "m"}, new String[]{"r", "m", "s", "s", "r", "r", "r", "r", "s", "j", "r", "m", "l", "m", "s"});

            final boolean[] fail = {true};

            _levelRun = new LevelRun(_level, algo, new LevelRun.ActionHandler() {
                @Override
                public void result(boolean success) {
                    fail[0] = !success;
                }

                @Override
                public void action() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void turn(int newDir, int oldDir) {
                    Platform.runLater(() -> updateArrow());
                }

                @Override
                public void move(int newX, int newY, int oldX, int oldY) {
                    Platform.runLater(() -> updateArrow());
                }

                @Override
                public void useSwitch(int x, int y) {
                    _tiles[x][y].setSwitch(!_levelRun.getRemainingSwitches().contains(new Pair<>(x, y)));
                }
            });

            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(2000);

                    _levelRun.exec();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            _threads.add(thread);

            /*thread.setUncaughtExceptionHandler((t, e) -> {
                //mute
            });*/

            thread.start();
        }

        private GameScene() {
            super(_root, 1024, 768, true, SceneAntialiasing.BALANCED);

            _root.setDepthTest(DepthTest.ENABLE);

            _arrow = new Arrow();

            addAxes();

            Boolean[][] switchMap = Matrix.transpose(new Boolean[][] {
                    {false, false, false, false, false, false, false},
                    {false, false, false, false, false, false, false},
                    {false, false, false, false, false, false, false},
                    {false, false, false, false, false, true, false},
                    {false, false, false, false, false, false, false},
                    {false, false, false, false, true, false, false},
                    {false, false, false, false, false, false, false}
            });

            Integer[][] heightMap = Matrix.transpose(new Integer[][] {
                    {3, 3, 3, 3, 3, 3, 3},
                    {3, 3, 1, 0, 0, 0, 3},
                    {3, 0, 0, 0, 0, 0, 3},
                    {3, 0, 2, 0, 0, 0, 3},
                    {3, 0, 2, 0, 1, 1, 3},
                    {3, 0, 2, 0, 1, 0, 3},
                    {3, 3, 3, 3, 3, 3, 3}
            });

            Level level = new Level(Gui.class.getSimpleName(), switchMap, heightMap, new Pair<>(1, 3), LevelRun.DIR_RIGHT);

            setLevel(level);

            updateArrow();
        }
    }

    private void addAxes() {
        Box x = new Box();

        x.setWidth(1000);
        x.setHeight(5);
        x.setDepth(5);

        x.setTranslateX(0);
        x.setTranslateY(0);
        x.setTranslateZ(0);

        x.setMaterial(new PhongMaterial(Color.RED));

        Box y = new Box();

        y.setWidth(5);
        y.setHeight(1000);
        y.setDepth(5);

        y.setMaterial(new PhongMaterial(Color.GREEN));

        Box z = new Box();

        z.setWidth(5);
        z.setHeight(5);
        z.setDepth(1000);

        z.setMaterial(new PhongMaterial(Color.BLUE));

        _root.getChildren().add(x);
        _root.getChildren().add(y);
        _root.getChildren().add(z);
    }

    public void start(Stage primaryStage) {
        GameScene gameScene = new GameScene();

        gameScene.run();

        gameScene.setFill(Color.GREY);

        primaryStage.setTitle("GUI");
        primaryStage.setScene(gameScene);

        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        for (Thread thread : _threads) {
            thread.interrupt();
        }
    }
}
