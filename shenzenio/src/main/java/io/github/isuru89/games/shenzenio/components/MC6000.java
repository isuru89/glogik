package io.github.isuru89.games.shenzenio.components;

import io.github.isuru89.games.shenzenio.ports.PortSimpleIO;
import io.github.isuru89.games.shenzenio.ports.PortXBus;

public class MC6000 extends Microcontroller {
    public MC6000(String id) {
        super(id, new Configuration()
                .setMaxNoOfInstructions(14)
                .addRegister("acc")
                .addRegister("dat")
                .addPort(new PortSimpleIO("p0"))
                .addPort(new PortSimpleIO("p1"))
                .addPort(new PortXBus("x0"))
                .addPort(new PortXBus("x1"))
                .addPort(new PortXBus("x2"))
                .addPort(new PortXBus("x3")));
    }
}
