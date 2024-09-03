package io.github.isuru89.games.shenzenio;

import io.github.isuru89.games.shenzenio.ports.CaptureSimpleIO;
import io.github.isuru89.games.shenzenio.ports.IOMode;
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
        var ticks = Utils.withTickers(src1, src2, dest);

        src1.write(100);

        Assertions.assertThrows(RuntimeException.class, src1::read);
        Utils.assertMode(IOMode.INPUT, dest);
        Utils.assertValue(100, src2.read());
        Utils.assertValue(100, dest.read());
        Assertions.assertThrows(RuntimeException.class, () -> src2.write(75));
        Assertions.assertThrows(RuntimeException.class, () -> dest.write(75));

        ticks.tick(1);
        src2.write(50);
        Utils.assertMode(IOMode.INPUT, dest);
        Utils.assertValue(50, src1.read());
        Assertions.assertThrows(RuntimeException.class, src2::read);
        Utils.assertValue(50, dest.read());
        Assertions.assertThrows(RuntimeException.class, () -> src1.write(75));
        Assertions.assertThrows(RuntimeException.class, () -> dest.write(75));

        ticks.tick(2);
        dest.write(25);
        Utils.assertValue(25, src1.read());
        Utils.assertValue(25, src2.read());
        Assertions.assertThrows(RuntimeException.class, dest::read);
        Assertions.assertThrows(RuntimeException.class, () -> src1.write(75));
        Assertions.assertThrows(RuntimeException.class, () -> src2.write(75));
    }

    @Test
    void shouldChain() {
        var src1 = new PortSimpleIO("p0");
        var src2 = new PortSimpleIO("p0");
        var dest = new PortSimpleIO("p0");
        src1.connect(src2);
        src2.connect(dest);
        var ticks = Utils.withTickers(src1, src2, dest);

        src1.write(100);

        Assertions.assertThrows(RuntimeException.class, src1::read);
        Utils.assertMode(IOMode.INPUT, src2);
        Utils.assertValue(100, src2.read());
        Utils.assertValue(100, dest.read());
        Assertions.assertThrows(RuntimeException.class, () -> src2.write(75));
        Assertions.assertThrows(RuntimeException.class, () -> dest.write(75));

        ticks.tick(1);
        src2.write(50);
        Utils.assertValue(50, src1.read());
        Assertions.assertThrows(RuntimeException.class, src2::read);
        Utils.assertValue(50, dest.read());
        Assertions.assertThrows(RuntimeException.class, () -> src1.write(75));
        Assertions.assertThrows(RuntimeException.class, () -> dest.write(75));

        ticks.tick(2);
        dest.write(25);
        Utils.assertValue(25, src1.read());
        Utils.assertValue(25, src2.read());
        Assertions.assertThrows(RuntimeException.class, dest::read);
        Assertions.assertThrows(RuntimeException.class, () -> src1.write(75));
        Assertions.assertThrows(RuntimeException.class, () -> src2.write(75));
    }

    @Test
    void shouldReturnZeroWhenNotConnected() {
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
    void shouldSetLinkedPortAsInputWhenWritten() {
        var src = new PortSimpleIO("p0");
        var dest = new PortSimpleIO("p0");
        src.connect(dest);

        Utils.assertMode(IOMode.UNDEFINED, dest);
        Utils.assertValue(0, dest.read());
        src.write(100);
        Utils.assertMode(IOMode.OUTPUT, src);
        Utils.assertMode(IOMode.INPUT, dest);
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
    void shouldNotBeBiDirectionalWithinTheSameTick() {
        var src = new PortSimpleIO("p0");
        var dest = new PortSimpleIO("p0");
        src.connect(dest);

        src.write(50);
        Utils.assertMode(IOMode.OUTPUT, src);
        Utils.assertValue(50, dest.read());
        Utils.assertMode(IOMode.INPUT, dest);
        Assertions.assertThrows(RuntimeException.class, () -> dest.write(75));
    }

    @Test
    void canBeBiDirectionalInContinousTicks() {
        var src = new PortSimpleIO("p0");
        var dest = new PortSimpleIO("p0");
        src.connect(dest);
        var ticks = Utils.withTickers(src, dest);

        src.write(50);
        Utils.assertMode(IOMode.OUTPUT, src);
        Utils.assertMode(IOMode.INPUT, dest);
        Utils.assertValue(50, dest.read());

        ticks.tick(1);
        dest.write(75);
        Utils.assertMode(IOMode.INPUT, src);
        Utils.assertMode(IOMode.OUTPUT, dest);
        Utils.assertValue(75, src.read());

        ticks.tick(2);
        src.write(100);
        Utils.assertMode(IOMode.OUTPUT, src);
        Utils.assertMode(IOMode.INPUT, dest);
        Utils.assertValue(100, dest.read());
    }

    @Test
    void readShouldSeeLatestValueIfMultipleWritersConnected() {
        var reader = new PortSimpleIO("r0");
        var writer1 = new PortSimpleIO("w0");
        var writer2 = new PortSimpleIO("w1");
        reader.connect(writer1);
        reader.connect(writer2);
        var ticks = Utils.withTickers(reader, writer1, writer2);

        reader.write(25);
        Utils.assertValue(25, writer1.read());
        Utils.assertValue(25, writer2.read());

        ticks.tick(1);
        writer1.write(50);
        Utils.assertValue(50, reader.read());
        writer2.write(75);
        Utils.assertValue(75, reader.read());
    }

    @Test
    void shouldNotCaptureUntilTick() {
        var src = new PortSimpleIO("p0");
        var capture = new CaptureSimpleIO("signal");
        src.connect(capture);

        src.write(100);
        Utils.assertValue(0, capture.read());
        src.write(50);
        Utils.assertValue(0, capture.read());
    }

    @Test
    void shouldCaptureWhenTick() {
        var src = new PortSimpleIO("p0");
        var capture = new CaptureSimpleIO("signal");
        src.connect(capture);
        var ticks = Utils.withTickers(src, capture);

        src.write(100);
        Utils.assertValue(0, capture.read());

        ticks.tick(1);
        Utils.assertValue(100, capture.read());
        Utils.assertValue(100, capture.read());

        src.write(50);
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
        var input = new InputSimpleIO("signal", Arrays.asList(1, 2, 3));
        var port1 = new PortSimpleIO("p0");
        var port2 = new PortSimpleIO("p0");
        input.connect(port1);
        input.connect(port2);
        var ticks = Utils.withTickers(input, port1, port2);

        Utils.assertValue(1, port1.read());
        Utils.assertValue(1, port1.read());
        Utils.assertValue(1, port2.read());
        Utils.assertValue(1, port2.read());

        ticks.tick(1);
        Utils.assertValue(2, port1.read());
        Utils.assertValue(2, port1.read());
        Utils.assertValue(2, port2.read());
        Utils.assertValue(2, port2.read());

        ticks.tick(2);
        Utils.assertValue(3, port1.read());
        Utils.assertValue(3, port2.read());

        ticks.tick(3);
        Utils.assertValue(1, port1.read());
        Utils.assertValue(1, port2.read());

    }


}