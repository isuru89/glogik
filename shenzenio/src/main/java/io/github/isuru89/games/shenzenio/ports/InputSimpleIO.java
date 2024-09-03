package io.github.isuru89.games.shenzenio.ports;

import io.github.isuru89.games.shenzenio.SimpleValue;
import io.github.isuru89.games.shenzenio.Value;

import java.util.List;

public class InputSimpleIO extends PortSimpleIO {

    private final List<Integer> values;
    private int currPos;

    public InputSimpleIO(String address, List<Integer> values) {
        super(address);
        
        this.values = values;
        this.value = new SimpleValue(values.get(0));
    }

    @Override
    public Value read() {
        return Value.nonBlocked(values.get(currPos % values.size()));
    }

    @Override
    public Value write(int num) {
        throw new RuntimeException("not allowed to write!");
    }

    @Override
    public void tick(int tickNumber) {
        currPos++;
        super.write(read().getValue());
    }
}
