package io.github.isuru89.games.shenzenio.ports;

import io.github.isuru89.games.shenzenio.Value;

import java.util.List;

public class InputXBus extends PortXBus {

    private final Nature nature;

    private final List<Integer> values;
    private int curPos;

    private InputXBus(String address, Nature nature, List<Integer> values) {
        super(address);

        this.nature = nature;
        this.values = values;
        curPos = 0;
    }

    public static InputXBus withConstantValue(String address, int value) {
        return new InputXBus(address, Nature.ALWAYS_SAME_VALUE, List.of(value));
    }

    public static InputXBus withRepeatable(String address, List<Integer> values) {
        return new InputXBus(address, Nature.REPEATABLE, List.copyOf(values));
    }

    public static InputXBus withSequential(String address, List<Integer> sequence) {
        return new InputXBus(address, Nature.NON_REPEATABLE, List.copyOf(sequence));
    }

    @Override
    public void connect(Port other) {
        super.connect(other);

        if (other instanceof PortXBus xBus) {
            xBus.priorityPorts.add(this);
        }
    }

    @Override
    public Value read() {
        if (values.isEmpty()) {
            throw new RuntimeException("no values specified in input!");
        }

        if (nature == Nature.ALWAYS_SAME_VALUE) {
            return Value.nonBlocked(values.get(0));
        }

        if (nature == Nature.REPEATABLE) {
            if (curPos >= values.size()) {
                curPos = 0;
            }
        }

        if (nature == Nature.NON_REPEATABLE) {
            if (curPos >= values.size()) {
                throw new RuntimeException("not enough values!");
            }
        }

        return Value.nonBlocked(values.get(curPos++));
    }

    enum Nature {
        ALWAYS_SAME_VALUE,
        REPEATABLE,
        NON_REPEATABLE
    }

}
