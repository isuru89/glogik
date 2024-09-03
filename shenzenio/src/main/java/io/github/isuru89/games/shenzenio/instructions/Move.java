package io.github.isuru89.games.shenzenio.instructions;

import io.github.isuru89.games.shenzenio.BlockType;
import io.github.isuru89.games.shenzenio.Value;

public class Move extends RunnableInstruction {
    public Move(String cmd, String[] ops, TestCondition activation) {
        super(cmd, ops, activation);
    }

    @Override
    public ExecutionResult execute(ExecutionContext context) {
        Value num = context.readValue(ops[1]);
        if (num.isBlocked()) {
            return new ExecutionResult(BlockType.IO, context.getTestCondition());
        }

        Value result = context.writeValue(ops[2], num.getValue());
        if (result.isBlocked()) {
            return new ExecutionResult(BlockType.IO, context.getTestCondition());
        }

        return ExecutionResult.proceedWithContext(context);
    }

    @Override
    public String toString() {
        return String.format("mov %s %s", ops[1], ops[2]);
    }
}
