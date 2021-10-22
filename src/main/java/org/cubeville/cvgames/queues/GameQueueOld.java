package org.cubeville.cvgames.queues;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.CommandResponse;

import java.util.*;

abstract public class GameQueueOld {

	private Set<Player> queueing = new HashSet<>();
	private QueueableGame game;
	private int players;

	public GameQueueOld(QueueableGame game, int players) {
		this.game = game;
		this.players = players;
	}

	public CommandResponse execute(Player p, List<Object> baseParameters) {
		System.out.println(baseParameters);
		if (baseParameters.get(0).equals("join")) {
			// add to queues if the game can handle a queues and queues does not already contain player
			if (game.canStartQueue() && !queueing.contains(p)) {
				queueing.add(p);
				for (Player queued : queueing) {
					queued.sendMessage("§e" + queueing.size() + "/" + players + " players in the queue." );
				}
				System.out.println(queueing.size());
				System.out.println(queueing);
				if (queueing.size() >= players) {
					this.setupGame(queueing);
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

	abstract void setupGame(Set<Player> player);

}
