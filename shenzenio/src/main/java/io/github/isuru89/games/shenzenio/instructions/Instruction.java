package io.github.isuru89.games.shenzenio.instructions;

import io.github.isuru89.games.shenzenio.BlockType;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class Instruction {

    protected final String[] ops;
    protected final String cmd;
    private final TestCondition activatedCondition;
    private Set<String> refs;

    public Instruction(String cmd, String[] ops, TestCondition activatedCondition) {
        this.cmd = cmd;
        this.ops = ops;
        this.activatedCondition = activatedCondition;

        this.parseOps();
    }

    private void parseOps() {
        var hSet = new HashSet<String>();
        if (ops != null) {
            for (var op : ops) {
                if (!StringUtils.isNumeric(op)) {
                    hSet.add(op);
                }
            }
        }
        refs = Collections.unmodifiableSet(hSet);
    }

    public TestCondition getActivatedCondition() {
        return activatedCondition;
    }

    public abstract boolean isExecutable();

    public Optional<String> getNavigableId() {
        return Optional.empty();
    }

    public ExecutionResult execute(ExecutionContext context) {
//        switch (cmd) {
//            case "noop":
//                return new ExecutionResult(BlockType.NONE, context.getTestCondition());
//            case "not":
//                var regValue = context.readValue("acc");
//                int newValue = regValue.getValue() == 0 ? 100 : 0;
//                context.writeValue("acc", newValue);
//                break;
//            case "add":
//                blockType = handleArithmeticOperation(ops, '+');
//                break;
//            case "sub":
//                blockType = handleArithmeticOperation(ops, '-');
//                break;
//            case "mul":
//                blockType = handleArithmeticOperation(ops, '*');
//                break;
//            case "div":
//                blockType = handleArithmeticOperation(ops, '/');
//                break;
//            case "mov":
//                blockType = handleMoveCommand(ops);
//                break;
//            case "teq":
//                comparisonResult = handleTestCompareCommand(ops, '=');
//                this.instructionSet.setTestCondition(comparisonResult.condition());
//                this.blockType = comparisonResult.blockType();
//                break;
//            case "tgt":
//                comparisonResult = handleTestCompareCommand(ops, '>');
//                this.instructionSet.setTestCondition(comparisonResult.condition());
//                this.blockType = comparisonResult.blockType();
//                break;
//            case "tlt":
//                comparisonResult = handleTestCompareCommand(ops, '<');
//                this.instructionSet.setTestCondition(comparisonResult.condition());
//                this.blockType = comparisonResult.blockType();
//                break;
//            case "jmp":
//                instructionSet.jump(ops[1]);
//                break;
//            case "slx":
//                blockWaitingPorts.push(ops[1]);
//                blockType = BlockType.IO;
//                break;
//            case "slp":
//                int count = Integer.parseInt(ops[1]);
//                blockedUntilCycle = clock.getCurrentTick() + count;
//                blockType = BlockType.SLEEP;
//                break;
//        }
        return new ExecutionResult(BlockType.NONE, TestCondition.ANY);
    }

    public Set<String> getReferences() {
        return refs;
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "ops=" + Arrays.toString(ops) +
                ", cmd='" + cmd + '\'' +
                '}';
    }
}
