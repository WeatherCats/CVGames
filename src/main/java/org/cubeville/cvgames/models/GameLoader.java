package org.cubeville.cvgames.models;

public class GameLoader {
    private final GameLoaderExpectedInput input;
    public GameLoader(GameLoaderExpectedInput input) {
        this.input = input;
    }

    public BaseGame load(String id, String arenaName) {
        return input.load(id, arenaName);
    }
}
