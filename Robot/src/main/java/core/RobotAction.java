package core;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class RobotAction {
    private final static Map<String, Type> _typesMap = new HashMap<>();

    /**
     * available robot actions
     */
    public enum Type {
        ROTATE_LEFT("l"),
        ROTATE_RIGHT("r"),
        MOVE("m"),
        JUMP("j"),
        SWITCH("s"),
        F1("1"),
        F2("2");

        Type(@Nonnull String s) {
            _typesMap.put(s, this);
        }
    }

    private final Type _type;

    @Nonnull
    public Type getType() {
        return _type;
    }

    @Override
    public String toString() {
        return _type.toString();
    }

    /**
     *
     *
     * @param type
     */
    public RobotAction(@Nonnull Type type) {
        _type = type;
    }

    /**
     * Creates a robot action from given string (refers to the keys in {@link Type})
     *
     * @param typeS
     */
    public RobotAction(@Nonnull String typeS) {
        Type.values(); // dirty hack to force initialization of types to be put in the map

        if (!_typesMap.containsKey(typeS)) throw new IllegalArgumentException("invalid type " + typeS);

        _type = _typesMap.get(typeS);
    }
}
