package io.github.isuru89.games.exapunk;

public class Value {

    public static Value NON_EXISTENCE = Value.nonBlocked("");

    private final boolean isBlocked;
    private final String value;

    private Value(String value) {
        this(value, false);
    }

    private Value(String value, boolean isBlocked) {
        this.value = value;
        this.isBlocked = isBlocked;
    }

    public static Value blocked() {
        return new Value("", true);
    }

    public static Value nonBlocked(String num) {
        return new Value(num);
    }

    public String getValue() {
        return value;
    }

    public boolean isBlocked() {
        return isBlocked;
    }
}
