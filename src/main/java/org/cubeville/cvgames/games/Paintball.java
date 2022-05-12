package org.cubeville.cvgames.games;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.PlayerInventory;
import org.cubeville.cvgames.CVGames;
import org.cubeville.cvgames.models.Game;
import org.cubeville.cvgames.utils.GameUtils;
import org.cubeville.cvgames.models.GameRegion;
import org.cubeville.cvgames.vartypes.*;

import java.util.*;

public class Paintball extends Game {

	int rechargeZoneChecker;
	private final HashMap<Player, PaintballState> state = new HashMap<>();
	private List<HashMap<String, Object>> teams;

	public Paintball(String id) {
		super(id);
		addGameVariable("spectate-lobby", new GameVariableLocation());
		addGameVariable("ammo", new GameVariableInt(), 16);
		addGameVariable("recharge-zones", new GameVariableList<>(GameVariableRegion.class));
		addGameVariable("recharge-cooldown", new GameVariableInt(), 15);
		addGameVariable("teams", new GameVariableList<>(PaintballTeam.class));
		setDefaultQueueMinMax(2, 4);

	}

	@Override
	public void onGameStart(List<Player> players) {
		teams = (List<HashMap<String, Object>>) getVariable("teams");
		List<Float> percentages = new ArrayList<>();
		List<String> teamKeys = new ArrayList<>();
		for (int i = 0; i < teams.size(); i++) {
			teamKeys.add(Integer.toString(i));
			percentages.add(1.0F / ((float) teams.size()));
		}

		Map<String, List<Player>> teamsMap = GameUtils.divideTeams(players, teamKeys, percentages);

		for (int i = 0; i < teams.size(); i++) {
			HashMap<String, Object> team = teams.get(i);
			List<Player> teamPlayers = teamsMap.get(Integer.toString(i));

			String teamName = (String) team.get("name");
			ChatColor chatColor = (ChatColor) team.get("chat-color");
			List<Location> tps = (List<Location>) team.get("tps");

			int j = 0;
			for (Player player : teamPlayers) {
				state.put(player, new PaintballState(i));

				resetInventory(player);
				Location tpLoc = tps.get(j);
				if (!tpLoc.getChunk().isLoaded()) {
					tpLoc.getChunk().load();
				}
				player.teleport(tpLoc);
				player.sendMessage(chatColor + "You are on §l" + teamName + chatColor + "!");
				j++;
			}
		}

		List<GameRegion> rechargeZones = (List<GameRegion>) getVariable("recharge-zones");
		int cooldown = (int) getVariable("recharge-cooldown");
		rechargeZoneChecker = Bukkit.getScheduler().scheduleSyncRepeatingTask(CVGames.getInstance(), () -> {
			for (GameRegion rechargeZone : rechargeZones) {
				for (Player player : state.keySet()) {
					if (rechargeZone.containsPlayer(player)) {
						Long lastRecharge = state.get(player).lastRecharge;
						if (lastRecharge == null || System.currentTimeMillis() - lastRecharge > (cooldown * 1000L)) {
							state.get(player).lastRecharge = System.currentTimeMillis();
							player.sendMessage("§b§lAmmo Recharged! §f§o(Cooldown: " + cooldown + " seconds)");
							resetInventory(player);
						}
					}
				}
			}
		}, 0L, 2L);
	}

	private Set<Integer> remainingTeams() {
		Set<Integer> stillInGame = new HashSet<>();
		for (PaintballState ps : state.values()) {
			if (ps.health == 0 || stillInGame.contains(ps.team)) { continue; }
			stillInGame.add(ps.team);
		}
		return stillInGame;
	}

	private void resetInventory(Player player) {
		HashMap<String, Object> team = teams.get(state.get(player).team);

		Color healthyColor = GameUtils.hex2Color((String) team.get("armor-color"));
		Color damagedColor = GameUtils.hex2Color((String) team.get("armor-color-damaged"));

		int health = state.get(player).health;

		PlayerInventory inv = player.getInventory();
		inv.clear();
		inv.setHelmet(GameUtils.createColoredLeatherArmor(Material.LEATHER_HELMET, health >= 4 ? healthyColor : damagedColor));
		inv.setChestplate(GameUtils.createColoredLeatherArmor(Material.LEATHER_CHESTPLATE, health >= 3 ? healthyColor : damagedColor));
		inv.setLeggings(GameUtils.createColoredLeatherArmor(Material.LEATHER_LEGGINGS, health >= 2 ? healthyColor : damagedColor));
		inv.setBoots(GameUtils.createColoredLeatherArmor(Material.LEATHER_BOOTS, health >= 1 ? healthyColor : damagedColor));

		inv.addItem(GameUtils.customItem(
				Material.SNOWBALL,
				(String) team.get("snowball-name"),
				(int) getVariable("ammo")
		));
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {

		if (event.getEntityType().equals(EntityType.SNOWBALL) && event.getHitEntity() instanceof Player && event.getEntity().getShooter() instanceof Player) {
			Snowball s = (Snowball) event.getEntity();
			Player hit = (Player) event.getHitEntity();
			Player attacker = (Player) s.getShooter();

			PaintballState hitState = state.get(hit);
			PaintballState attackerState = state.get(attacker);

			// return if either player is not in the game
			if (hitState == null || attackerState == null) { return; }

			// if the player isn't shooting themselves
			if (hit.equals(attacker)) { return; }

			PlayerInventory inv = hit.getInventory();

			hitState.health -= 1;

			attacker.sendMessage("§aYou have hit " + hit.getName() + "!");
			attacker.playSound(attacker.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 0.7F);
			hit.sendMessage("§cYou have been hit by " + attacker.getName() + "!");

			Color damagedColor = GameUtils.hex2Color((String) teams.get(hitState.team).get("armor-color-damaged"));

			switch (hitState.health) {
				case 3:
					inv.setHelmet(GameUtils.createColoredLeatherArmor(Material.LEATHER_HELMET, damagedColor));
					break;
				case 2:
					inv.setChestplate(GameUtils.createColoredLeatherArmor(Material.LEATHER_CHESTPLATE, damagedColor));
					break;
				case 1:
					inv.setLeggings(GameUtils.createColoredLeatherArmor(Material.LEATHER_LEGGINGS, damagedColor));
					break;
				default:
					// remove players armor, they dead!
					inv.clear();
					if (!testGameEnd()) {
						hit.sendMessage("§4§lYou have been eliminated!");
						hit.teleport((Location) getVariable("spectate-lobby"));
					}
					break;
			}
		}
	}

	private boolean testGameEnd() {
		if (remainingTeams().size() <= 1) {
			finishGame(new ArrayList<>(state.keySet()));
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerLogout(Player p) {
		state.remove(p);
		testGameEnd();
	}

	@Override
	public void onGameFinish(List<Player> players) {
		Bukkit.getScheduler().cancelTask(rechargeZoneChecker);
		rechargeZoneChecker = 0;
		int remainingTeam = -1;
		for (int rt : remainingTeams()) { remainingTeam = rt; }
		if (remainingTeam < 0) { return; }

		String teamName = (String) teams.get(remainingTeam).get("name");
		ChatColor chatColor = (ChatColor) teams.get(remainingTeam).get("chat-color");
		GameUtils.messagePlayerList(players, chatColor + "§l" + teamName + " won the game!");
		state.clear();
	}
}

class PaintballState {

	int health = 4;
	int team;
	Long lastRecharge = null;

	 public PaintballState(int team) {
		 this.team = team;
	 }
}

