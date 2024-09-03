package io.github.isuru89.games.shenzenio.instructions;

import io.github.isuru89.games.shenzenio.BlockType;

public class Sleep extends RunnableInstruction {
    public Sleep(String cmd, String[] ops, TestCondition activation) {
        super(cmd, ops, activation);
    }

    @Override
    public ExecutionResult execute(ExecutionContext context) {
        int currTick = context.getCurrentTick();
        var forWait = Integer.parseInt(ops[1]);

        context.awaitForNextTick(currTick + forWait);
        return new ExecutionResult(BlockType.SLEEP, context.getTestCondition());
    }

    @Override
    public String toString() {
        return "slp " + ops[1];
    }
}
