package org.cubeville.cvgames.models;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
	private final Map<String, GameVariable> verificationMap = new HashMap<>();

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
		if (teleportToExit) p.teleport((Location) arena.getGame().getVariable("exit"));
		onPlayerLeave(p);
		state.remove(p);
		if (state.isEmpty()) { finishGame();}
		p.getInventory().clear();
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
	};

	public void finishGame() {
		arena.setStatus(ArenaStatus.OPEN);
		arena.getQueue().clear();
		killArenaRegionCheck();
		onGameFinish();
		this.state.keySet().forEach(player -> {
			PlayerManager.removePlayer(player);
			player.teleport((Location) getVariable("exit"));
			player.getInventory().clear();
		});
	};

	public abstract void processPlayerMap(Map<Integer, List<Player>> playerTeamMap);

	public abstract void onGameFinish();

	// i hate this
	public GameVariable getGameVariable(String var, GameVariable inVariable) {
		if (!var.equals("") && var.charAt(0) == '.') {
			var = var.substring(1);
		}
		if (var.equals("")) { return inVariable; }
		String firstVar = var.split("\\.")[0];
		GameVariable gv;
		if (inVariable instanceof GameVariableList) {
			GameVariable variableAtIndex = ((GameVariableList<?>) inVariable).getVariableAtIndex(Integer.valueOf(firstVar));
			if (variableAtIndex != null) {
				gv = variableAtIndex;
			} else {
				gv = ((GameVariableList<?>) inVariable).addBlankGameVariable();
			}
		} else if (inVariable instanceof GameVariableObject) {
			// inVariable is an object
			gv = ((GameVariableObject) inVariable).getVariableAtField(firstVar);
		} else {
			return null;
		}

		if (gv instanceof GameVariableList || gv instanceof GameVariableObject) {
			// continued despair
			return getGameVariable(var.replaceFirst(firstVar, ""), gv);
		}
		return gv;
	}

	public GameVariable getGameVariable(String var) {
		String firstVar = var.split("\\.")[0];
		GameVariable gv = verificationMap.get(firstVar);
		if (gv instanceof GameVariableList || gv instanceof GameVariableObject) {
			// fuck
			gv.path = firstVar;
			return getGameVariable(var.replaceFirst(firstVar, ""), gv);
		}
		return gv;
	}

	public Object getVariable(String var) {
		return getGameVariable(var.toLowerCase()).getItem();
	}

	public boolean hasVariable(String var) {
		return verificationMap.containsKey(var);
	}

	public void addGameVariable(String varName, GameVariable variable) {
		addGameVariable(varName, variable, null);
	}

	public void addGameVariable(String varName, GameVariable variable, @Nullable Object defaultValue) {
		if (defaultValue != null) {
			if (defaultValue instanceof List && variable instanceof GameVariableList) {
				((GameVariableList) variable).setItems((List<Object>) defaultValue, "");
			} else {
				variable.setItem(defaultValue, "");
			}
		}
		verificationMap.put(varName.toLowerCase(), variable);
	}

	private void startArenaRegionCheck() {
		GameRegion gameRegion = (GameRegion) getVariable("region");

		arenaRegionTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(CVGames.getInstance(), () -> {
			for (Player player : state.keySet()) {
				if (!gameRegion.containsPlayer(player)) {
					kickPlayerFromGame(player, false);
				}
			}
		}, 0L, 20L);
	}

	private void killArenaRegionCheck() {
		Bukkit.getScheduler().cancelTask(arenaRegionTask);
		arenaRegionTask = -1;
	}

	public void setVarFromValue(String path, String arenaName) {
		String fullPath = "arenas." + arenaName + "." + path;
		path = path.replace("variables", "");
		if (!path.equals("") && path.charAt(0) == '.') {
			path = path.substring(1);
		}

		GameVariable gv = getGameVariable(path);
		if (gv == null) return;
		gv.path = path;
		gv.setItem(gv.getFromPath(fullPath), arenaName);
	}

	public Set<String> getVariables() {
		return this.verificationMap.keySet();
	}

}
