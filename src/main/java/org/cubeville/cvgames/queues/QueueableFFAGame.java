package org.cubeville.cvgames.queues;

import org.bukkit.entity.Player;

import java.util.Set;

public interface QueueableFFAGame extends QueueableGame {
	void startGame(Set<Player> players);

	boolean canStartQueue();

}
