package io.github.isuru89.games.shenzenio.ports;

import io.github.isuru89.games.shenzenio.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CaptureXBus extends PortXBus {

    private final List<Integer> values;

    public CaptureXBus(String address) {
        super(address);
        
        this.values = new ArrayList<>();
    }

    @Override
    public Value read() {
        if (values.isEmpty()) {
            return Value.blocked();
        }

        Integer lastVal = values.getLast();
        if (lastVal == null) {
            return Value.NON_EXISTENCE;
        }

        return Value.nonBlocked(values.getLast());
    }

    public List<Integer> getAllValues() {
        return Collections.unmodifiableList(values);
    }

    @Override
    public void reset() {
        super.reset();

        this.values.clear();
    }

    @Override
    protected boolean isSink() {
        return true;
    }

    @Override
    public Value write(int num) {
        values.add(num);
        return Value.nonBlocked(num);
    }

}
