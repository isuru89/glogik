package io.github.isuru89.games.shenzenio.instructions;

import io.github.isuru89.games.shenzenio.BlockType;

public class WaitForValue extends RunnableInstruction {
    public WaitForValue(String cmd, String[] ops, TestCondition activation) {
        super(cmd, ops, activation);
    }

    @Override
    public ExecutionResult execute(ExecutionContext context) {
        var ref = context.readValue(ops[1]);
        if (ref.isBlocked()) {
            return new ExecutionResult(BlockType.IO, context.getTestCondition());
        }

        return new ExecutionResult(BlockType.NONE, context.getTestCondition());
    }

    @Override
    public String toString() {
        return "slx " + ops[1];
    }
}
