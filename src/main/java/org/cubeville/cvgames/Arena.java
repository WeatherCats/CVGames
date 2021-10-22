package org.cubeville.cvgames;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Arena {

	private String name;
	private Game game;
	private GameQueue queue;
	private ArenaStatus status;


	public Arena(String name) {
		this.name = name;
	}

	public void setGame(Game game) {
		this.game = game;
		this.queue = new GameQueue(this, game.getQueueMin(), game.getQueueMax());
	}

	public Game getGame() {
		return game;
	}

	public String getName() {
		return name;
	}

	public void setStatus(ArenaStatus status) {
		this.status = status;
	}

	public ArenaStatus getStatus() {
		return status;
	}

	public GameQueue getQueue() {
		return queue;
	}

	public void playerLogoutCleanup(Player p) {
		if (status == ArenaStatus.OPEN) {
			queue.onPlayerLogout(p);
		} else {
			game.onPlayerLogout(p);
		}
	}
}
