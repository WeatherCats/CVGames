package org.cubeville.cvgames;

import org.bukkit.entity.Player;
import org.cubeville.cvgames.types.GameVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract public class Game <GV extends GameVariable> implements PlayerContainer {
	private String id;
	private Map<Player, Object> playerState = new HashMap<>();
	private int queueMin;
	private int queueMax;
	private final Map<String, GV> verificationMap;

	public Game(String id, int queueMin, int queueMax) {
		this.id = id;
		this.queueMin = queueMin;
		this.queueMax = queueMax;
		this.verificationMap = Stream.of(verificationMap())
			.collect(Collectors.toMap(data -> (String) data[0], data -> (GV) data[1]));
	}

	public void setPlayerState(Player player, Object state) {
		playerState.put(player, state);
	}

	public void deletePlayerState(Player player) {
		playerState.remove(player);
	}

	@Override
	public void onPlayerLogout(Player p) {
		deletePlayerState(p);
	}

	public String getId() {
		return id;
	}

	public int getQueueMax() {
		return queueMax;
	}

	public int getQueueMin() {
		return queueMin;
	}

	public void startGame(List<Player> players, Arena arena) {
		//TODO -- STUFF FOR EVERY GAME START
		arena.setStatus(ArenaStatus.IN_USE);
		onGameStart(players);
	};

	public void finishGame(List<Player> players, Arena arena) {
		arena.setStatus(ArenaStatus.OPEN);
		players.forEach(PlayerLogoutManager::removePlayer);
		onGameFinish(players);
	};

	protected abstract void onGameStart(List<Player> players);

	protected abstract void onGameFinish(List<Player> players);

	protected abstract Object[][] verificationMap();

	public Map<String, GV> getVerificationMap() {
		return verificationMap;
	}

	public GV getGamesVariable(String var) {
		return verificationMap.get(var);
	}

	public Object getVariable(String var) {
		return getGamesVariable(var).getItem();
	}

	public boolean hasVariable(String var) {
		return verificationMap.containsKey(var);
	}
}
