package io.github.isuru89.games.shenzenio;

import io.github.isuru89.games.shenzenio.ports.IOMode;
import io.github.isuru89.games.shenzenio.ports.Port;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {

    public static void assertValue(int val, Value value) {
        Assertions.assertEquals(val, value.getValue());
        Assertions.assertFalse(value.isBlocked());
    }

    public static void assertMode(IOMode mode, Port... ports) {
        for (Port port : ports) {
            Assertions.assertEquals(mode, port.getIOMode());
        }
    }

    public static TickHandler withTickers(TickHandler... tickers) {
        return new AllTickers(tickers);
    }

    public static void assertBlockedValue(Value value) {
        Assertions.assertTrue(value.isBlocked());
    }

    private static class AllTickers implements TickHandler {
        private final List<TickHandler> tickHandlers = new ArrayList<>();

        AllTickers(TickHandler... handlers) {
            tickHandlers.addAll(Arrays.asList(handlers));
        }

        @Override
        public void tick(int cycleNumber) {
            for (var tick : tickHandlers) {
                tick.tick(cycleNumber);
            }
        }
    }
}
