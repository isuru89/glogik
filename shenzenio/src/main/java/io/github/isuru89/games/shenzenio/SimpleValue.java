package io.github.isuru89.games.shenzenio;

import java.util.Optional;

public class SimpleValue implements Accessible {

    private final int initialVal;

    private volatile int value;

    public SimpleValue() {
        this(0);
    }

    public SimpleValue(int initialValue) {
        value = initialValue;
        this.initialVal = initialValue;
    }

    @Override
    public Value write(int newValue) {
        value = newValue;
        return Value.nonBlocked(newValue);
    }

    @Override
    public Value read() {
        return Value.nonBlocked(value);
    }

    @Override
    public Optional<Integer> peek() {
        return Optional.of(value);
    }

    @Override
    public void reset() {
        value = initialVal;
    }
}
