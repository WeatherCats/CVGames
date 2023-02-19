package org.cubeville.cvgames.managers;

import org.cubeville.cvgames.models.BaseGame;

import java.util.HashMap;
import java.util.function.BiFunction;

public class GameManager {

    private HashMap<String, BiFunction<String, String, BaseGame>> games = new HashMap<>();

    public void registerGame(String name, BiFunction<String, String, BaseGame> fn) {
        games.put(name, fn);
        ConfigImportManager.importConfiguration(name);
        SignManager.updateSigns();
    }

    public BiFunction<String, String, BaseGame> getGameLoader(String id) {
        return games.get(id);
    }

    public boolean hasGame(String name) {
        return games.containsKey(name);
    }
}
