package io.github.isuru89.games.shenzenio.instructions.checks;

import io.github.isuru89.games.shenzenio.BlockType;
import io.github.isuru89.games.shenzenio.Value;
import io.github.isuru89.games.shenzenio.instructions.ExecutionContext;
import io.github.isuru89.games.shenzenio.instructions.ExecutionResult;
import io.github.isuru89.games.shenzenio.instructions.RunnableInstruction;
import io.github.isuru89.games.shenzenio.instructions.TestCondition;

public abstract class CheckInstruction extends RunnableInstruction {
    public CheckInstruction(String cmd, String[] ops, TestCondition activation) {
        super(cmd, ops, activation);
    }

    @Override
    public ExecutionResult execute(ExecutionContext context) {
        Value val1 = context.readValue(ops[1]);
        if (val1.isBlocked()) {
            return new ExecutionResult(BlockType.IO, context.getTestCondition());
        }
        Value val2 = context.readValue(ops[2]);
        if (val2.isBlocked()) {
            return new ExecutionResult(BlockType.IO, context.getTestCondition());
        }

        boolean condBool = applyCondition(val1.getValue(), val2.getValue());
        TestCondition cond = condBool ? TestCondition.TRUE : TestCondition.FALSE;

        return new ExecutionResult(BlockType.NONE, cond);
    }

    protected abstract boolean applyCondition(int num1, int num2);

    @Override
    public String toString() {
        return String.format("%s %s %s", cmd, ops[1], ops[2]);
    }
}
