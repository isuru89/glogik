package io.github.isuru89.games.shenzenio.instructions;

public class Invert extends RunnableInstruction {
    public Invert(String cmd, String[] ops, TestCondition activation) {
        super(cmd, ops, activation);
    }

    @Override
    public ExecutionResult execute(ExecutionContext context) {
        var regValue = context.readValue("acc");
        int newValue = regValue.getValue() == 0 ? 100 : 0;
        context.writeValue("acc", newValue);

        return ExecutionResult.proceedWithContext(context);
    }

    @Override
    public String toString() {
        return "not";
    }
}
