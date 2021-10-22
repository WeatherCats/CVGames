package org.cubeville.cvgames;

import java.util.HashMap;

public class GameManager<T extends Game> {

	private HashMap<String, Class<T>> games = new HashMap<>();

	public void registerGame(String name, Class<T> game) {
		games.put(name, game);
	}

	public Class<T> getGame(String id) {
		return games.get(id);
	}

	public boolean hasGame(String name) {
		return games.containsKey(name);
	}
}
