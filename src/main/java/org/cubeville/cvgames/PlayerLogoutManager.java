package org.cubeville.cvgames;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerLogoutManager {

	private static HashMap<UUID, String> uuidToArenaName = new HashMap<>();

	public static void setPlayer(Player p, String name) {
		uuidToArenaName.put(p.getUniqueId(), name);
	}

	public static void removePlayer(Player p) {
		uuidToArenaName.remove(p.getUniqueId());
	}

	public static Arena getPlayerArena(Player p) {
		Arena result = ArenaManager.getArena(uuidToArenaName.get(p.getUniqueId()));
		removePlayer(p);
		return result;
	}
}