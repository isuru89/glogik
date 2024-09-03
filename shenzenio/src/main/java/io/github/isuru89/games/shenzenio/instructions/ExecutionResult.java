package io.github.isuru89.games.shenzenio.instructions;

import io.github.isuru89.games.shenzenio.BlockType;

public record ExecutionResult(
        BlockType blockType,
        TestCondition testCondition
) {

    public static ExecutionResult proceedWithContext(ExecutionContext context) {
        return new ExecutionResult(BlockType.NONE, context.getTestCondition());
    }
}
