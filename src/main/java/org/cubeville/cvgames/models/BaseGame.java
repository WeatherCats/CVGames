package org.cubeville.cvgames.models;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.cubeville.cvgames.CVGames;
import org.cubeville.cvgames.enums.ArenaStatus;
import org.cubeville.cvgames.managers.PlayerManager;
import org.cubeville.cvgames.vartypes.GameVariable;
import org.cubeville.cvgames.vartypes.GameVariableList;
import org.cubeville.cvgames.vartypes.GameVariableObject;
import org.cubeville.cvgames.vartypes.GameVariableRegion;

import javax.annotation.Nullable;
import java.util.*;

abstract public class BaseGame implements PlayerContainer, Listener {
	private final String id;
	protected Arena arena;
	protected HashMap<Player, Object> state = new HashMap<>();
	private int arenaRegionTask;
	private boolean isRunningGame = false;

	public BaseGame(String id) {
		this.id = id;
		addGameVariable("region", new GameVariableRegion());
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

	public String getId() {
		return id;
	}

	public void startGame(Map<Integer, List<Player>> playerTeamMap, Arena arena) {
		playerTeamMap.values().forEach(players ->
				players.forEach(player -> {
					player.closeInventory();
					player.getInventory().clear();
				})
		);
		this.arena = arena;
		startArenaRegionCheck();
		processPlayerMap(playerTeamMap);
		isRunningGame = true;
	};

	public void finishGame() {
		arena.setStatus(ArenaStatus.OPEN);
		arena.getQueue().clear();
		killArenaRegionCheck();
		this.state.keySet().forEach(player -> {
			PlayerManager.removePlayer(player);
			player.teleport((Location) getVariable("exit"));
			player.getInventory().clear();
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
}
