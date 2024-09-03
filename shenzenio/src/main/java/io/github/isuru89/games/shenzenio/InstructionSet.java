package io.github.isuru89.games.shenzenio;

import io.github.isuru89.games.shenzenio.instructions.Factory;
import io.github.isuru89.games.shenzenio.instructions.Instruction;
import io.github.isuru89.games.shenzenio.instructions.TestCondition;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InstructionSet implements ResetHandler {

    private final Map<String, CodeLine> labelPos;
    private final String[] instructions;
    private final CodeLine program;
    private CodeLine curr;
    private TestCondition testCondition;

    private Instruction justExecuted;

    private InstructionSet(String[] instructions) {
        this(instructions, null, new HashMap<>());
    }

    private InstructionSet(String[] instructions, CodeLine first, Map<String, CodeLine> labelPos) {
        this.instructions = instructions;
        this.program = first;
        this.curr = first;
        this.labelPos = labelPos;
        this.testCondition = TestCondition.ANY;
        this.justExecuted = null;
    }

    public static InstructionSet of(String... lines) {
        CodeLine first = null;
        CodeLine latest = null;
        Map<String, CodeLine> labelPositions = new HashMap<>();
        int runnableCount = 0;

        for (var line : lines) {
            var instruction = Factory.create(line);
            CodeLine tmpCode;
            if (first == null) {
                first = new CodeLine(instruction);
                tmpCode = first;
                latest = first;
            } else {
                var newCode = new CodeLine(instruction, first);
                tmpCode = newCode;
                latest.withNext(newCode);
                latest = newCode;
            }

            if (instruction.getNavigableId().isPresent()) {
                labelPositions.put(instruction.getNavigableId().get(), tmpCode);
            }

            if (instruction.isExecutable()) {
                runnableCount++;
            }
        }

        if (runnableCount == 0) {
            return new InstructionSet(lines);
        }

        return new InstructionSet(lines, first, labelPositions);
    }

    public TestCondition getTestCondition() {
        return testCondition;
    }

    public void setTestCondition(TestCondition testCondition) {
        this.testCondition = testCondition;
    }

    public void jump(String toLabel) {
        var toGo = getLabelPosition(toLabel);
        if (toGo == null) {
            throw new RuntimeException("unknown label!");
        }
        curr = findNextRunnableInstruction(toGo);
    }

    public Optional<Instruction> getJustExecuted() {
        return Optional.ofNullable(justExecuted);
    }

    public Instruction readInstruction() {
        var result = getInstruction();
        justExecuted = result;
        return result;
    }

    private Instruction getInstruction() {
        if (instructions.length == 0 || curr == null) {
            return null;
        }

        do {
            curr = findNextRunnableInstruction(curr);
            var instruction = curr.data;
            curr = curr.next;

            if (instruction.getActivatedCondition() == TestCondition.ANY) {
                return instruction;
            }

            if (instruction.getActivatedCondition() == testCondition) {
                return instruction;
            }

        } while (true);
    }

    private CodeLine findNextRunnableInstruction(CodeLine current) {
        CodeLine tmp = current;
        while (!tmp.data.isExecutable()) {
            tmp = tmp.next;
        }
        return tmp;
    }

    private CodeLine getLabelPosition(String label) {
        return labelPos.get(label);
    }

    public boolean isLabelExists(String label) {
        return getLabelPosition(label) != null;
    }

    @Override
    public void reset() {
        this.testCondition = TestCondition.ANY;
        this.curr = program;
    }

    private static class CodeLine {
        private final Instruction data;
        private CodeLine next;

        private CodeLine(Instruction data, CodeLine next) {
            this.next = next;
            this.data = data;
        }

        private CodeLine(Instruction data) {
            this.next = this;
            this.data = data;
        }

        public void withNext(CodeLine next) {
            this.next = next;
        }

        @Override
        public String toString() {
            return data.toString();
        }
    }
}
