package org.cubeville.cvgames.models;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.models.Arena;

public interface PlayerContainer {
	 void whenPlayerLogout(Player p, Arena a);
}
