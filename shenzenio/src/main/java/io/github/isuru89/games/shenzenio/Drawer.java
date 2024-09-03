package io.github.isuru89.games.shenzenio;

import io.github.isuru89.games.shenzenio.components.Component;
import io.github.isuru89.games.shenzenio.instructions.Instruction;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.TreeMap;

public class Drawer {

    public String snapshot(Component component) {
        var sb = new StringBuilder();

        var insSet = component.getInstructionSet();
        sb.append("@").append(":")
                .append(insSet.getTestCondition()).append(" ");

        var executed = insSet.getJustExecuted();
        sb.append(StringUtils.rightPad("<" + executed.map(Instruction::toString).orElse("") + ">", 14));

        var map = new TreeMap<String, Integer>();
        for (String portNo : component.getPortNames()) {
            var port = component.getPort(portNo);
            Optional<Integer> peek = port.peek();
            peek.ifPresent(integer -> map.put(portNo, integer));
        }

        sb.append(map).append("\t").append(component.getRegisters()).append("  | ");


        return sb.toString();
    }

    public String snapshot(Game game) {
        var sb = new StringBuilder();

        int c = 1;
        for (Component component : game.getComponents()) {
            sb.append("comp").append(c++).append(this.snapshot(component));
        }

        return sb.toString();
    }


}
