package org.cubeville.cvgames.games;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.models.Game;
import org.cubeville.cvgames.GameUtils;
import org.cubeville.cvgames.vartypes.GameVariableInt;
import org.cubeville.cvgames.vartypes.GameVariableLocation;

import java.util.List;
import java.util.Map;

public class PVPLives extends Game {
	Map<Player, String> playerToTeam;
	Map<String, Integer> teamToLives;

	public PVPLives(String id) {
		super(id);
		setDefaultQueueMinMax(2, 4);
		addGameVariable("lives", new GameVariableInt(), 3);
		addGameVariable("spawn-red", new GameVariableLocation());
		addGameVariable("spawn-blue", new GameVariableLocation());
	}

	@Override
	public void onGameStart(List<Player> players) {
		Map<String, List<Player>> teamMap = GameUtils.divideTeams(players, List.of("red", "blue"));
		for ( String team : teamMap.keySet()) {
			teamToLives.put(team, (Integer) this.getVariable("lives"));
			for (Player player : teamMap.get(team)) {
				player.teleport((Location) this.getVariable("spawn-" + team));

			}
		}
	}

	@Override
	public void onGameFinish(List<Player> players) {

	}

	@Override
	public void onPlayerLogout(Player p) {

	}
}
