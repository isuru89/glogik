package io.github.isuru89.games.shenzenio.instructions.checks;

import io.github.isuru89.games.shenzenio.instructions.TestCondition;

public class CheckLessThan extends CheckInstruction {
    public CheckLessThan(String cmd, String[] ops, TestCondition activation) {
        super(cmd, ops, activation);
    }

    @Override
    protected boolean applyCondition(int num1, int num2) {
        return num1 < num2;
    }
}
