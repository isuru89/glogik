package io.github.isuru89.games.shenzenio.instructions.arithmetic;

import io.github.isuru89.games.shenzenio.instructions.TestCondition;

public class OpAdd extends ArithmeticInstruction {
    public OpAdd(String cmd, String[] ops, TestCondition activation) {
        super(cmd, ops, activation);
    }

    @Override
    protected int applyOperation(int num1, int num2) {
        return num1 + num2;
    }
}