package io.github.isuru89.games.shenzenio.ports;

import io.github.isuru89.games.shenzenio.Accessible;
import io.github.isuru89.games.shenzenio.ResetHandler;
import io.github.isuru89.games.shenzenio.TickHandler;

public abstract class Port implements ResetHandler, TickHandler, Accessible {

    private final String address;

    protected Accessible value;

    protected Port(String address) {
        this.address = address;
    }

    abstract boolean supportsPort(Port port);

    public void connect(Port other) {
        if (!supportsPort(other)) {
            throw new RuntimeException("Port mismatch!");
        }

        if (value == null && other.value == null) {
            value = initialValue();
            other.value = value;
        } else if (value == null) {
            value = other.value;
        } else if (other.value == null) {
            other.value = value;
        } else if (value == other.value) {
            throw new RuntimeException("already connected");
        }
    }

    protected abstract Accessible initialValue();

    @Override
    public void reset() {
        value.reset();
    }

    public void tick(int tickNumber) {
    }

    public String getAddress() {
        return address;
    }
}
