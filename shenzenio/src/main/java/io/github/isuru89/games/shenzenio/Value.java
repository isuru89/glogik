package io.github.isuru89.games.shenzenio;

public class Value {

    public static Value NON_EXISTENCE = Value.nonBlocked(Integer.MIN_VALUE);

    private final boolean isBlocked;
    private final int value;

    private Value(int value) {
        this(value, false);
    }

    private Value(int value, boolean isBlocked) {
        this.value = value;
        this.isBlocked = isBlocked;
    }

    public static Value blocked() {
        return new Value(0, true);
    }

    public static Value nonBlocked(int num) {
        return new Value(num);
    }

    public int getValue() {
        return value;
    }

    public boolean isBlocked() {
        return isBlocked;
    }
}
