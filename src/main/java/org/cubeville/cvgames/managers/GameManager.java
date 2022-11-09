package org.cubeville.cvgames.managers;

import org.cubeville.cvgames.models.BaseGame;
import org.cubeville.cvgames.models.GameLoader;

import java.util.HashMap;

public class GameManager {

	private HashMap<String, GameLoader> games = new HashMap<>();

	public void registerGame(String name, GameLoader gameLoader) {
		games.put(name, gameLoader);
		ConfigImportManager.importConfiguration(name);
		SignManager.updateSigns();
	}

	public GameLoader getGameLoader(String id) {
		return games.get(id);
	}

	public boolean hasGame(String name) {
		return games.containsKey(name);
	}
}
