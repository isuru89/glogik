package io.github.isuru89.games.shenzenio.instructions;

import io.github.isuru89.games.shenzenio.InstructionSet;

public enum TestCondition {
    ANY(" "),
    TRUE("+"),
    FALSE("-");

    private final String abbr;

    TestCondition(String abbr) {
        this.abbr = abbr;
    }

    public static TestCondition from(String line) {
        if (line.startsWith("+")) {
            return TRUE;
        } else if (line.startsWith("-")) {
            return FALSE;
        }
        return ANY;
    }

    @Override
    public String toString() {
        return abbr;
    }
}
