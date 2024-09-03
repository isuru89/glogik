package io.github.isuru89.games.shenzenio;

import io.github.isuru89.games.shenzenio.ports.InputXBus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class InputXBusTest {

    @Test
    void shouldReturnSameValue() {
        var input = InputXBus.withConstantValue("x1", 5);
        Utils.assertValue(5, input.read());
        Utils.assertValue(5, input.read());
        Utils.assertValue(5, input.read());
        Utils.assertValue(5, input.read());
    }

    @Test
    void shouldRepeatableOperateCorrectly() {
        var input = InputXBus.withRepeatable("x0", List.of(1, 2, 3));
        Utils.assertValue(1, input.read());
        Utils.assertValue(2, input.read());
        Utils.assertValue(3, input.read());
        Utils.assertValue(1, input.read());
        Utils.assertValue(2, input.read());
        Utils.assertValue(3, input.read());
    }

    @Test
    void shouldFailWhenRepeatableHasNoValues() {
        var input = InputXBus.withRepeatable("x0", List.of());
        Assertions.assertThrows(RuntimeException.class, () -> Utils.assertValue(-1, input.read()));
    }

    @Test
    void shouldNonRepeatableOperateCorrectly() {
        var input = InputXBus.withSequential("x0", List.of(1, 2, 3));
        Utils.assertValue(1, input.read());
        Utils.assertValue(2, input.read());
        Utils.assertValue(3, input.read());
        Assertions.assertThrows(RuntimeException.class, () -> Utils.assertValue(-1, input.read()));
    }
}