package io.github.isuru89.games.shenzenio;

import io.github.isuru89.games.shenzenio.ports.CaptureSimpleIO;
import io.github.isuru89.games.shenzenio.ports.InputSimpleIO;
import io.github.isuru89.games.shenzenio.ports.PortSimpleIO;
import io.github.isuru89.games.shenzenio.ports.PortXBus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class PortSimpleIOTest {

    @Test
    void shouldOnlyConnectibleSimpleIO() {
        var port = new PortSimpleIO("p0");
        Assertions.assertThrows(RuntimeException.class, () -> port.connect(new PortXBus("x1")));
        Assertions.assertDoesNotThrow(() -> port.connect(new PortSimpleIO("p1")));
    }

    @Test
    void shouldFailSameConnectionTwiceOrMore() {
        var src = new PortSimpleIO("p0");
        var dest = new PortSimpleIO("p1");
        src.connect(dest);

        Assertions.assertThrows(RuntimeException.class, () -> src.connect(dest));
    }


    @Test
    void shouldFanIn() {
        var src1 = new PortSimpleIO("p0");
        var src2 = new PortSimpleIO("p0");
        var dest = new PortSimpleIO("p0");
        src1.connect(dest);
        src2.connect(dest);

        Utils.assertValue(0, src1.read());
        Utils.assertValue(0, src2.read());
        Utils.assertValue(0, dest.read());

        src1.write(100);

        Utils.assertValue(100, src1.read());
        Utils.assertValue(100, src2.read());
        Utils.assertValue(100, dest.read());

        src2.write(50);
        Utils.assertValue(50, src1.read());
        Utils.assertValue(50, src2.read());
        Utils.assertValue(50, dest.read());

        dest.write(25);
        Utils.assertValue(25, src1.read());
        Utils.assertValue(25, src2.read());
        Utils.assertValue(25, dest.read());
    }


    @Test
    void shouldChain() {
        var src1 = new PortSimpleIO("p0");
        var src2 = new PortSimpleIO("p0");
        var dest = new PortSimpleIO("p0");
        src1.connect(src2);
        src2.connect(dest);

        Utils.assertValue(0, src1.read());
        Utils.assertValue(0, src2.read());
        Utils.assertValue(0, dest.read());

        src1.write(100);

        Utils.assertValue(100, src1.read());
        Utils.assertValue(100, src2.read());
        Utils.assertValue(100, dest.read());

        src2.write(50);
        Utils.assertValue(50, src1.read());
        Utils.assertValue(50, src2.read());
        Utils.assertValue(50, dest.read());

        dest.write(25);
        Utils.assertValue(25, src1.read());
        Utils.assertValue(25, src2.read());
        Utils.assertValue(25, dest.read());
    }


    @Test
    void shouldReturnZeroNonConnected() {
        var port = new PortSimpleIO("p0");
        Utils.assertValue(0, port.read());
    }

    @Test
    void shouldImmediateVisibleAsSoonAsWritten() {
        var src = new PortSimpleIO("p0");
        var dest = new PortSimpleIO("p0");
        src.connect(dest);

        Utils.assertValue(0, dest.read());
        src.write(100);
        Utils.assertValue(100, dest.read());
        Utils.assertValue(100, dest.read());
        src.write(0);
        Utils.assertValue(0, dest.read());
    }

    @Test
    void shouldFanOut() {
        var src = new PortSimpleIO("p0");
        var dest1 = new PortSimpleIO("p0");
        var dest2 = new PortSimpleIO("p0");
        src.connect(dest1);
        src.connect(dest2);

        Utils.assertValue(0, dest1.read());
        Utils.assertValue(0, dest2.read());
        src.write(100);
        Utils.assertValue(100, dest1.read());
        Utils.assertValue(100, dest2.read());
        src.write(0);
        Utils.assertValue(0, dest1.read());
        Utils.assertValue(0, dest2.read());
    }

    @Test
    void shouldBiDirectional() {
        var src = new PortSimpleIO("p0");
        var dest = new PortSimpleIO("p0");
        src.connect(dest);

        Utils.assertValue(0, dest.read());
        Utils.assertValue(0, src.read());
        dest.write(100);
        Utils.assertValue(100, src.read());
        Utils.assertValue(100, dest.read());
        src.write(50);
        Utils.assertValue(50, src.read());
        Utils.assertValue(50, src.read());
        Utils.assertValue(50, dest.read());
        Utils.assertValue(50, dest.read());
    }

    @Test
    void shouldNotCaptureUntilTick() {
        var src = new PortSimpleIO("p0");
        var capture = new CaptureSimpleIO("signal");
        src.connect(capture);
        Runnable ticker = () -> {
            src.tick(1);
            capture.tick(1);
        };

        Utils.assertValue(0, src.read());
        Utils.assertValue(0, capture.read());

        ticker.run();
        src.write(100);
        Utils.assertValue(100, src.read());
        Utils.assertValue(0, capture.read());
    }

    @Test
    void shouldCaptureWhenTick() {
        var src = new PortSimpleIO("p0");
        var capture = new CaptureSimpleIO("signal");
        src.connect(capture);
        Runnable ticker = () -> {
            src.tick(1);
            capture.tick(1);
        };

        Utils.assertValue(0, src.read());
        Utils.assertValue(0, capture.read());

        src.write(100);
        ticker.run();

        Utils.assertValue(100, src.read());
        Utils.assertValue(100, capture.read());
    }


    @Test
    void shouldStartWithFirstValueWithAInput() {
        var input = new InputSimpleIO("sensor", Arrays.asList(1, 2, 3));
        var port = new PortSimpleIO("p1");
        input.connect(port);

        Utils.assertValue(1, port.read());
        Utils.assertValue(1, input.read());

        // repeatable read
        Utils.assertValue(1, port.read());
        Utils.assertValue(1, input.read());
    }


    @Test
    void shouldStartWithFirstValueWithAInputConnectionDirectionDoesNotMatter() {
        var input = new InputSimpleIO("signal", Arrays.asList(1, 2, 3));
        var port = new PortSimpleIO("p0");
        port.connect(input);

        Utils.assertValue(1, port.read());
        Utils.assertValue(1, input.read());

        // repeatable read
        Utils.assertValue(1, port.read());
        Utils.assertValue(1, input.read());
    }

    @Test
    void shouldIterateWithEachTickWithAInput() {
        var input = new InputSimpleIO("signal", Arrays.asList(1, 2, 3));
        var dest = new PortSimpleIO("p0");
        input.connect(dest);
        Runnable ticker = () -> {
            input.tick(1);
            dest.tick(1);
        };

        Utils.assertValue(1, dest.read());
        Utils.assertValue(1, input.read());
        ticker.run();

        Utils.assertValue(2, input.read());
        Utils.assertValue(2, input.read());
        Utils.assertValue(2, dest.read());
        Utils.assertValue(2, dest.read());
        ticker.run();

        Utils.assertValue(3, dest.read());
        ticker.run();

        Utils.assertValue(1, input.read());
        Utils.assertValue(1, input.read());
        Utils.assertValue(1, dest.read());
        Utils.assertValue(1, dest.read());
    }

    @Test
    void shouldFanOutWithInput() {
        var input = new InputSimpleIO("signal", Arrays.asList(1, 2, 3, 4, 5));
        var port1 = new PortSimpleIO("p0");
        var port2 = new PortSimpleIO("p0");
        input.connect(port1);
        input.connect(port2);
        Runnable ticker = () -> {
            input.tick(1);
            port1.tick(1);
            port2.tick(1);
        };

        Utils.assertValue(1, port1.read());
        Utils.assertValue(1, port2.read());

        ticker.run();

        Utils.assertValue(2, port1.read());
        Utils.assertValue(2, port1.read());
        Utils.assertValue(2, port2.read());
        Utils.assertValue(2, port2.read());

        ticker.run();
        port1.write(0);

        Utils.assertValue(0, port1.read());
        Utils.assertValue(0, port1.read());
        Utils.assertValue(0, port2.read());
        Utils.assertValue(0, port2.read());
    }

}