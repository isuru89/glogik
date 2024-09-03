package io.github.isuru89.games.shenzenio.components;

import io.github.isuru89.games.shenzenio.ports.Port;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration {

    private final List<Port> ports = new ArrayList<>();
    private final Map<String, Integer> registers = new HashMap<>();
    private int maxNoOfInstructions;

    public Configuration addRegister(String name) {
        registers.put(name, 0);
        return this;
    }

    public Map<String, Integer> getRegisters() {
        return registers;
    }

    public Configuration addPort(Port port) {
        this.ports.add(port);
        return this;
    }

    public List<Port> getPorts() {
        return ports;
    }

    public int getMaxNoOfInstructions() {
        return maxNoOfInstructions;
    }

    public Configuration setMaxNoOfInstructions(int maxNoOfInstructions) {
        this.maxNoOfInstructions = maxNoOfInstructions;
        return this;
    }
}
