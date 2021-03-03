package org.cubeville.cvgames;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.CommandResponse;

import java.util.*;

class GameQueue {

	private Set<Player> queueing = new HashSet<>();
	private QueueableGame game;
	private int players;

	GameQueue(QueueableGame game, int players) {
		this.game = game;
		this.players = players;
	}

	CommandResponse execute(Player p, List<Object> baseParameters) {
		System.out.println(baseParameters);
		if (baseParameters.get(0).equals("join")) {
			// add to queue if the game can handle a queue and queue does not already contain player
			if (game.canStartQueue() && !queueing.contains(p)) {
				queueing.add(p);
				CommandResponse cr = new CommandResponse("&bYou joined the queue!");
				for (Player queued : queueing) {
					queued.sendMessage("§e" + queueing.size() + "/" + players + " players in the queue." );
				}
				if (queueing.size() >= players) {
					game.startGame(queueing);
					queueing.clear();
				}
			}
			return new CommandResponse("&bYou are already in the queue!");
		} else if (baseParameters.get(0).equals("leave")) {
			queueing.remove(p);
			if (!queueing.isEmpty()) {
				for (Player queued : queueing) {
					queued.sendMessage("§e" + queueing.size() + "/" + players + " players in the queue.");
				}
			}
			return new CommandResponse("&bYou have left the queue.");
		} else {
			return new CommandResponse("&b:(");
		}
	}


}
