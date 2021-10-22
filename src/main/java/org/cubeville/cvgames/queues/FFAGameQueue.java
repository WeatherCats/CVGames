package org.cubeville.cvgames.queues;

import org.bukkit.entity.Player;

import java.util.Set;

public class FFAGameQueue extends GameQueueOld {

	int numberOfTeams;
	private QueueableFFAGame game;

	public FFAGameQueue(QueueableFFAGame game, int playerCap) {
		super(game, playerCap);
		this.game = game;
	}

	@Override
	void setupGame(Set<Player> players) {
		System.out.println("setup game");
		game.startGame(players);
	}

}
