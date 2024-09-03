package io.github.isuru89.games.shenzenio.instructions;

import io.github.isuru89.games.shenzenio.BlockType;

public class MutedInstruction extends Instruction {
    public MutedInstruction(String cmd, String[] ops) {
        super(cmd, ops, TestCondition.ANY);
    }

    @Override
    public boolean isExecutable() {
        return false;
    }

    @Override
    public final ExecutionResult execute(ExecutionContext context) {
        // do nothing
        return new ExecutionResult(BlockType.NONE, context.getTestCondition());
    }
}
