package io.github.isuru89.games.shenzenio.components;

import io.github.isuru89.games.shenzenio.BlockType;
import io.github.isuru89.games.shenzenio.GameClock;
import io.github.isuru89.games.shenzenio.ports.PortSimpleIO;

public class DX300 extends Component {
    public DX300(String id) {
        super(id, new Configuration()
                .addPort(new PortSimpleIO("p0"))
                .addPort(new PortSimpleIO("p1"))
                .addPort(new PortSimpleIO("p2"))
        );
    }

    @Override
    public BlockType execute(GameClock clock) {
        // TODO
        return BlockType.NONE;
    }
}
