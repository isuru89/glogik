package io.github.isuru89.games.shenzenio.instructions;

public class Jump extends RunnableInstruction {
    public Jump(String cmd, String[] ops, TestCondition activation) {
        super(cmd, ops, activation);
    }

    @Override
    public ExecutionResult execute(ExecutionContext context) {
        context.jumpToLabel(ops[1]);

        return ExecutionResult.proceedWithContext(context);
    }

    @Override
    public String toString() {
        return "jmp " + ops[1];
    }
}
