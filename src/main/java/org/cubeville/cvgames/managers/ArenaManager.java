package org.cubeville.cvgames.managers;

import org.cubeville.cvgames.models.Arena;
import org.cubeville.cvgames.CVGames;
import org.cubeville.cvgames.models.BaseGame;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import static org.bukkit.Bukkit.getServer;
import static org.cubeville.cvgames.CVGames.*;

public class ArenaManager {

	private static HashMap<String, Arena> arenas = new HashMap<>();

	public static Arena getArena(String name) {
		return arenas.get(name);
	}

	public static void createArena(String name) {
		getInstance().getConfig().set("arenas." + name, new HashMap<>());
		getInstance().saveConfig();
		addArena(name);
	}

	public static void addArena(String name) {
		arenas.put(name, new Arena(name));
	}

	public static void deleteArena(String name) {
		getInstance().getConfig().set("arenas." + name, null);
		getInstance().saveConfig();
		arenas.remove(name);
	}

	public static void addArenaGame(String name, String game) throws Error {
		importArenaGame(name, game);
		getInstance().getConfig().set("arenas." + name + ".game", new ArrayList<>(arenas.get(name).getGameNames()));
		getInstance().saveConfig();
	}

	public static void importArenaGame(String name, String game) throws Error {
		BaseGame arenaGame = gameManager().getGameLoader(game).apply(game, name);
		getServer().getPluginManager().registerEvents(arenaGame, CVGames.getInstance());
		arenas.get(name).addGame(arenaGame);
	}

	public static void removeArenaGame(String name, String game) {
		if (!arenas.containsKey(name)) return;
		arenas.get(name).removeGameWithName(game);

		getInstance().getConfig().set("arenas." + name + ".game", new ArrayList<>(arenas.get(name).getGameNames()));
		getInstance().saveConfig();
	}

	public static boolean hasLoadedArena(String name) {
		return arenas.containsKey(name);
	}

	public static boolean hasArena(String name) {
		return hasLoadedArena(name) || getInstance().getConfig().contains("arenas." + name);
	}

	public static Collection<Arena> getArenas() {
		return arenas.values();
	}
}
