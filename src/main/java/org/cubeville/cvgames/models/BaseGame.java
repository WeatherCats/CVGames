package org.cubeville.cvgames.models;

import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Scoreboard;
import org.cubeville.cvgames.CVGames;
import org.cubeville.cvgames.enums.ArenaStatus;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.managers.PlayerManager;
import org.cubeville.cvgames.utils.GameUtils;
import org.cubeville.cvgames.vartypes.GameVariable;
import org.cubeville.cvgames.vartypes.GameVariableList;
import org.cubeville.cvgames.vartypes.GameVariableObject;
import org.cubeville.cvgames.vartypes.GameVariableRegion;

import javax.annotation.Nullable;
import java.util.*;

abstract public class BaseGame implements PlayerContainer, Listener {
	private final String id;
	protected Arena arena;
	public HashMap<Player, PlayerState> state = new HashMap<>();
	private int arenaRegionTask;
	public boolean isRunningGame = false;

	public BaseGame(String id, String arenaName) {
		this.id = id;
		this.arena = ArenaManager.getArena(arenaName);
		this.arena.addGameVariable("region", new GameVariableRegion());
	}

	@Override
	public void whenPlayerLogout(Player p, Arena a) {
		kickPlayerFromGame(p, true);
	}

	private void kickPlayerFromGame(Player p, boolean teleportToExit) {
		PlayerManager.removePlayer(p);
		onPlayerLeave(p);
		state.remove(p);
		p.getInventory().clear();
		if (teleportToExit) { p.teleport((Location) arena.getGame(id).getVariable("exit")); }
		if (isRunningGame && state.isEmpty()) { finishGame(); }
	}

	public abstract void onPlayerLeave(Player p);

	protected abstract PlayerState getState(Player p);

	public String getId() {
		return id;
	}

	public void startGame(Map<Integer, List<Player>> playerTeamMap) {
		playerTeamMap.values().forEach(players ->
				players.forEach(player -> {
					player.closeInventory();
					player.getInventory().clear();
				})
		);
		startArenaRegionCheck();
		processPlayerMap(playerTeamMap);
		isRunningGame = true;
	};

	public void finishGame() {
		if (!arena.getStatus().equals(ArenaStatus.HOSTING)) { arena.setStatus(ArenaStatus.OPEN); }
		arena.getQueue().clear();
		killArenaRegionCheck();
		this.state.keySet().forEach(player -> {
			if (arena.getStatus().equals(ArenaStatus.HOSTING)) {
				player.teleport((Location) getVariable("lobby"));
				player.getInventory().clear();
				arena.getQueue().setLobbyInventory(player.getInventory());
			} else {
				PlayerManager.removePlayer(player);
				player.teleport((Location) getVariable("exit"));
				player.getInventory().clear();
			}
		});
		onGameFinish();
		state.clear();
		isRunningGame = false;
	};

	public abstract void processPlayerMap(Map<Integer, List<Player>> playerTeamMap);

	public abstract void onGameFinish();

	private void startArenaRegionCheck() {
		GameRegion gameRegion = (GameRegion) getVariable("region");

		arenaRegionTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(CVGames.getInstance(), () -> {
			for (Player player : state.keySet()) {
				if (!gameRegion.containsPlayer(player)) {
					kickPlayerFromGame(player, false);
					player.sendMessage("§cYou have left the game!");
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, .7F);
				}
			}
		}, 0L, 20L);
	}

	private void killArenaRegionCheck() {
		Bukkit.getScheduler().cancelTask(arenaRegionTask);
		arenaRegionTask = -1;
	}

	public Object getVariable(String var) {
		return arena.getVariable(var);
	}

	public void addGameVariable(String varName, GameVariable variable) {
		addGameVariable(varName, variable, null);
	}

	public void addGameVariable(String varName, GameVariable variable, @Nullable Object defaultValue) {
		arena.addGameVariable(varName, variable, defaultValue);
	}

	public void addGameVariableObjectList(String varName, HashMap<String, GameVariable> fields) {
		arena.addGameVariableObjectList(varName, fields);
	}

	public void addGameVariableTeamsList(HashMap<String, GameVariable> fields) {
		arena.addGameVariableTeamsList(fields);
	}

	public void sendScoreboardToArena(Scoreboard scoreboard) {
		arena.getQueue().getPlayerSet().forEach(p -> p.setScoreboard(scoreboard));
	}

	public void sendMessageToArena(String message) {
		arena.getQueue().getPlayerSet().forEach(p -> p.sendMessage(message));
	}

}
