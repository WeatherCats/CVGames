package org.cubeville.cvgames;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.vartypes.GameVariable;
import org.cubeville.cvgames.vartypes.GameVariableList;

import javax.annotation.Nullable;
import java.util.*;

abstract public class Game implements PlayerContainer {
	private String id;
	private final Map<String, GameVariable> verificationMap = new HashMap<>();

	public Game(String id) {
		this.id = id;
	}

	@Override
	public void whenPlayerLogout(Player p, Arena a) {
		PlayerLogoutManager.removePlayer(p);
		p.teleport((Location) a.getGame().getVariable("exit"));
		p.getInventory().clear();
		SignManager.updateArenaSignsFill(a.getName());
		onPlayerLogout(p);
	}

	public abstract void onPlayerLogout(Player p);

	public String getId() {
		return id;
	}

	public void startGame(List<Player> players, Arena arena) {
		//TODO -- STUFF FOR EVERY GAME START
		onGameStart(players);
	};

	public void finishGame(List<Player> players, Arena arena) {
		arena.setStatus(ArenaStatus.OPEN);
		players.forEach(PlayerLogoutManager::removePlayer);
		onGameFinish(players);
	};

	public abstract void onGameStart(List<Player> players);

	public abstract void onGameFinish(List<Player> players);

	public  Map<String, GameVariable> getVerificationMap() {
		return verificationMap;
	}

	public GameVariable getGamesVariable(String var) {
		return verificationMap.get(var);
	}

	public Object getVariable(String var) {
		return getGamesVariable(var.toLowerCase()).getItem();
	}

	public boolean hasVariable(String var) {
		return verificationMap.containsKey(var);
	}

	public void addGamesVariable(String varName, GameVariable variable) {
		addGamesVariable(varName, variable, null);
	}

	public void addGamesVariable(String varName, GameVariable variable, @Nullable Object defaultValue) {
		if (defaultValue != null) {
			if (defaultValue instanceof List && variable instanceof GameVariableList) {
				((GameVariableList) variable).setItems((List<String>) defaultValue, "");
			} else if ((defaultValue instanceof String) || (defaultValue instanceof Integer)) {
				variable.setItem(defaultValue.toString(), "");
			} else {
				throw new Error("Error on defaultValue setup for variable " + varName + " on game " + id);
			}
		}
		verificationMap.put(varName.toLowerCase(), variable);
	}

	public void setVarFromValue(String varName, String arenaName, String value) {
		if (getGamesVariable(varName) == null) return;
		getGamesVariable(varName).setItem(value, arenaName);
	}


	public void setVarFromValue(String varName, String arenaName, List<String> value) {
		GameVariable variable = getGamesVariable(varName);
		if (!(variable instanceof GameVariableList)) {
			throw new Error("Tried to set non-list var " + varName + " to list of strings");
		}
		((GameVariableList) variable).setItems(value, arenaName);
	}

	public Set<String> getVariables() {
		return this.verificationMap.keySet();
	}

	public void setDefaultQueueMinMax(int min, int max) {
		this.setVarFromValue("queue-min", "", Integer.toString(min));
		this.setVarFromValue("queue-max", "", Integer.toString(max));
	}
}
