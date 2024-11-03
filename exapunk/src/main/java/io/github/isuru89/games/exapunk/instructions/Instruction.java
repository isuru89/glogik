package io.github.isuru89.games.exapunk.instructions;

import io.github.isuru89.games.exapunk.ExA;

public abstract class Instruction {

    protected final ExA exA;
    protected final String[] args;

    protected Instruction(ExA exA, String[] args) {
        this.exA = exA;
        this.args = args;
    }

    public void prepare() {

    }

    public void execute() {

    }

    protected void prepareRegisterRead(String register) {
        if ("M".equalsIgnoreCase(register)) {
            var refValue = exA.isLocal() ? exA.getCurrentHost().orElseThrow().getLocalM() :
                    exA.getCurrentLevel().getGlobalM();
            refValue.submitRead(exA);
        }
    }

    protected void prepareRegisterWrite(String register) {
        if ("M".equalsIgnoreCase(register)) {
            var refValue = exA.isLocal() ? exA.getCurrentHost().orElseThrow().getLocalM() :
                    exA.getCurrentLevel().getGlobalM();
            refValue.submitWrite(exA);
        }
    }


    protected void mustHaveOnlyOneArgument() {
        mustHaveOnlyNArguments(1);
    }

    protected void mustHaveOnlyTwoArguments() {
        mustHaveOnlyNArguments(2);
    }

    protected void mustHaveOnlyThreeArguments() {
        mustHaveOnlyNArguments(3);
    }

    protected void mustHaveOnlyNArguments(int n) {
        if (args.length != n + 1) {
            throw new RuntimeException("only " + n + " argument(s) expected!");
        }
    }
}
