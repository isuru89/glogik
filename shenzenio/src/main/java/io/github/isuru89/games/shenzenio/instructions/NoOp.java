package io.github.isuru89.games.shenzenio.instructions;

public class NoOp extends RunnableInstruction {
    public NoOp(String cmd, String[] ops, TestCondition activation) {
        super(cmd, ops, activation);
    }

    @Override
    public ExecutionResult execute(ExecutionContext context) {
        // do nothing.
        return ExecutionResult.proceedWithContext(context);
    }

    @Override
    public String toString() {
        return "nop";
    }
}
