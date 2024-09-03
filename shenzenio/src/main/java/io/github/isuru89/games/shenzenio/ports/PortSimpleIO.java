package io.github.isuru89.games.shenzenio.ports;

import io.github.isuru89.games.shenzenio.Accessible;
import io.github.isuru89.games.shenzenio.SimpleValue;
import io.github.isuru89.games.shenzenio.Value;

import java.util.Optional;

public class PortSimpleIO extends Port {

    public PortSimpleIO(String address) {
        super(address);
    }

    @Override
    public Optional<Integer> peek() {
        return Optional.of(readWithoutModeCheck().getValue());
    }

    @Override
    public Value write(int num) {
        ioMode.set(IOMode.OUTPUT);
        try {
            for (Port linkedPort : linkedPorts) {
                linkedPort.ioMode.set(IOMode.INPUT);
            }
        } catch (RuntimeException e) {
            ioMode.makeAvailable();
            throw e;
        }

        if (value != null) {
            return value.write(num);
        }
        return Value.nonBlocked(0);
    }

    @Override
    public Value read() {
        ioMode.set(IOMode.INPUT);

        return readWithoutModeCheck();
    }

    private Value readWithoutModeCheck() {
        if (value != null) {
            return value.read();
        }
        return Value.nonBlocked(0);
    }

    @Override
    boolean supportsPort(Port port) {
        return (port instanceof PortSimpleIO);
    }

    @Override
    public void tick(int tickNumber) {
        ioMode.makeAvailable();
    }

    @Override
    protected Accessible initialValue() {
        return new SimpleValue();
    }

}
