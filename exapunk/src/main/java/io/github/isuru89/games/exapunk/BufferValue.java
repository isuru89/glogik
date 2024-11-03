package io.github.isuru89.games.exapunk;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

public class BufferValue {

    private final ReentrantLock lock = new ReentrantLock();
    private final Queue<ExA> readQueue = new ConcurrentLinkedQueue<>();
    private final Queue<ExA> writeQueue = new ConcurrentLinkedQueue<>();
    private String value;
    private boolean hasValue;

    public void submitRead(ExA exA) {
        if (!readQueue.contains(exA)) {
            readQueue.add(exA);
        }
    }

    public void submitWrite(ExA exA) {
        if (!writeQueue.contains(exA)) {
            writeQueue.add(exA);
        }
    }

    public void withdrawRead(ExA exA) {
        readQueue.removeIf(e -> e.equals(exA));
    }

    public void withdrawWrite(ExA exA) {
        writeQueue.removeIf(e -> e.equals(exA));
    }

    public Value write(String newValue) {
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

    public Value read() {
        try {
            lock.lock();

            if (!hasValue) {
                return Value.blocked();
            }

            var tmp = value;
            value = "";
            hasValue = false;

            return Value.nonBlocked(tmp);

        } finally {
            lock.unlock();
        }
    }

    public Optional<String> peek() {
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

    public void reset() {
        try {
            lock.lock();

            value = "";
            hasValue = false;

        } finally {
            lock.unlock();
        }
    }
}
