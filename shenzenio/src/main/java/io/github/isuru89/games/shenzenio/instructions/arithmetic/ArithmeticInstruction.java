package io.github.isuru89.games.shenzenio.instructions.arithmetic;

import io.github.isuru89.games.shenzenio.BlockType;
import io.github.isuru89.games.shenzenio.instructions.ExecutionContext;
import io.github.isuru89.games.shenzenio.instructions.ExecutionResult;
import io.github.isuru89.games.shenzenio.instructions.RunnableInstruction;
import io.github.isuru89.games.shenzenio.instructions.TestCondition;

public abstract class ArithmeticInstruction extends RunnableInstruction {
    public ArithmeticInstruction(String cmd, String[] ops, TestCondition activation) {
        super(cmd, ops, activation);
    }

    private ExecutionResult handleArithmeticOperation(ExecutionContext context, char operator) {
        var acc = context.readValue("acc").getValue();
        var src = context.readValue(ops[1]);
        if (src.isBlocked()) {
            return new ExecutionResult(
                    BlockType.IO,
                    context.getTestCondition()
            );
        }

        int result = switch (operator) {
            case '+' -> acc + src.getValue();
            case '-' -> acc - src.getValue();
            case '*' -> acc * src.getValue();
            case '/' -> acc / src.getValue();
            default -> acc;
        };

        context.writeValue("acc", result);
        return new ExecutionResult(BlockType.NONE, context.getTestCondition());
    }

    protected abstract int applyOperation(int num1, int num2);

    @Override
    public String toString() {
        return String.format("%s %s", cmd, ops[1]);
    }
}
