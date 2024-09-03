package io.github.isuru89.games.shenzenio.components;

import io.github.isuru89.games.shenzenio.BlockType;
import io.github.isuru89.games.shenzenio.GameClock;
import io.github.isuru89.games.shenzenio.InstructionSet;
import io.github.isuru89.games.shenzenio.ResetHandler;
import io.github.isuru89.games.shenzenio.Value;
import io.github.isuru89.games.shenzenio.instructions.ExecutionContext;
import io.github.isuru89.games.shenzenio.instructions.TestCondition;
import io.github.isuru89.games.shenzenio.ports.Port;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class Component implements ResetHandler {

    private final String id;
    private final Configuration configuration;

    private final Map<String, Port> ports = new HashMap<>();
    private final Map<String, Integer> registers = new HashMap<>();
    private final Stack<String> blockWaitingPorts = new Stack<>();
    private InstructionSet instructionSet;
    private BlockType blockType = BlockType.NONE;
    private int blockedUntilCycle = -1;

    private ExecutionContext executionContext;

    public Component(String id, Configuration configuration) {
        this.id = id;
        this.configuration = configuration;

        configuration.getPorts().forEach(port -> ports.put(port.getAddress(), port));
        registers.putAll(configuration.getRegisters());
    }

    public Port getPort(String id) {
        return ports.get(id);
    }

    public Map<String, Integer> getRegisters() {
        return registers;
    }

    public Set<String> getPortNames() {
        return ports.keySet();
    }

    public InstructionSet getInstructionSet() {
        return instructionSet;
    }

    public BlockType execute(GameClock clock) {
        if (executionContext == null) {
            executionContext = new ComponentRuntime(this, clock);
        }

        if (blockType == BlockType.SLEEP) {
            if (clock.getCurrentTick() >= blockedUntilCycle) {
                blockType = BlockType.NONE;
                blockedUntilCycle = -1;
            } else {
                return blockType;
            }
        } else if (blockType == BlockType.IO) {
            if (blockWaitingPorts.empty()) {
                throw new RuntimeException("marked as waiting but nothing specified");
            }

            var port = blockWaitingPorts.peek();
            if (ports.get(port).peek().isPresent()) {
                // value is present
                blockType = BlockType.NONE;
                blockWaitingPorts.clear();
            } else {
                return blockType;
            }
        }


        var inst = instructionSet.readInstruction();
        var result = inst.execute(executionContext);

        instructionSet.setTestCondition(result.testCondition());

        return result.blockType();
    }

    public Component addInstructions(InstructionSet instructionSet) {
        this.instructionSet = instructionSet;
        return this;
    }

    @Override
    public void reset() {
        this.instructionSet.reset();
        this.blockWaitingPorts.clear();
        this.blockedUntilCycle = -1;
        this.blockType = BlockType.NONE;
        this.executionContext = null;
    }

    private record ComponentRuntime(Component component, GameClock clock) implements ExecutionContext {

        @Override
        public int getCurrentTick() {
            return clock.getCurrentTick();
        }

        @Override
        public TestCondition getTestCondition() {
            return component.getInstructionSet().getTestCondition();
        }

        @Override
        public Value readValue(String registerPortOrValue) {
            if (component.getRegisters().containsKey(registerPortOrValue)) {
                return Value.nonBlocked(component.getRegisters().get(registerPortOrValue));
            } else if (component.getPortNames().contains(registerPortOrValue)) {
                var val = component.getPort(registerPortOrValue).read();
                if (val.isBlocked()) {
                    awaitForValueInAddress(registerPortOrValue);
                }
                return val;
            }

            return Value.nonBlocked(Integer.parseInt(registerPortOrValue));
        }

        @Override
        public Value writeValue(String registerOrPort, int newValue) {
            if (component.getRegisters().containsKey(registerOrPort)) {
                component.getRegisters().put(registerOrPort, newValue);
                return Value.nonBlocked(newValue);

            } else if (component.getPortNames().contains(registerOrPort)) {
                return component.getPort(registerOrPort).write(newValue);
            }

            throw new RuntimeException("unknown place to write!");
        }

        @Override
        public void awaitForValueInAddress(String port) {
            if (component.blockWaitingPorts.contains(port)) {
                return;
            }

            component.blockWaitingPorts.push(port);
        }

        @Override
        public void awaitForNextTick(int nextTickToAwake) {
            component.blockedUntilCycle = nextTickToAwake;
        }

        @Override
        public void jumpToLabel(String label) {
            component.instructionSet.jump(label);
        }
    }

}
