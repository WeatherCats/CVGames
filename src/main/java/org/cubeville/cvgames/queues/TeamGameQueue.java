package org.cubeville.cvgames.queues;

import org.bukkit.entity.Player;

import java.util.*;

public class TeamGameQueue extends GameQueueOld {

	private int numberOfTeams;
	private QueueableTeamGame game;

	public TeamGameQueue(QueueableTeamGame game, int playerCap, int numberOfTeams) {
		super(game, playerCap);
		this.game = game;
		this.numberOfTeams = numberOfTeams;
	}

	@Override
	void setupGame(Set<Player> players) {
		// create teams
		List<Player> playerList = new ArrayList<>(players);
		Collections.shuffle(playerList);

		List<Set<Player>> teams = new ArrayList<>();

		// add team as new set
		for (int i = 0; i < numberOfTeams; i++) {
			teams.add(new HashSet<>());
		}

		// add player to each team
		for (int i = 0; i < playerList.size(); i++) {
			int teamIndex = i % numberOfTeams;
			Set<Player> team = teams.get(teamIndex);
			team.add(playerList.get(i));
			teams.set(teamIndex, team);
		}

		game.startGame(teams);
	}

}
