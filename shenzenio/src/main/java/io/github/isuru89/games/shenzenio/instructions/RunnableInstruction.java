package io.github.isuru89.games.shenzenio.instructions;

public class RunnableInstruction extends Instruction {
    public RunnableInstruction(String cmd, String[] ops, TestCondition activation) {
        super(cmd, ops, activation);
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}
