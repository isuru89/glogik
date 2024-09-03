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
        return Optional.of(read().getValue());
    }

    @Override
    public Value write(int num) {
        if (value != null) {
            return value.write(num);
        }
        return Value.nonBlocked(0);
    }

    @Override
    public Value read() {
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
    protected Accessible initialValue() {
        return new SimpleValue();
    }

}
