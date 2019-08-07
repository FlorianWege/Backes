import java.util.Stack;

public class MinStack {
    private Stack<Integer> _stack;

    private Integer _min = null;

    public Integer min() {
        return _min;
    }

    public void push(int el) {
        if (el < _min) _min = el;

        _stack.add(el);
    }

    public Integer pop() {
        return _stack.pop();
    }
}
