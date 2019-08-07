package core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RobotAlgo {
    /**
     * positional container for robot actions
     */
    public class Group {
        private final List<RobotAction> _actions = new ArrayList<>();

        @Nonnull
        public List<RobotAction> getActions() {
            return _actions;
        }

        private void addAction(@Nonnull RobotAction action) {
            _actions.add(action);
        }

        private Group() {

        }
    }

    private final Group _mainGroup = new Group();
    private final Group _f1Group = new Group();
    private final Group _f2Group = new Group();

    /**
     * Accessor method for the main action group.
     *
     * @return      main action group
     */
    @Nonnull
    public Group getMainGroup() {
        return _mainGroup;
    }

    /**
     * Accessor method for the function 1 action group.
     *
     * @return      function 1 action group
     */
    @Nonnull
    public Group getF1Group() {
        return _f1Group;
    }

    /**
     * Accessor method for the function 2 action group
     *
     * @return      function 2 action group
     */
    @Nonnull
    public Group getF2Group() {
        return _f2Group;
    }

    /**
     * Creates an empty robot algorithm. You can still access the action groups and add actions to them directly.
     */
    public RobotAlgo() {

    }

    /**
     * Creates a robot algorithm from the given action groups as indicated by string arrays. The strings refer
     * to {@link core.RobotAction.Type}.
     *
     * @param mainGroupActions      actions for the main group
     * @param f1GroupActions        actions for function 1
     * @param f2GroupActions        actions for function 2
     */
    public RobotAlgo(@Nonnull String[] mainGroupActions, @Nonnull String[] f1GroupActions, @Nonnull String[] f2GroupActions) {
        for (String actionS : mainGroupActions) {
            _mainGroup.addAction(new RobotAction(actionS));
        }
        for (String actionS : f1GroupActions) {
            _f1Group.addAction(new RobotAction(actionS));
        }
        for (String actionS : f2GroupActions) {
            _f2Group.addAction(new RobotAction(actionS));
        }
    }

    private String _associatedLevelName;

    /**
     * The name for a level as and only if read from the file presented to {@link RobotAlgo#RobotAlgo(File)}
     *
     * @return      level name, may be null
     */
    @Nullable
    public String getAssociatedLevelName() {
        return _associatedLevelName;
    }

    /**
     * Creates a robot algorithm from a given text file.
     * 
     * @param file              file to read from
     * @throws IOException      an IOExeception occurred when trying to handle <code>file</code>
     */
    public RobotAlgo(@Nonnull File file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

        String line = reader.readLine();

        _associatedLevelName = line;

        while ((line = reader.readLine()) != null) {
            Pattern pattern = Pattern.compile("(\\w+):([\\w\\s]+)$");

            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                String groupS = matcher.group(1);
                String actionLine = matcher.group(2);

                Group group = null;

                switch (groupS) {
                    case "main":
                        group = _mainGroup;

                        break;
                    case "f1":
                        group = _f1Group;

                        break;
                    case "f2":
                        group = _f2Group;

                        break;
                }

                if (group == null) throw new IllegalArgumentException("invalid group " + groupS);

                actionLine = actionLine.trim();

                actionLine = actionLine.replaceAll("\\s+", ";");

                String[] actionsS = actionLine.split(";");

                for (String actionS : actionsS) {
                    group.addAction(new RobotAction(actionS));
                }
            }
        }

        reader.close();
    }
}
