package org.cubeville.cvgames.managers;

import org.cubeville.cvgames.models.BaseGame;

import java.util.HashMap;

public class GameManager<T extends BaseGame> {

	private HashMap<String, Class<T>> games = new HashMap<>();

	public void registerGame(String name, Class<T> game) {
		games.put(name, game);
		ConfigImportManager.importConfiguration(name);
		SignManager.updateSigns();
	}

	public Class<T> getGame(String id) {
		return games.get(id);
	}

	public boolean hasGame(String name) {
		return games.containsKey(name);
	}
}
