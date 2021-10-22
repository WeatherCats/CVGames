package org.cubeville.cvgames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameQueue implements PlayerContainer {

	private List<Player> players = new ArrayList<>();
	private Arena arena;
	private int minPlayers;
	private int maxPlayers;
	private int countdownTimer;
	private List<Location> signs;

	GameQueue(Arena arena, int minPlayers, int maxPlayers) {
		this.arena = arena;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
	}

	private boolean canJoinQueue(Player p) {
		if (players.contains(p)) {
			p.sendMessage(ChatColor.RED + "You are already in this queue!");
			return false;
		}
		if (players.size() == maxPlayers) {
			p.sendMessage(ChatColor.RED + "This arena is full!");
			return false;
		}
		if (arena.getStatus().equals(ArenaStatus.IN_USE)) {
			p.sendMessage(ChatColor.RED + "This arena is currently in use. Please try again later.");
			return false;
		}
		if (arena.getStatus().equals(ArenaStatus.CLOSED)) {
			p.sendMessage(ChatColor.RED + "This arena is currently closed. Please try again later.");
			return false;
		}
		return true;
	}

	void join(Player p) {
		if (!canJoinQueue(p)) {
			return;
		}
		players.add(p);
		if (players.size() == minPlayers) {
			startCountdown();
		}
	}

	public void leave( Player p ) {
		players.remove(p);
		if (players.size() == minPlayers - 1) {
			endCountdown();
		}
	}


	public int size() {
		return players.size();
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}
	private void startCountdown() {
		this.countdownTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(CVGames.getInstance(), new Runnable() {
			int counter = 3;
			@Override
			public void run() {
				if (counter > 0) {
					GameUtils.messagePlayerList(players,"§c" + (counter * 10) + "seconds until the game starts", Sound.BLOCK_DISPENSER_DISPENSE);
					counter--;
				} else {
					endCountdown();
					arena.getGame().startGame(players, arena);
				}
			}
		}, 0L, 200L);
	}

	private void endCountdown() {
		Bukkit.getScheduler().cancelTask(this.countdownTimer);
	}

	@Override
	public void onPlayerLogout(Player p) {
		leave(p);
	}
}
