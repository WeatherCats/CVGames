package org.cubeville.cvgames.database;

import org.bukkit.entity.Player;
import org.cubeville.cvgames.CVGames;
import org.cubeville.cvgames.models.Arena;
import org.cubeville.cvgames.models.BaseGame;

import java.sql.ResultSet;

public class GamesDB extends SQLite {

	public String SQLiteCreateMetricsTable = "CREATE TABLE IF NOT EXISTS metrics (" +
			"`metric_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
			"`name` varchar(32) NOT NULL," +
			"`arena` varchar(32) NOT NULL," +
			"`game` varchar(32) NOT NULL," +
			"`value` INTEGER NOT NULL" +
			");";

	public String SQLiteCreateFlagsTable = "CREATE TABLE IF NOT EXISTS flags (" +
			"`flag_name` varchar(32) NOT NULL," +
			"`uuid` varchar(32) NOT NULL," +
			"PRIMARY KEY (`flag_name`, `uuid`)" +
			");";

	public String SQLiteCreateLeaderboardsTable = "CREATE TABLE IF NOT EXISTS leaderboards (" +
			"`leaderboard_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
			"`leaderboard_display_name` varchar(32) NOT NULL," +
			");";

	public String SQLiteCreateScoresTable = "CREATE TABLE IF NOT EXISTS scores (" +
			"`score_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
			"`uuid` varchar(32) NOT NULL," +
			"`value` BIGINT NOT NULL," +
			"`timestamp` BIGINT NOT NULL," +
			"FOREIGN KEY (`leaderboard_id`) REFERENCES scores(`leaderboard_id`) ON DELETE CASCADE" +
			");";

	public GamesDB() {
		super(CVGames.getInstance(), "games-data");
	}

	public void load() {
		connect();
		// TODO -- add leaderboards, also figure out how to display them
		//update(SQLiteCreateLeaderboardsTable);
		//update(SQLiteCreateScoresTable);
		update(SQLiteCreateMetricsTable);
		update(SQLiteCreateFlagsTable);
	}

	public ResultSet getAllFlags() {
		return getResult("SELECT * FROM `flags`");
	}

	public ResultSet getAllMetrics() {
		return getResult("SELECT * FROM `metrics`");
	}

	public void addFlag(Player player, String flagName) {
		update("INSERT INTO IGNORE flags (`uuid`, `name`)" +
				"VALUES(\"" + player.getUniqueId() + "\", \"" + flagName + "\")"
		);
	}

	public void removeFlag(Player player, String flagName) {
		update("DELETE IGNORE FROM flags where `uuid` = \"" + player.getUniqueId() + "\"" +
				" AND `name` = \"" + flagName + "\""
		);
	}

	public void addMetric(BaseGame game, String metricName) {
		update("INSERT INTO metrics (`arena`, `game`, `name`, `value`)" +
				"VALUES(\"" + game.getArena().getName() + "\", \"" + game.getId() + "\", \"" + metricName + "\", " + 1 + ")"
		);
	}

	public void setMetric(BaseGame game, String metricName, long value) {
		update("UPDATE `metrics` SET `value` = " + value +
				" WHERE `arena` = \"" + game.getArena().getName() + "\" AND" +
				" `game` = \"" + game.getId() + "\" AND" +
				" `name` = \"" + metricName + "\""
		);
	}
}
