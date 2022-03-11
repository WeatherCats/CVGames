package org.cubeville.cvgames;

import org.cubeville.commons.commands.CommandExecutionException;

import java.util.HashMap;

public class GameManager<T extends Game> {

	private HashMap<String, Class<T>> games = new HashMap<>();

	public void registerGame(String name, Class<T> game) {
		System.out.println("registered: " + game.toString() + " as " + name);
		games.put(name, game);
	}

	public Class<T> getGame(String id) {
		return games.get(id);
	}

	public boolean hasGame(String name) {
		return games.containsKey(name);
	}

	public String filterGameInput(String gameInput) throws CommandExecutionException {
		String gameName = gameInput.toLowerCase();
		if (!hasGame(gameName)) {
			throw new CommandExecutionException("Game with name " + gameName + " does not exist!");
		}
		return gameInput;
	}
}
