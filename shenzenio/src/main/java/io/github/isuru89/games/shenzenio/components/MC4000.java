package io.github.isuru89.games.shenzenio.components;

import io.github.isuru89.games.shenzenio.ports.PortSimpleIO;
import io.github.isuru89.games.shenzenio.ports.PortXBus;

public class MC4000 extends Microcontroller {
    public MC4000(String id) {
        super(id, new Configuration()
                .setMaxNoOfInstructions(10)
                .addRegister("acc")
                .addPort(new PortSimpleIO("p0"))
                .addPort(new PortSimpleIO("p1"))
                .addPort(new PortXBus("x0"))
                .addPort(new PortXBus("x1")));
    }
}
