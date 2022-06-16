package org.cubeville.cvgames.models;

import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.SignManager;
import org.cubeville.cvgames.enums.ArenaStatus;
import org.cubeville.cvgames.utils.GameUtils;
import org.cubeville.cvgames.vartypes.GameVariable;
import org.cubeville.cvgames.vartypes.GameVariableList;
import org.cubeville.cvgames.vartypes.GameVariableObject;
import org.cubeville.cvgames.vartypes.GameVariableTeam;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Arena {

	private final Map<String, GameVariable> verificationMap = new HashMap<>();
	private final Map<String, Map<String, GameVariable>> objectFields = new HashMap<>();
	private final String name;
	private final HashMap<String, BaseGame> games = new HashMap<>();
	private String usingGame;
	private final GameQueue queue;
	private ArenaStatus status;


	public Arena(String name) {
		this.name = name;
		this.queue = new GameQueue(this);
		this.status = ArenaStatus.OPEN;
	}

	public void addGame(BaseGame game) {
		queue.setGameQueueVariables(game);
		games.put(game.getId(), game);
	}

	public void removeGameWithName(String name) {
		games.remove(name);
	}

	public BaseGame getGame(String name) {
		return games.get(name);
	}

	public Set<String> getGameNames() { return games.keySet(); }

	public BaseGame getFirstGame() {
		if (games.values().stream().findFirst().isPresent()) {
			return games.values().stream().findFirst().get();
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public void setStatus(ArenaStatus status) {
		SignManager.updateArenaSignsStatus(getName(), status);
		this.status = status;
	}

	public ArenaStatus getStatus() {
		return status;
	}

	public GameQueue getQueue() {
		return queue;
	}

	public void playerLogoutCleanup(Player p) {
		queue.whenPlayerLogout(p, this);
		if (status.equals(ArenaStatus.IN_USE)) {
			games.get(usingGame).whenPlayerLogout(p, this);
		}
	}

	public void setUsingGame(String usingGame) {
		this.usingGame = usingGame;
	}

	public String teamSelectorInventoryName() {
		return GameUtils.teamSelectorPrefix + name;
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

	public void setGameVariable(String var, Player player, String input) {
		getGameVariable(var).setVariable(this.getName(), var, player, input);
	}

	public void addGameVariable(String var, Player player, String input) {
		getGameVariable(var).setVariable(this.getName(), var, player, input);
	}

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
		if (verificationMap.containsKey(varName)) return;
		if (defaultValue != null) {
			if (defaultValue instanceof List && variable instanceof GameVariableList) {
				((GameVariableList) variable).setItems((List<Object>) defaultValue, "");
			} else {
				variable.setItem(defaultValue, "");
			}
		}
		verificationMap.put(varName.toLowerCase(), variable);
	}

	public void addGameVariableObjectList(String varName, HashMap<String, GameVariable> fields) {
		addGameVariable(varName, new GameVariableList<>(GameVariableObject.class));
		saveObjectFields(varName, fields);
	}

	public void addGameVariableTeamsList(HashMap<String, GameVariable> fields) {
		addGameVariable("teams", new GameVariableList<>(GameVariableTeam.class));
		saveObjectFields("teams", fields);
	}

	private void saveObjectFields(String varName, HashMap<String, GameVariable> fields) {
		// merge the object fields together
		if (objectFields.containsKey(varName)) {
			Stream<Map.Entry<String, GameVariable>> combinedFieldNames = Stream.concat(objectFields.get(varName).entrySet().stream(), fields.entrySet().stream());
			objectFields.put(varName, combinedFieldNames.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
		} else {
			objectFields.put(varName, fields);
		}
	}

	public Map<String, GameVariable> getObjectFields(String varName) {
		return objectFields.get(varName);
	}

	public Set<String> getVariables() {
		return verificationMap.keySet();
	}
}
