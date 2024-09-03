package io.github.isuru89.games.shenzenio;

import io.github.isuru89.games.shenzenio.ports.CaptureXBus;
import io.github.isuru89.games.shenzenio.ports.InputXBus;
import io.github.isuru89.games.shenzenio.ports.PortSimpleIO;
import io.github.isuru89.games.shenzenio.ports.PortXBus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

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
        Utils.assertBlockedValue(port.read());
    }


    @Test
    void shouldBlockWhenWriteTwice() {
        var src = new PortXBus("x1");
        var dest = new PortXBus("x1");
        src.connect(dest);

        Utils.assertValue(63, src.write(63));

        var peekVal = src.peek();
        Assertions.assertTrue(peekVal.isPresent());
        Assertions.assertEquals(63, peekVal.get());

        Utils.assertBlockedValue(src.write(40));

        Utils.assertValue(63, dest.read());
        Utils.assertBlockedValue(dest.read());


        Utils.assertValue(40, src.write(40));

        peekVal = src.peek();
        Assertions.assertTrue(peekVal.isPresent());
        Assertions.assertEquals(40, peekVal.get());
    }

    @Test
    void shouldBlockWhenSrcHasNotEmittedValue() {
        var src = new PortXBus("x1");
        var dest = new PortXBus("x1");
        src.connect(dest);

        Utils.assertBlockedValue(src.read());
        Utils.assertBlockedValue(dest.read());

        // repeatable reads should still block
        Utils.assertBlockedValue(dest.read());
    }


    @Test
    void shouldVisibleToSrcWithValueAfterEmitting() {
        var src = new PortXBus("x0");
        var dest = new PortXBus("x0");
        src.connect(dest);

        Utils.assertBlockedValue(src.read());
        Utils.assertBlockedValue(dest.read());

        src.write(63);

        var peekVal = src.peek();
        Assertions.assertTrue(peekVal.isPresent());
        Assertions.assertEquals(63, peekVal.get());
        Utils.assertValue(63, src.read());
    }


    @Test
    void shouldUnblockWhenSrcEmittedValue() {
        var src = new PortXBus("x0");
        var dest = new PortXBus("x0");
        src.connect(dest);

        Utils.assertBlockedValue(src.read());
        Utils.assertBlockedValue(dest.read());

        src.write(63);

        Utils.assertValue(63, dest.read());
        Utils.assertBlockedValue(src.read());

        // cannot read again
        Utils.assertBlockedValue(dest.read());
    }

    @Test
    void shouldChainAndOnlyOneWillGetValue() {
        var src1 = new PortXBus("x0");
        var src2 = new PortXBus("x0");
        var dest = new PortXBus("x0");
        src1.connect(src2);
        src2.connect(dest);

        Utils.assertBlockedValue(src1.read());
        Utils.assertBlockedValue(src2.read());
        Utils.assertBlockedValue(dest.read());

        src1.write(100);

        Utils.assertValue(100, src2.read());
        Utils.assertBlockedValue(dest.read());

        src2.write(50);
        Utils.assertValue(50, src1.read());
        Utils.assertBlockedValue(dest.read());

        dest.write(25);
        Utils.assertValue(25, src1.read());
        Utils.assertBlockedValue(src2.read());
    }


    @Test
    void shouldFanIn() {
        var src1 = new PortXBus("x1");
        var src2 = new PortXBus("x1");
        var dest = new PortXBus("x1");
        src1.connect(dest);
        src2.connect(dest);

        Utils.assertBlockedValue(src1.read());
        Utils.assertBlockedValue(src2.read());
        Utils.assertBlockedValue(dest.read());

        src1.write(100);
        Utils.assertValue(100, src2.read());
        Utils.assertBlockedValue(dest.read());

        src1.write(100);
        Utils.assertValue(100, dest.read());
        Utils.assertBlockedValue(src2.read());

        src2.write(50);
        Utils.assertValue(50, src1.read());
        Utils.assertBlockedValue(dest.read());

        dest.write(25);
        Utils.assertValue(25, src1.read());
        Utils.assertBlockedValue(src2.read());
    }


    @Test
    void shouldNotFanOut() {
        var src = new PortXBus("x0");
        var dest1 = new PortXBus("x0");
        var dest2 = new PortXBus("x0");
        src.connect(dest1);
        src.connect(dest2);

        Utils.assertValue(42, src.write(42));

        Utils.assertValue(42, dest1.read());
        Utils.assertBlockedValue(dest2.read());
    }

    @Test
    void shouldBiDirectional() {
        var src = new PortXBus("x1");
        var dest = new PortXBus("x1");
        src.connect(dest);

        Utils.assertValue(100, src.write(100));

        Utils.assertBlockedValue(dest.write(150));
        Utils.assertValue(100, dest.read());
        Utils.assertValue(150, dest.write(150));
        Utils.assertBlockedValue(src.write(200));
        Utils.assertValue(150, src.read());
    }

    @Test
    void shouldCaptureAsSoonAsWritten() {
        var src = new PortXBus("x0");
        var capture = new CaptureXBus("signal");
        src.connect(capture);

        src.write(100);
        Utils.assertValue(100, capture.read());
    }


    @Test
    void shouldCaptureAllMultipleValuesSequential() {
        var src = new PortXBus("x1");
        var capture = new CaptureXBus("signal");
        src.connect(capture);

        Utils.assertValue(100, src.write(100));
        Utils.assertValue(150, src.write(150));
        Utils.assertValue(50, src.write(50));
        // latest value must be 50
        Utils.assertValue(50, capture.read());

        var list = capture.getAllValues();
        Assertions.assertEquals(3, list.size());
        Assertions.assertEquals(list, Arrays.asList(100, 150, 50));
    }

    @Test
    void shouldNotAvailableToOtherPortsWhenAtLeastOneCaptureConnected() {
        var src = new PortXBus("x0");
        var dest = new PortXBus("x0");
        var capture = new CaptureXBus("signal");
        src.connect(dest);
        src.connect(capture);

        Utils.assertValue(25, src.write(25));
        Utils.assertValue(50, src.write(50));
        Utils.assertValue(75, src.write(75));

        Utils.assertBlockedValue(dest.read());

        // all the values must have read by sink capture
        var list = capture.getAllValues();
        Assertions.assertEquals(3, list.size());
        Assertions.assertEquals(list, Arrays.asList(25, 50, 75));
    }

    @Test
    void shouldReadSameValueRepeatedlyFromInput() {
        var input = InputXBus.withConstantValue("sensor", 3);
        var port = new PortXBus("x1");
        input.connect(port);

        Utils.assertValue(3, port.read());
        Utils.assertValue(3, port.read());
        Utils.assertValue(3, port.read());
    }


}