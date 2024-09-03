package io.github.isuru89.games.shenzenio;

import io.github.isuru89.games.shenzenio.instructions.Instruction;
import io.github.isuru89.games.shenzenio.instructions.TestCondition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InstructionSetTest {

    @Test
    void getEmptyInstructionSet() {
        var iset = InstructionSet.of();
        Assertions.assertTrue(iset.getJustExecuted().isEmpty());
        Assertions.assertEquals(TestCondition.ANY, iset.getTestCondition());
        Assertions.assertNull(iset.readInstruction());
    }


    @Test
    void getCurrentLineOfOneLine() {
        var iset = InstructionSet.of("mov 100 p0");
        Assertions.assertTrue(iset.getJustExecuted().isEmpty());
        Assertions.assertEquals(TestCondition.ANY, iset.getTestCondition());

        var line = iset.readInstruction();
        Assertions.assertNotNull(line);
        Assertions.assertTrue(iset.getJustExecuted().isPresent());
        Assertions.assertEquals(line, iset.getJustExecuted().get());

        // should cycle same
        var nextLine = iset.readInstruction();
        Assertions.assertNotNull(nextLine);
        Assertions.assertEquals(line, nextLine);
        Assertions.assertTrue(iset.getJustExecuted().isPresent());
        Assertions.assertEquals(nextLine, iset.getJustExecuted().get());
    }

    @Test
    void shouldReturnNothingWhenNoRunnableInstructionsExists() {
        var iset = InstructionSet.of("# this is a comment");

        {
            var line = iset.readInstruction();
            Assertions.assertNull(line);
            Assertions.assertFalse(iset.getJustExecuted().isPresent());
        }

        // reading again and again should return nothing
        {
            var line = iset.readInstruction();
            Assertions.assertNull(line);
            Assertions.assertFalse(iset.getJustExecuted().isPresent());
        }
    }

    @Test
    void shouldReturnNothingWhenAllInstructionsAreNonRunnable() {
        var iset = InstructionSet.of("# this is a comment", " ", "#another comment", "label:", "");

        {
            var line = iset.readInstruction();
            Assertions.assertNull(line);
            Assertions.assertFalse(iset.getJustExecuted().isPresent());
        }

        // reading again and again should return nothing
        {
            var line = iset.readInstruction();
            Assertions.assertNull(line);
            Assertions.assertFalse(iset.getJustExecuted().isPresent());
        }
    }

    @Test
    void shouldCycle() {
        var iset = InstructionSet.of("mov 100 p0", "mov x0 acc", "slp 1");
        assertInstructionText("mov 100 p0", iset.readInstruction());
        assertInstructionText("mov x0 acc", iset.readInstruction());
        assertInstructionText("slp 1", iset.readInstruction());
        assertInstructionText("mov 100 p0", iset.readInstruction());
        assertInstructionText("mov x0 acc", iset.readInstruction());
        assertInstructionText("slp 1", iset.readInstruction());
    }


    @Test
    void shouldCycleSkippingEmptyLines() {
        var iset = InstructionSet.of("mov 100 p0", "", "mov x0 acc", "slp 1", "  ");
        assertExecutionOrder(iset,
                "mov 100 p0", "mov x0 acc", "slp 1",
                "mov 100 p0", "mov x0 acc", "slp 1",
                "mov 100 p0", "mov x0 acc");
    }

    @Test
    void shouldCycleSkippingCommentLines() {
        var iset = InstructionSet.of("mov 100 p0", "#comment1", "mov x0 acc", "slp 1", " #  comment 2");
        assertExecutionOrder(iset,
                "mov 100 p0", "mov x0 acc", "slp 1",
                "mov 100 p0", "mov x0 acc", "slp 1",
                "mov 100 p0", "mov x0 acc");
    }

    @Test
    void shouldCycleSkippingLabels() {
        var iset = InstructionSet.of("mov 100 p0", "label1:", "mov x0 acc", "slp 1", " label 2:");
        assertExecutionOrder(iset,
                "mov 100 p0", "mov x0 acc", "slp 1",
                "mov 100 p0", "mov x0 acc", "slp 1",
                "mov 100 p0", "mov x0 acc");
    }

    @Test
    void shouldCorrectlyIterateWithTestCondition() {
        var iset = InstructionSet.of(
                "mov x0 acc",
                "  teq p0 x2",
                "+ mov 100 dat",
                "  teq p0 x3",
                "+ mov 0 dat",
                "  add dat",
                "  tgt acc 119",
                "+ mov 100 p1",
                "- mov 0 p1",
                "  slp 1");

        assertExecutionOrder(iset, "mov x0 acc", "teq p0 x2");
        iset.setTestCondition(TestCondition.TRUE);
        assertExecutionOrder(iset, "mov 100 dat", "teq p0 x3", "mov 0 dat", "add dat", "tgt acc 119");
        iset.setTestCondition(TestCondition.FALSE);
        assertExecutionOrder(iset, "mov 0 p1", "slp 1", "mov x0 acc", "teq p0 x2", "teq p0 x3");
    }

    @Test
    void jumpNonExistingLabel() {
        var iset = InstructionSet.of("mov 100 p0", "mylabel:", "slp 1", "mov 100 p1");
        Assertions.assertThrows(RuntimeException.class, () -> iset.jump("unknown"));
    }

    @Test
    void jumpToExistingLabelShouldMoveToNextRunnableInstruction() {
        var iset = InstructionSet.of("mov 100 p0", "mylabel:", "", "slp 1", "mov 100 p1", "jmp mylabel");
        assertExecutionOrder(iset, "mov 100 p0", "slp 1", "mov 100 p1", "jmp mylabel");
        iset.jump("mylabel");
        assertExecutionOrder(iset, "slp 1", "mov 100 p1", "jmp mylabel");
    }

    @Test
    void isLabelExists() {
        var ins = InstructionSet.of("nop", "mov p0 p1", " label1: ", "end:x:");
        Assertions.assertTrue(ins.isLabelExists("label1"));
        Assertions.assertTrue(ins.isLabelExists("end:x"));
        Assertions.assertFalse(ins.isLabelExists("unknown"));
    }

    void assertInstructionText(String expected, Instruction actual) {
        Assertions.assertNotNull(actual);
        assertEquals(expected, actual.toString());
    }

    void assertExecutionOrder(InstructionSet iset, String... expected) {
        for (String s : expected) {
            var inst = iset.readInstruction();
            assertInstructionText(s, inst);
        }
    }
}