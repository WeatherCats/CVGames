package org.cubeville.cvgames;

import org.bukkit.Location;
import org.cubeville.commons.commands.CommandExecutionException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

import static org.cubeville.cvgames.CVGames.*;

public class ArenaManager {

	private static HashMap<String, Arena> arenas = new HashMap<>();

	public static Arena getArena(String name) {
		return arenas.get(name);
	}

	public static void addArena(String name) {
		getInstance().getConfig().set("arenas." + name, new Object());
		getInstance().saveConfig();
		arenas.put(name, new Arena(name));
	}

	public static void deleteArena(String name) {
		getInstance().getConfig().set("arenas." + name, null);
		getInstance().saveConfig();
		arenas.remove(name);
	}

	public static void setArenaGame(String name, String game) throws CommandExecutionException {
		try {
			arenas.get(name).setGame((Game) gameManager().getGame(game).getDeclaredConstructor().newInstance());
		}
		catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			e.printStackTrace();
			throw new CommandExecutionException("Could not set arena game properly, please contact a system administrator.");
		}
		getInstance().getConfig().set("arenas." + name + ".game", game);
		getInstance().saveConfig();
	}

	public static boolean hasArena(String name) {
		return arenas.containsKey(name) || getInstance().getConfig().contains("arenas." + name);
	}

}
