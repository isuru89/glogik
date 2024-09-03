package io.github.isuru89.games.shenzenio.ports;

import io.github.isuru89.games.shenzenio.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CaptureSimpleIO extends PortSimpleIO {

    private final List<Integer> values;

    public CaptureSimpleIO(String address) {
        super(address);
        
        this.values = new ArrayList<>();
    }

    public void tick(int tickNumber) {
        var value = super.read();
        if (value.isBlocked()) {
            return;
        }

        values.add(value.getValue());
    }

    @Override
    public Value read() {
        if (values.isEmpty()) {
            return Value.nonBlocked(0);
        }

        return Value.nonBlocked(values.getLast());
    }

    @Override
    public void reset() {
        super.reset();

        this.values.clear();
    }

    public List<Integer> getAllValues() {
        return Collections.unmodifiableList(values);
    }

    @Override
    public String toString() {
        return "Capture{" +
                "values=" + values.stream().map(v -> StringUtils.leftPad(String.valueOf(v), 3)).collect(Collectors.joining("|")) +
                '}';
    }
}
