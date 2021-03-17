package org.cubeville.cvgames.queues;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public interface QueueableTeamGame extends QueueableGame {
	void startGame(List<Set<Player>> players);

	boolean canStartQueue();

}
