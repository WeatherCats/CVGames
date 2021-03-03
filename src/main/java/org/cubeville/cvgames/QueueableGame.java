package org.cubeville.cvgames;

import org.bukkit.entity.Player;

import java.util.Set;

public interface QueueableGame {
	public void startGame(Set<Player> players);

	public boolean canStartQueue();
}
