package io.github.isuru89.games.shenzenio;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

public class BufferValue implements Accessible {

    private final ReentrantLock lock = new ReentrantLock();
    private int value;
    private boolean hasValue;

    @Override
    public Value write(int newValue) {
        try {
            lock.lock();

            if (hasValue) {
                return Value.blocked();
            }

            value = newValue;
            hasValue = true;

            return Value.nonBlocked(newValue);

        } finally {
            lock.unlock();
        }
    }

    @Override
    public Value read() {
        try {
            lock.lock();

            if (!hasValue) {
                return Value.blocked();
            }

            int tmp = value;
            value = 0;
            hasValue = false;

            return Value.nonBlocked(tmp);

        } finally {
            lock.unlock();
        }
    }

    public Optional<Integer> peek() {
        try {
            lock.lock();

            if (hasValue) {
                return Optional.of(value);
            }

            return Optional.empty();

        } finally {
            lock.unlock();
        }
    }

    @Override
    public void reset() {
        try {
            lock.lock();

            value = 0;
            hasValue = false;

        } finally {
            lock.unlock();
        }
    }
}
