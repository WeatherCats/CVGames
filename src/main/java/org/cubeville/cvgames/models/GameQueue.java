package org.cubeville.cvgames.models;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.cubeville.cvgames.CVGames;
import org.cubeville.cvgames.utils.GameUtils;
import org.cubeville.cvgames.enums.ArenaStatus;
import org.cubeville.cvgames.managers.PlayerLogoutManager;
import org.cubeville.cvgames.managers.SignManager;
import org.cubeville.cvgames.vartypes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameQueue implements PlayerContainer {

	private List<Player> players = new ArrayList<>();
	private Arena arena;
	private int countdownTimer;
	private int counter;

	public GameQueue(Arena arena) {
		this.arena = arena;
		arena.getGame().addGameVariable("queue-min", new GameVariableInt());
		arena.getGame().addGameVariable("queue-max", new GameVariableInt());
		arena.getGame().addGameVariable("lobby", new GameVariableLocation());
		arena.getGame().addGameVariable("exit", new GameVariableLocation());
		arena.getGame().addGameVariable("signs", new GameVariableList<>(GameVariableQueueSign.class));
	}

	private boolean canJoinQueue(Player p) {
		if (players.contains(p)) {
			p.sendMessage(ChatColor.RED + "You are already in this queue!");
			return false;
		}
		if (players.size() == (Integer) arena.getGame().getVariable("queue-max")) {
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

	public boolean join(Player p) {
		if (!canJoinQueue(p)) {
			return false;
		}
		players.add(p);
		setPlayerToLobby(p);
		PlayerLogoutManager.setPlayer(p, arena.getName());
		if (players.size() == (Integer) arena.getGame().getVariable("queue-min")) {
			startCountdown(20);
		}
		final int SPEED_COUNTDOWN = 6;
		if (players.size() == getMaxPlayers() && counter > SPEED_COUNTDOWN) {
			endCountdown();
			GameUtils.messagePlayerList(players, "§bQueue has been filled, starting game.", Sound.BLOCK_DISPENSER_DISPENSE);
			startCountdown(SPEED_COUNTDOWN);
		}
		return true;
	}

	private void setPlayerToLobby(Player p) {
		p.teleport((Location) arena.getGame().getVariable("lobby"));
		PlayerInventory inv = p.getInventory();
		inv.clear();
		Bukkit.getScheduler().scheduleSyncDelayedTask(CVGames.getInstance(), () -> {
			inv.setItem(0, queueLeaveItem());
		}, 20L);
		GameUtils.messagePlayerList(players, "§b" + p.getName() + " has joined the queue!", Sound.BLOCK_DISPENSER_DISPENSE);
	}

	private void removePlayerFromLobby(Player p) {
		p.teleport((Location) arena.getGame().getVariable("exit"));
		p.getInventory().clear();
 		GameUtils.messagePlayerList(players, "§b" + p.getName() + " has left the queue.", Sound.BLOCK_DISPENSER_DISPENSE);
	}

	public void leave( Player p ) {
		if (!players.contains(p)) { return; }
		players.remove(p);
		SignManager.updateArenaSignsFill(arena.getName());
		PlayerLogoutManager.removePlayer(p);
		if (arena.getStatus() != ArenaStatus.OPEN) { return; }
		p.sendMessage("§bYou have left the queue.");
		removePlayerFromLobby(p);
		if (players.size() == (getMinPlayers() - 1)) {
			GameUtils.messagePlayerList(players, "§cCountdown cancelled -- Not enough players!");
			endCountdown();
		}
	}

	public int size() {
		return players.size();
	}

	public int getMaxPlayers() {
		Integer max = (Integer) arena.getGame().getVariable("queue-max");
		if (max == null) { return 0; }
		return max;
	}

	public int getMinPlayers() {
		Integer min = (Integer) arena.getGame().getVariable("queue-min");
		if (min == null) { return 0; }
		return min;
	}


	private void startCountdown(int startCount) {
		counter = startCount;
		this.countdownTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(CVGames.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (counter > 0) {
					if (counter % 10 == 0 || counter <= 5)
					GameUtils
						.messagePlayerList(players, "§e" + counter + " seconds until the game starts",
							Sound.BLOCK_NOTE_BLOCK_PLING);
					counter--;
				} else {
					endCountdown();
					arena.setStatus(ArenaStatus.IN_USE);
					arena.getGame().startGame(players, arena);
				}
			}
		}, 0L, 20L);
	}

	private void endCountdown() {
		Bukkit.getScheduler().cancelTask(this.countdownTimer);
	}

	@Override
	public void whenPlayerLogout(Player p, Arena a) {
		leave(p);
	}

	public ItemStack queueLeaveItem() {
		return GameUtils.customItem(Material.RED_BED, "§c§l§oLeave Queue §7§o(Right Click)");
	}

	public void clear() {
		players.clear();
		SignManager.updateArenaSignsFill(arena.getName());
	}
}
