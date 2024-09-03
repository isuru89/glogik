package io.github.isuru89.games.shenzenio;

import io.github.isuru89.games.shenzenio.ports.IOMode;
import io.github.isuru89.games.shenzenio.ports.StatefulMode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StatefulModeTest {

    @Test
    void shouldHaveInitialValuesSetCorrectly() {
        var mode = new StatefulMode(IOMode.UNDEFINED);
        Assertions.assertEquals(IOMode.UNDEFINED, mode.get());
        Assertions.assertTrue(mode.isAvailable());
    }

    @Test
    void shouldUnavailableAfterWrite() {
        var mode = new StatefulMode(IOMode.UNDEFINED);
        Assertions.assertTrue(mode.isAvailable());
        mode.set(IOMode.OUTPUT);
        Assertions.assertFalse(mode.isAvailable());
    }

    @Test
    void shouldNotChangeAvailabilityWhenRead() {
        var mode = new StatefulMode(IOMode.UNDEFINED);
        Assertions.assertTrue(mode.isAvailable());
        mode.set(IOMode.OUTPUT);
        Assertions.assertFalse(mode.isAvailable());
        Assertions.assertEquals(IOMode.OUTPUT, mode.get());
        Assertions.assertFalse(mode.isAvailable());
        Assertions.assertEquals(IOMode.OUTPUT, mode.peek());
        Assertions.assertFalse(mode.isAvailable());
    }

    @Test
    void shouldPreventWriteAgainWhenAvailable() {
        var mode = new StatefulMode(IOMode.UNDEFINED);
        Assertions.assertTrue(mode.isAvailable());
        mode.set(IOMode.OUTPUT);
        Assertions.assertFalse(mode.isAvailable());
        Assertions.assertThrows(RuntimeException.class, () -> mode.set(IOMode.INPUT));
        Assertions.assertDoesNotThrow(() -> mode.set(IOMode.OUTPUT));
    }


    @Test
    void shouldNotThrowWriteSameValueEvenIfUnavailable() {
        var mode = new StatefulMode(IOMode.UNDEFINED);
        Assertions.assertTrue(mode.isAvailable());
        mode.set(IOMode.OUTPUT);
        Assertions.assertFalse(mode.isAvailable());
        Assertions.assertDoesNotThrow(() -> mode.set(IOMode.OUTPUT));
    }


    @Test
    void shouldMakeUnavailableInNextTickEvenIfSameValue() {
        var mode = new StatefulMode(IOMode.UNDEFINED);
        Assertions.assertTrue(mode.isAvailable());
        mode.set(IOMode.OUTPUT);
        Assertions.assertFalse(mode.isAvailable());

        mode.makeAvailable();
        Assertions.assertDoesNotThrow(() -> mode.set(IOMode.OUTPUT));
        Assertions.assertFalse(mode.isAvailable());
    }
}