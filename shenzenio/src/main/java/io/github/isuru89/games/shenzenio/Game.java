package io.github.isuru89.games.shenzenio;

import io.github.isuru89.games.shenzenio.components.Component;
import io.github.isuru89.games.shenzenio.ports.CaptureSimpleIO;
import io.github.isuru89.games.shenzenio.ports.Port;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Game implements ResetHandler, Iterable<Integer> {

    private final Map<String, CaptureSimpleIO> outputs = new HashMap<>();
    private final Map<String, Port> inputs = new HashMap<>();
    private final List<Component> components = new ArrayList<>();

    void addOutput(CaptureSimpleIO capture) {
        outputs.put(capture.getAddress(), capture);
    }

    void addInput(Port input) {
        inputs.put(input.getAddress(), input);
    }

    void addComponent(Component component) {
        components.add(component);
    }

    List<Component> getComponents() {
        return components;
    }

    Iterator<Integer> iterator(int cycles) {
        return new Game.ExecutionIterator(cycles, this);
    }

    void execute(int cycles) {
        var it = iterator(cycles);
        while (it.hasNext()) {
            it.next();
        }
    }

    @Override
    public Iterator<Integer> iterator() {
        return iterator(100);
    }

    @Override
    public void reset() {
        for (var comp : components) {
            comp.reset();
        }
    }

    private static class ExecutionIterator implements Iterator<Integer> {

        private final int maxCycles;
        private final Game game;
        private GameClock clock;

        private ExecutionIterator(int maxCycles, Game game) {
            this.maxCycles = maxCycles;
            this.game = game;

            this.init();
        }

        private void init() {
            var tickList = new ArrayList<TickHandler>();
            tickList.addAll(game.inputs.values());
            tickList.addAll(game.outputs.values());
            clock = new GameClock().withTickables(tickList);
        }

        @Override
        public boolean hasNext() {
            return clock.getCurrentTick() < maxCycles;
        }

        @Override
        public Integer next() {
            int notBlocked = 0, blockedIO = 0, blockedSleep = 0;
            for (var component : game.components) {
                var blockType = component.execute(clock);
                switch (blockType) {
                    case NONE -> notBlocked++;
                    case IO -> blockedIO++;
                    case SLEEP -> blockedSleep++;
                }
            }

            if (notBlocked == 0) {
                if (blockedIO > 0 || blockedSleep > 0) {
                    clock.tick();
                }
            }

            return 0;
        }
    }

}
