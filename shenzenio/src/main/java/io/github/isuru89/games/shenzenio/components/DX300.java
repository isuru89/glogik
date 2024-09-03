package io.github.isuru89.games.shenzenio.components;

import io.github.isuru89.games.shenzenio.BlockType;
import io.github.isuru89.games.shenzenio.GameClock;
import io.github.isuru89.games.shenzenio.Value;
import io.github.isuru89.games.shenzenio.ports.IOMode;
import io.github.isuru89.games.shenzenio.ports.Port;
import io.github.isuru89.games.shenzenio.ports.PortSimpleIO;
import io.github.isuru89.games.shenzenio.ports.PortXBus;

public class DX300 extends Component {
    public DX300(String id) {
        super(id, new Configuration()
                .addPort(new PortXBus("x0"))
                .addPort(new PortXBus("x1"))
                .addPort(new PortXBus("x2"))
                .addPort(new PortSimpleIO("p0"))
                .addPort(new PortSimpleIO("p1"))
                .addPort(new PortSimpleIO("p2"))
        );
    }

    @Override
    public BlockType execute(GameClock clock) {
        var x0 = getPort("x0");
        var x1 = getPort("x1");
        var x2 = getPort("x2");
        var p0 = getPort("p0");
        var p1 = getPort("p1");
        var p2 = getPort("p2");

        if (isXBusPortReadReady(x0)) {
            return writeToSimpleIO(x0.read().getValue(), p0, p1, p2);
        } else if (isXBusPortReadReady(x1)) {
            return writeToSimpleIO(x1.read().getValue(), p0, p1, p2);
        } else if (isXBusPortReadReady(x2)) {
            return writeToSimpleIO(x2.read().getValue(), p0, p1, p2);
        }

        if (isXBusPortWriteReady(x0)) {
            int number = deriveSimpleIOValue(p0, p1, p2);
            return writeToXBus(number, x0, x1, x2);
        } else if (isXBusPortWriteReady(x1)) {
            int number = deriveSimpleIOValue(p0, p1, p2);
            return writeToXBus(number, x0, x1, x2);
        } else if (isXBusPortWriteReady(x2)) {
            int number = deriveSimpleIOValue(p0, p1, p2);
            return writeToXBus(number, x0, x1, x2);
        }

        return BlockType.IO;
    }

    private boolean isXBusPortReadReady(Port x) {
        return x.isConnected() && x.getIOMode() == IOMode.INPUT && x.peek().isPresent();
    }

    private boolean isXBusPortWriteReady(Port x) {
        return x.isConnected() && x.getIOMode() != IOMode.INPUT;
    }

    private int deriveSimpleIOValue(Port p0, Port p1, Port p2) {
        int n0 = p0.read().getValue();
        int n1 = p1.read().getValue();
        int n2 = p2.read().getValue();

        return (n2 == 0 ? 0 : 1) * 100 + (n1 == 0 ? 0 : 1) * 10 + (n0 == 0 ? 0 : 1);
    }

    private BlockType writeToSimpleIO(int number, Port p0, Port p1, Port p2) {
        int[] result = processedValues(number);
        p2.write(result[0] == 0 ? 0 : 100);
        p1.write(result[1] == 0 ? 0 : 100);
        p0.write(result[2] == 0 ? 0 : 100);

        return BlockType.NONE;
    }

    private BlockType writeToXBus(int number, Port x0, Port x1, Port x2) {
        Value value = Value.blocked();
        if (x0.isConnected()) {
            value = x0.write(number);
        } else if (x1.isConnected()) {
            value = x1.write(number);
        } else if (x2.isConnected()) {
            value = x2.write(number);
        }

        return value.isBlocked() ? BlockType.IO : BlockType.NONE;
    }

    int[] processedValues(int input) {
        return new int[]{
                input / 100,
                (input % 100) / 10,
                input % 10
        };
    }
}
