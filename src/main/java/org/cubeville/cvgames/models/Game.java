package org.cubeville.cvgames.models;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.cubeville.cvgames.enums.ArenaStatus;
import org.cubeville.cvgames.managers.PlayerLogoutManager;
import org.cubeville.cvgames.vartypes.GameVariable;
import org.cubeville.cvgames.vartypes.GameVariableList;
import org.cubeville.cvgames.vartypes.GameVariableObject;

import javax.annotation.Nullable;
import java.util.*;

abstract public class Game implements PlayerContainer, Listener {
	private String id;
	protected Arena arena;
	private final Map<String, GameVariable> verificationMap = new HashMap<>();

	public Game(String id) {
		this.id = id;
	}

	@Override
	public void whenPlayerLogout(Player p, Arena a) {
		PlayerLogoutManager.removePlayer(p);
		p.teleport((Location) a.getGame().getVariable("exit"));
		onPlayerLogout(p);
		p.getInventory().clear();
	}

	public abstract void onPlayerLogout(Player p);

	public String getId() {
		return id;
	}

	public void startGame(List<Player> players, Arena arena) {
		players.forEach(player -> player.getInventory().clear());
		this.arena = arena;
		onGameStart(players);
	};

	public void finishGame(List<Player> players) {
		arena.setStatus(ArenaStatus.OPEN);
		arena.getQueue().clear();
		onGameFinish(players);
		players.forEach(p -> {
			PlayerLogoutManager.removePlayer(p);
			p.teleport((Location) getVariable("exit"));
			p.getInventory().clear();
		});
	};

	public abstract void onGameStart(List<Player> players);

	public abstract void onGameFinish(List<Player> players);

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
