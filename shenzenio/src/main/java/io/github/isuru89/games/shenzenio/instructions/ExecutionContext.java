package io.github.isuru89.games.shenzenio.instructions;

import io.github.isuru89.games.shenzenio.Value;

public interface ExecutionContext {

    int getCurrentTick();

    TestCondition getTestCondition();

    Value readValue(String registerPortOrValue);

    Value writeValue(String registerOrPort, int newValue);

    void awaitForValueInAddress(String port);

    void awaitForNextTick(int nextTickToAwake);

    void jumpToLabel(String label);
}
