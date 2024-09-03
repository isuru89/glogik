package io.github.isuru89.games.shenzenio.instructions;

import io.github.isuru89.games.shenzenio.instructions.arithmetic.OpAdd;
import io.github.isuru89.games.shenzenio.instructions.arithmetic.OpSub;
import io.github.isuru89.games.shenzenio.instructions.checks.CheckEq;
import io.github.isuru89.games.shenzenio.instructions.checks.CheckGreaterThan;
import io.github.isuru89.games.shenzenio.instructions.checks.CheckLessThan;

public class Factory {

    public static Instruction create(String codeLine) {
        if (codeLine == null || codeLine.trim().isBlank()) {
            return new Blank();
        }

        var line = codeLine.trim().toLowerCase();

        var activation = TestCondition.from(line);
        if (line.startsWith("+") || line.startsWith("-")) {
            line = line.substring(1);
        }

        if (line.startsWith("#")) {
            return new Comment(line.substring(1).trim());
        }

        if (line.endsWith(":")) {
            return new Label(line);
        }

        String[] tokens = line.trim().split("[\\s+]");
        var cmd = tokens[0];

        return switch (cmd) {
            case "mov" -> new Move(cmd, tokens, activation);
            case "slp" -> new Sleep(cmd, tokens, activation);
            case "slx" -> new WaitForValue(cmd, tokens, activation);
            case "tgt" -> new CheckGreaterThan(cmd, tokens, activation);
            case "tlt" -> new CheckLessThan(cmd, tokens, activation);
            case "teq" -> new CheckEq(cmd, tokens, activation);
            case "add" -> new OpAdd(cmd, tokens, activation);
            case "sub" -> new OpSub(cmd, tokens, activation);
            case "not" -> new Invert(cmd, tokens, activation);
            case "nop" -> new NoOp(cmd, tokens, activation);
            case "jmp" -> new Jump(cmd, tokens, activation);
            default -> throw new RuntimeException("unknown command " + cmd);
        };

    }

}
