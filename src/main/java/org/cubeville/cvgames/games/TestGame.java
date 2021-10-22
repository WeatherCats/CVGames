package org.cubeville.cvgames.games;

import org.bukkit.entity.Player;
import org.cubeville.cvgames.Game;
import org.cubeville.cvgames.GameUtils;
import org.cubeville.cvgames.types.*;

import java.util.List;
import java.util.Map;

public class TestGame extends Game {

	public TestGame() {
		super("test", 2, 4);
	}

	@Override
	protected void onGameStart(List startList) {
		if (startList.isEmpty() || !(startList.get(0) instanceof Player)) {
			throw new Error("Game did not start properly due to players list being incorrect");
		}
		List<Player> players = (List<Player>) startList;
		Map<String, List<Player>> teamsMap = GameUtils.divideTeams(players, List.of("team1", "team2"));
		GameUtils.messagePlayerList(teamsMap.get("team1"),"§aStarted the game on team 1");
		GameUtils.messagePlayerList(teamsMap.get("team2"),"§aStarted the game on team 2");
	}

	@Override
	protected void onGameFinish(List list) {

	}

	@Override
	protected Object[][] verificationMap() {
		return new Object[][] {
			{"flag", new GameVariableBlockLocation()},
			{"lobby", new GameVariablePlayerLocation()},
			{"blocks", new GameVariableList<>(GameVariableMaterial.class) }
		};
	}
}
