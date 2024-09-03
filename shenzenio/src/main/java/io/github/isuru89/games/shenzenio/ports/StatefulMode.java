package io.github.isuru89.games.shenzenio.ports;

public class StatefulMode {

    private boolean available;
    private IOMode mode;

    public StatefulMode(IOMode initialValue) {
        this.mode = initialValue;
        this.available = true;
    }

    public void set(IOMode newValue) {
        if (mode == newValue) {
            available = false;
            return;
        }

        if (!available) {
            throw new RuntimeException("unavailable to change mode!");
        }
        available = false;
        mode = newValue;
    }

    public IOMode get() {
        return mode;
    }

    public IOMode peek() {
        return get();
    }

    public boolean isAvailable() {
        return available;
    }

    public void makeAvailable() {
        available = true;
    }
}
