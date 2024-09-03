package io.github.isuru89.games.shenzenio;

import org.junit.jupiter.api.Assertions;

public class Utils {

    static void assertValue(int val, Value value) {
        Assertions.assertEquals(val, value.getValue());
        Assertions.assertFalse(value.isBlocked());
    }

    static void assertBlockedValue(Value value) {
        Assertions.assertTrue(value.isBlocked());
    }
}
