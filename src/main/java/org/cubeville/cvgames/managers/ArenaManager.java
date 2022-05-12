package org.cubeville.cvgames.managers;

import org.cubeville.cvgames.models.Arena;
import org.cubeville.cvgames.CVGames;
import org.cubeville.cvgames.models.Game;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

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

	public static void setArenaGame(String name, String game) throws Error {
		try {
			Class[] cArgs = new Class[1];
			cArgs[0] = String.class;
			Game arenaGame = (Game) gameManager().getGame(game).getDeclaredConstructor(cArgs).newInstance(name);
			getServer().getPluginManager().registerEvents(arenaGame, CVGames.getInstance());

			arenas.get(name).setGame(arenaGame);
		}
		catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			e.printStackTrace();
			throw new Error("Could not set game properly for arena " + name + " and game " + game + "!");
		}
		getInstance().getConfig().set("arenas." + name + ".game", game);
		getInstance().saveConfig();
	}



	public static boolean hasArena(String name) {
		return arenas.containsKey(name) || getInstance().getConfig().contains("arenas." + name);
	}

	public static String filterArenaInput(String name) throws Error {
		String arenaName = name.toLowerCase();
		if (!hasArena(arenaName)) {
			throw new Error("Arena with name " + arenaName + " does not exist!");
		}
		return arenaName;
	}
}
