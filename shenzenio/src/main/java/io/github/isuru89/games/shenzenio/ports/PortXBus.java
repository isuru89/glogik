package io.github.isuru89.games.shenzenio.ports;

import io.github.isuru89.games.shenzenio.Accessible;
import io.github.isuru89.games.shenzenio.BufferValue;
import io.github.isuru89.games.shenzenio.Value;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class PortXBus extends Port {

    protected final List<Port> priorityPorts = new LinkedList<>();

    public PortXBus(String address) {
        super(address);
    }

    @Override
    public void connect(Port other) {
        super.connect(other);

        if (other instanceof PortXBus xBusPort && xBusPort.isSink()) {
            addPriorityPort(other);
        } else if (other instanceof InputXBus) {
            addPriorityPort(other);
        }
    }

    @Override
    protected Accessible initialValue() {
        return new BufferValue();
    }

    void addPriorityPort(Port port) {
        this.priorityPorts.add(port);
    }

    protected boolean isSink() {
        return false;
    }

    @Override
    boolean supportsPort(Port port) {
        return (port instanceof PortXBus);
    }

    @Override
    public Optional<Integer> peek() {
        if (value == null) {
            return Optional.empty();
        }
        return value.peek();
    }

    @Override
    public Value write(int num) {
        if (!priorityPorts.isEmpty()) {
            for (var port : priorityPorts) {
                port.write(num);
            }
            return Value.nonBlocked(num);
        }

        return value.write(num);
    }

    @Override
    public Value read() {
        if (!priorityPorts.isEmpty()) {
            for (var port : priorityPorts) {
                return port.read();
            }
        }

        if (value == null) {
            return Value.blocked();
        }
        return value.read();
    }
}
