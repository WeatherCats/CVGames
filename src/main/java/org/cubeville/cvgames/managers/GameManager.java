package org.cubeville.cvgames.managers;

import org.cubeville.cvgames.models.Game;

import java.util.HashMap;

public class GameManager<T extends Game> {

	private HashMap<String, Class<T>> games = new HashMap<>();

	public void registerGame(String name, Class<T> game) {
		System.out.println("registered: " + game.toString() + " as " + name);
		games.put(name, game);
		ConfigImportManager.importConfiguration(name);
	}

	public Class<T> getGame(String id) {
		return games.get(id);
	}

	public boolean hasGame(String name) {
		return games.containsKey(name);
	}

	public String filterGameInput(String gameInput) throws Error {
		String gameName = gameInput.toLowerCase();
		if (!hasGame(gameName)) {
			throw new Error("Game with name " + gameName + " does not exist!");
		}
		return gameInput;
	}
}
