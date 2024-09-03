package io.github.isuru89.games.shenzenio.components;

import io.github.isuru89.games.shenzenio.BlockType;
import io.github.isuru89.games.shenzenio.GameClock;
import io.github.isuru89.games.shenzenio.Utils;
import io.github.isuru89.games.shenzenio.ports.PortSimpleIO;
import io.github.isuru89.games.shenzenio.ports.PortXBus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DX300Test {

    @Test
    void testNumberProcessing() {
        var dx = new DX300("test");
        Assertions.assertArrayEquals(new int[]{0, 0, 0}, dx.processedValues(0));
        Assertions.assertArrayEquals(new int[]{0, 0, 1}, dx.processedValues(1));
        Assertions.assertArrayEquals(new int[]{0, 1, 0}, dx.processedValues(10));
        Assertions.assertArrayEquals(new int[]{0, 1, 1}, dx.processedValues(11));
        Assertions.assertArrayEquals(new int[]{1, 0, 0}, dx.processedValues(100));
        Assertions.assertArrayEquals(new int[]{1, 0, 1}, dx.processedValues(101));
        Assertions.assertArrayEquals(new int[]{1, 1, 0}, dx.processedValues(110));
        Assertions.assertArrayEquals(new int[]{1, 1, 1}, dx.processedValues(111));

        Assertions.assertArrayEquals(new int[]{0, 0, 5}, dx.processedValues(5));
        Assertions.assertArrayEquals(new int[]{8, 7, 6}, dx.processedValues(876));
        Assertions.assertArrayEquals(new int[]{-1, -2, -3}, dx.processedValues(-123));
    }

    @Test
    void testWriteModeWithX0() {
        var dx = new DX300("test");
        var p0 = new PortSimpleIO("p0");
        var p1 = new PortSimpleIO("p1");
        var p2 = new PortSimpleIO("p2");

        var src = new PortXBus("x0");
        src.connect(dx.getPort("x0"));
        dx.getPort("p0").connect(p0);
        dx.getPort("p1").connect(p1);
        dx.getPort("p2").connect(p2);

        src.write(110);
        Assertions.assertEquals(BlockType.NONE, dx.execute(new GameClock()));
        Assertions.assertEquals(0, p0.peek().orElse(-999));
        Assertions.assertEquals(100, p1.peek().orElse(-999));
        Assertions.assertEquals(100, p2.peek().orElse(-999));

        src.write(510);
        Assertions.assertEquals(BlockType.NONE, dx.execute(new GameClock()));
        Assertions.assertEquals(0, p0.peek().orElse(-999));
        Assertions.assertEquals(100, p1.peek().orElse(-999));
        Assertions.assertEquals(100, p2.peek().orElse(-999));
    }

    @Test
    void testWriteModeWithX1() {
        var dx = new DX300("test");
        var p0 = new PortSimpleIO("p0");
        var p1 = new PortSimpleIO("p1");
        var p2 = new PortSimpleIO("p2");

        var src = new PortXBus("x1");
        src.connect(dx.getPort("x1"));
        dx.getPort("p0").connect(p0);
        dx.getPort("p1").connect(p1);
        dx.getPort("p2").connect(p2);

        src.write(101);
        Assertions.assertEquals(BlockType.NONE, dx.execute(new GameClock()));

        Assertions.assertEquals(100, p0.peek().orElse(-999));
        Assertions.assertEquals(0, p1.peek().orElse(-999));
        Assertions.assertEquals(100, p2.peek().orElse(-999));
    }

    @Test
    void testWriteModeWithX2() {
        var dx = new DX300("test");
        var p0 = new PortSimpleIO("p0");
        var p1 = new PortSimpleIO("p1");
        var p2 = new PortSimpleIO("p2");

        var src = new PortXBus("x2");
        src.connect(dx.getPort("x2"));
        dx.getPort("p0").connect(p0);
        dx.getPort("p1").connect(p1);
        dx.getPort("p2").connect(p2);

        src.write(1);
        Assertions.assertEquals(BlockType.NONE, dx.execute(new GameClock()));

        Assertions.assertEquals(100, p0.peek().orElse(-999));
        Assertions.assertEquals(0, p1.peek().orElse(-999));
        Assertions.assertEquals(0, p2.peek().orElse(-999));
    }

    @Test
    void testReadModeWithX0() {
        var dx = new DX300("test");
        var p0 = new PortSimpleIO("p0");
        var p1 = new PortSimpleIO("p1");
        var p2 = new PortSimpleIO("p2");

        var src = new PortXBus("x0");
        src.connect(dx.getPort("x0"));
        dx.getPort("p0").connect(p0);
        dx.getPort("p1").connect(p1);
        dx.getPort("p2").connect(p2);

        Assertions.assertEquals(BlockType.NONE, dx.execute(new GameClock()));
        // default value is zero
        Utils.assertValue(0, src.read());

        p0.write(100);
        p1.write(50);
        p2.write(0);
        Assertions.assertEquals(BlockType.NONE, dx.execute(new GameClock()));
        Utils.assertValue(11, src.read());

        p0.write(0);
        p1.write(50);
        p2.write(75);
        Assertions.assertEquals(BlockType.NONE, dx.execute(new GameClock()));
        Utils.assertValue(110, src.read());


        p0.write(0);
        p1.write(100);
        p2.write(0);
        Assertions.assertEquals(BlockType.NONE, dx.execute(new GameClock()));
        Utils.assertValue(10, src.read());
    }

    @Test
    void testReadModeWithX1() {
        var dx = new DX300("test");
        var p0 = new PortSimpleIO("p0");
        var p1 = new PortSimpleIO("p1");
        var p2 = new PortSimpleIO("p2");

        var src = new PortXBus("x1");
        src.connect(dx.getPort("x1"));
        dx.getPort("p0").connect(p0);
        dx.getPort("p1").connect(p1);
        dx.getPort("p2").connect(p2);

        Assertions.assertEquals(BlockType.NONE, dx.execute(new GameClock()));
        // default value is zero
        Utils.assertValue(0, src.read());

        p0.write(100);
        p1.write(50);
        p2.write(0);
        Assertions.assertEquals(BlockType.NONE, dx.execute(new GameClock()));
        Utils.assertValue(11, src.read());

        p0.write(0);
        p1.write(50);
        p2.write(75);
        Assertions.assertEquals(BlockType.NONE, dx.execute(new GameClock()));
        Utils.assertValue(110, src.read());


        p0.write(0);
        p1.write(100);
        p2.write(0);
        Assertions.assertEquals(BlockType.NONE, dx.execute(new GameClock()));
        Utils.assertValue(10, src.read());
    }

    @Test
    void testReadModeWithX2() {
        var dx = new DX300("test");
        var p0 = new PortSimpleIO("p0");
        var p1 = new PortSimpleIO("p1");
        var p2 = new PortSimpleIO("p2");

        var src = new PortXBus("x2");
        src.connect(dx.getPort("x2"));
        dx.getPort("p0").connect(p0);
        dx.getPort("p1").connect(p1);
        dx.getPort("p2").connect(p2);

        Assertions.assertEquals(BlockType.NONE, dx.execute(new GameClock()));
        // default value is zero
        Utils.assertValue(0, src.read());

        p0.write(100);
        p1.write(50);
        p2.write(0);
        Assertions.assertEquals(BlockType.NONE, dx.execute(new GameClock()));
        Utils.assertValue(11, src.read());

        p0.write(0);
        p1.write(50);
        p2.write(75);
        Assertions.assertEquals(BlockType.NONE, dx.execute(new GameClock()));
        Utils.assertValue(110, src.read());


        p0.write(0);
        p1.write(100);
        p2.write(0);
        Assertions.assertEquals(BlockType.NONE, dx.execute(new GameClock()));
        Utils.assertValue(10, src.read());
    }

    @Test
    void testReadModeWhenNoneOfXBusConnected() {
        var dx = new DX300("test");
        var p0 = new PortSimpleIO("p0");
        var p1 = new PortSimpleIO("p1");
        var p2 = new PortSimpleIO("p2");

        dx.getPort("p0").connect(p0);
        dx.getPort("p1").connect(p1);
        dx.getPort("p2").connect(p2);

        Assertions.assertEquals(BlockType.IO, dx.execute(new GameClock()));

        p0.write(100);
        p1.write(50);
        p2.write(0);
        Assertions.assertEquals(BlockType.IO, dx.execute(new GameClock()));

        p0.write(0);
        p1.write(50);
        p2.write(75);
        Assertions.assertEquals(BlockType.IO, dx.execute(new GameClock()));

        p0.write(0);
        p1.write(100);
        p2.write(0);
        Assertions.assertEquals(BlockType.IO, dx.execute(new GameClock()));
    }


    @Test
    void shouldWriteToX0WhenAllOfXBusConnected() {
        var dx = new DX300("test");
        var p0 = new PortSimpleIO("p0");
        var p1 = new PortSimpleIO("p1");
        var p2 = new PortSimpleIO("p2");
        var x0 = new PortXBus("x0");
        var x1 = new PortXBus("x1");
        var x2 = new PortXBus("x2");

        dx.getPort("x0").connect(x0);
        dx.getPort("x1").connect(x1);
        dx.getPort("x2").connect(x2);
        dx.getPort("p0").connect(p0);
        dx.getPort("p1").connect(p1);
        dx.getPort("p2").connect(p2);

        p0.write(100);
        p1.write(50);
        p2.write(0);
        Assertions.assertEquals(BlockType.NONE, dx.execute(new GameClock()));
        Utils.assertValue(11, x0.read());
        Utils.assertBlockedValue(x1.read());
        Utils.assertBlockedValue(x2.read());

        p0.write(0);
        p1.write(50);
        p2.write(75);
        Assertions.assertEquals(BlockType.NONE, dx.execute(new GameClock()));
        Utils.assertValue(110, x0.read());
        Utils.assertBlockedValue(x1.read());
        Utils.assertBlockedValue(x2.read());

        p0.write(0);
        p1.write(100);
        p2.write(0);
        Assertions.assertEquals(BlockType.NONE, dx.execute(new GameClock()));
        Utils.assertValue(10, x0.read());
        Utils.assertBlockedValue(x1.read());
        Utils.assertBlockedValue(x2.read());
    }

}