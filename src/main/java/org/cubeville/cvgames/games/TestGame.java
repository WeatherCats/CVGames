package org.cubeville.cvgames.games;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.Game;
import org.cubeville.cvgames.GameUtils;
import org.cubeville.cvgames.vartypes.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestGame extends Game {

	public TestGame(String id) {
		super(id);
		addGamesVariable("flag", new GameVariableBlock());
		addGamesVariable("red-tp", new GameVariableLocation());
		addGamesVariable("blue-tp", new GameVariableLocation());
		setDefaultQueueMinMax(1, 2);
	}

	@Override
	public void onPlayerLogout(Player p) {

	}

	@Override
	public void onGameStart(List<Player> players) {
		Map<String, List<Player>> teamsMap = GameUtils.divideTeams(players, List.of("red", "blue"), Arrays.asList(.5F, .5F));
		for (String key : teamsMap.keySet()) {
			System.out.println(key + " team -----");
			if (teamsMap.get(key) == null) continue;
			GameUtils.messagePlayerList(teamsMap.get(key),"§aStarted the game on " + key);
			for (Player player : teamsMap.get(key)) {
				System.out.println(player.getName());
				player.teleport((Location) getVariable(key + "-tp"));
			}
		}
	}

	@Override
	public void onGameFinish(List<Player> list) {

	}
}
