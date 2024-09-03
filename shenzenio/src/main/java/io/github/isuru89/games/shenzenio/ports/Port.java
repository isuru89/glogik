package io.github.isuru89.games.shenzenio.ports;

import io.github.isuru89.games.shenzenio.Accessible;
import io.github.isuru89.games.shenzenio.ResetHandler;
import io.github.isuru89.games.shenzenio.TickHandler;

import java.util.HashSet;
import java.util.Set;

public abstract class Port implements ResetHandler, TickHandler, Accessible {

    protected final Set<Port> linkedPorts = new HashSet<>();
    private final String address;
    protected StatefulMode ioMode = new StatefulMode(IOMode.UNDEFINED);
    protected Accessible value;

    protected Port(String address) {
        this.address = address;
    }

    abstract boolean supportsPort(Port port);

    public final boolean isConnected() {
        return !linkedPorts.isEmpty();
    }

    public void connect(Port other) {
        if (!supportsPort(other)) {
            throw new RuntimeException("Port mismatch!");
        }

        if (value == null && other.value == null) {
            value = initialValue();
            other.value = value;
            linkedPorts.add(other);
            other.linkedPorts.add(this);
        } else if (value == null) {
            value = other.value;
            linkedPorts.add(other);
            other.linkedPorts.add(this);
        } else if (other.value == null) {
            other.value = value;
            linkedPorts.add(other);
            other.linkedPorts.add(this);
        } else if (value == other.value) {
            throw new RuntimeException("already connected");
        }
    }

    protected abstract Accessible initialValue();

    @Override
    public void reset() {
        ioMode.makeAvailable();
        ioMode.set(IOMode.UNDEFINED);
        value.reset();
    }

    public void tick(int tickNumber) {
    }

    public String getAddress() {
        return address;
    }

    public IOMode getIOMode() {
        return ioMode.peek();
    }
}
