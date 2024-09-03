package io.github.isuru89.games.shenzenio.instructions;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class Label extends MutedInstruction {
    public Label(String cmd) {
        super(cmd, null);
    }

    @Override
    public Optional<String> getNavigableId() {
        return Optional.of(StringUtils.substringBeforeLast(cmd, ":"));
    }

    @Override
    public String toString() {
        return cmd + ":";
    }
}
