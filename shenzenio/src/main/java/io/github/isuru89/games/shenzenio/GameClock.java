package io.github.isuru89.games.shenzenio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameClock {

    private final List<TickHandler> tickHandlers = new ArrayList<>();

    private int curr;

    public void tick() {
        curr++;
        for (var c : tickHandlers) {
            c.tick(curr);
        }
    }

    public int getCurrentTick() {
        return curr;
    }

    public GameClock withTickables(Collection<? extends TickHandler> allCaptures) {
        tickHandlers.addAll(allCaptures);
        return this;
    }

}
