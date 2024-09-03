package io.github.isuru89.games.shenzenio.instructions;

public class Comment extends MutedInstruction {
    public Comment(String text) {
        super("comment", new String[]{text});
    }

    @Override
    public String toString() {
        return "#" + ops[0];
    }
}
