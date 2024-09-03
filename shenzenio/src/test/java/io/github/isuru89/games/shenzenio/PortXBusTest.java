package io.github.isuru89.games.shenzenio;

import io.github.isuru89.games.shenzenio.ports.CaptureXBus;
import io.github.isuru89.games.shenzenio.ports.IOMode;
import io.github.isuru89.games.shenzenio.ports.InputXBus;
import io.github.isuru89.games.shenzenio.ports.PortSimpleIO;
import io.github.isuru89.games.shenzenio.ports.PortXBus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static io.github.isuru89.games.shenzenio.Utils.assertBlockedValue;
import static io.github.isuru89.games.shenzenio.Utils.assertMode;
import static io.github.isuru89.games.shenzenio.Utils.assertValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PortXBusTest {

    @Test
    void shouldOnlyConnectibleXBus() {
        var port = new PortXBus("x0");
        Assertions.assertThrows(RuntimeException.class, () -> port.connect(new PortSimpleIO("p0")));
        Assertions.assertDoesNotThrow(() -> port.connect(new PortXBus("x1")));
    }

    @Test
    void shouldFailSameConnectionTwiceOrMore() {
        var src = new PortXBus("x1");
        var dest = new PortXBus("x1");
        src.connect(dest);

        Assertions.assertThrows(RuntimeException.class, () -> src.connect(dest));
    }

    @Test
    void shouldReturnZeroNonConnected() {
        var port = new PortXBus("x0");
        assertBlockedValue(port.read());
    }

    @Test
    void writingShouldChangeLinkedModes() {
        var src = new PortXBus("x1");
        var dest = new PortXBus("x1");
        src.connect(dest);

        assertValue(63, src.write(63));
        assertMode(IOMode.OUTPUT, src);
        assertMode(IOMode.INPUT, dest);
    }

    @Test
    void bothPortsCannotWriteInSameTick() {
        var src = new PortXBus("x0");
        var dest = new PortXBus("x1");
        src.connect(dest);

        assertValue(63, src.write(63));
        assertMode(IOMode.OUTPUT, src);
        assertMode(IOMode.INPUT, dest);

        Assertions.assertThrows(RuntimeException.class, () -> dest.write(42));
        assertMode(IOMode.OUTPUT, src);
        assertMode(IOMode.INPUT, dest);
    }

    @Test
    void shouldBlockWhenReadTwice() {
        var src = new PortXBus("x1");
        var dest = new PortXBus("x1");
        src.connect(dest);

        assertValue(25, src.write(25));
        assertValue(25, dest.read());
        assertBlockedValue(dest.read());
    }


    @Test
    void shouldBlockWhenWriteTwice() {
        var src = new PortXBus("x1");
        var dest = new PortXBus("x1");
        src.connect(dest);

        assertValue(63, src.write(63));

        var peekVal = src.peek();
        assertTrue(peekVal.isPresent());
        assertEquals(63, peekVal.get());

        assertBlockedValue(src.write(40));

        assertValue(63, dest.read());
        assertBlockedValue(dest.read());

        assertValue(40, src.write(40));

        peekVal = src.peek();
        assertTrue(peekVal.isPresent());
        assertEquals(40, peekVal.get());
    }

    @Test
    void shouldBlockWhenSrcHasNotEmittedValue() {
        var src = new PortXBus("x1");
        var dest = new PortXBus("x1");
        src.connect(dest);

        assertBlockedValue(src.read());
        assertBlockedValue(dest.read());

        // repeatable reads should still block
        assertBlockedValue(dest.read());
    }


    @Test
    void shouldVisibleToSrcWithValueAfterEmitting() {
        var src = new PortXBus("x0");
        var dest = new PortXBus("x0");
        src.connect(dest);

        assertBlockedValue(src.read());
        assertBlockedValue(dest.read());

        src.write(63);

        var peekVal = src.peek();
        assertTrue(peekVal.isPresent());
        assertEquals(63, peekVal.get());
        assertValue(63, src.read());
    }


    @Test
    void shouldUnblockWhenSrcEmittedValue() {
        var src = new PortXBus("x0");
        var dest = new PortXBus("x0");
        src.connect(dest);

        assertBlockedValue(src.read());
        assertBlockedValue(dest.read());

        src.write(63);

        assertValue(63, dest.read());
        assertBlockedValue(src.read());

        // cannot read again
        assertBlockedValue(dest.read());
    }

    @Test
    void shouldBeAbleToChainOnNextTickOnwards() {
        var src1 = new PortXBus("x0");
        var src2 = new PortXBus("x0");
        var dest = new PortXBus("x0");
        src1.connect(src2);
        src2.connect(dest);
        var ticks = Utils.withTickers(src1, src2, dest);

        src1.write(100);
        assertValue(100, src2.read());
        assertBlockedValue(dest.read());

        ticks.tick(1);
        src2.write(50);
        assertValue(50, src1.read());
        assertBlockedValue(dest.read());

        ticks.tick(2);
        dest.write(25);
        assertValue(25, src1.read());
        assertBlockedValue(src2.read());
    }

    @Test
    void shouldFanIn() {
        var src1 = new PortXBus("x1");
        var src2 = new PortXBus("x1");
        var dest = new PortXBus("x1");
        src1.connect(dest);
        src2.connect(dest);

        assertBlockedValue(src1.read());
        assertBlockedValue(src2.read());
        assertBlockedValue(dest.read());

        src1.write(100);
        assertValue(100, src2.read());
        assertBlockedValue(dest.read());

        src1.write(75);
        assertValue(75, dest.read());
        assertBlockedValue(src2.read());

        src2.write(50);
        assertValue(50, dest.read());
        assertBlockedValue(dest.read());

        assertThrows(RuntimeException.class, () -> dest.write(25));
    }


    @Test
    void shouldNotFanOut() {
        var src = new PortXBus("x0");
        var dest1 = new PortXBus("x0");
        var dest2 = new PortXBus("x0");
        src.connect(dest1);
        src.connect(dest2);

        assertValue(42, src.write(42));

        assertValue(42, dest1.read());
        assertBlockedValue(dest2.read());
    }

    @Test
    void shouldNotBiDirectionalInSameTick() {
        var src = new PortXBus("x0");
        var dest = new PortXBus("x1");
        src.connect(dest);

        assertValue(100, src.write(100));
        assertThrows(RuntimeException.class, () -> dest.write(50));


    }

    @Test
    void shouldCaptureAsSoonAsWritten() {
        var src = new PortXBus("x0");
        var capture = new CaptureXBus("signal");
        src.connect(capture);

        src.write(100);
        assertValue(100, capture.read());
    }


    @Test
    void shouldCaptureAllMultipleValuesSequential() {
        var src = new PortXBus("x1");
        var capture = new CaptureXBus("signal");
        src.connect(capture);

        assertValue(100, src.write(100));
        assertValue(150, src.write(150));
        assertValue(50, src.write(50));
        // latest value must be 50
        assertValue(50, capture.read());

        var list = capture.getAllValues();
        assertEquals(3, list.size());
        assertEquals(list, Arrays.asList(100, 150, 50));
    }

    @Test
    void shouldNotAvailableToOtherPortsWhenAtLeastOneCaptureConnected() {
        var src = new PortXBus("x0");
        var dest = new PortXBus("x0");
        var capture = new CaptureXBus("signal");
        src.connect(dest);
        src.connect(capture);

        assertValue(25, src.write(25));
        assertValue(50, src.write(50));
        assertValue(75, src.write(75));

        assertBlockedValue(dest.read());

        // all the values must have read by sink capture
        var list = capture.getAllValues();
        assertEquals(3, list.size());
        assertEquals(list, Arrays.asList(25, 50, 75));
    }

    @Test
    void shouldReadSameValueRepeatedlyFromInput() {
        var input = InputXBus.withConstantValue("sensor", 3);
        var port = new PortXBus("x1");
        input.connect(port);

        assertValue(3, port.read());
        assertValue(3, port.read());
        assertValue(3, port.read());
    }


}