package io.github.isuru89.games.shenzenio;

import java.util.Optional;

public interface Accessible extends ResetHandler {

    Value write(int newValue);

    Value read();

    Optional<Integer> peek();

}
