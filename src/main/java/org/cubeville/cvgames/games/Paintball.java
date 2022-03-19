package org.cubeville.cvgames.games;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.cubeville.cvgames.Game;
import org.cubeville.cvgames.GameUtils;
import org.cubeville.cvgames.vartypes.GameVariableChatColor;
import org.cubeville.cvgames.vartypes.GameVariableList;
import org.cubeville.cvgames.vartypes.GameVariableLocation;
import org.cubeville.cvgames.vartypes.GameVariableString;

import java.util.*;

public class Paintball extends Game {

	private final HashMap<Player, PaintballState> state = new HashMap<>();

	public Paintball(String id) {
		super(id);
		addGamesVariable("spectate-lobby", new GameVariableLocation());
		addGamesVariable("team1-tps", new GameVariableList<>(GameVariableLocation.class));
		addGamesVariable("team1-name", new GameVariableString(), "Red Team");
		addGamesVariable("team1-chat-color", new GameVariableChatColor(), String.valueOf(ChatColor.RED.getChar()));
		addGamesVariable("team1-armor-color", new GameVariableString(), "#FF0000");
		addGamesVariable("team1-armor-color-damaged", new GameVariableString(), "#440000");

		addGamesVariable("team2-tps", new GameVariableList<>(GameVariableLocation.class));
		addGamesVariable("team2-name", new GameVariableString(), "Blue Team");
		addGamesVariable("team2-chat-color", new GameVariableChatColor(), String.valueOf(ChatColor.BLUE.getChar()));
		addGamesVariable("team2-armor-color", new GameVariableString(), "#0000FF");
		addGamesVariable("team2-armor-color-damaged", new GameVariableString(), "#000044");
		setDefaultQueueMinMax(2, 4);

	}

	private ItemStack createColoredLeatherArmor(Material armorType, Color color) {
		ItemStack armorItem = new ItemStack(armorType);
		if (armorItem.getItemMeta() instanceof LeatherArmorMeta) {
			LeatherArmorMeta meta = (LeatherArmorMeta) armorItem.getItemMeta();
			meta.setColor(color);
			armorItem.setItemMeta(meta);
		}
		return armorItem;
	}

	private Color hex2Color(String colorStr) {
		return Color.fromRGB(
				Integer.valueOf(colorStr.substring(1, 3), 16),
				Integer.valueOf(colorStr.substring(3, 5), 16),
				Integer.valueOf(colorStr.substring(5, 7), 16)
		);
	}

	@Override
	public void onGameStart(List<Player> players) {
		Map<String, List<Player>> teamsMap = GameUtils.divideTeams(players, List.of("team1", "team2"), Arrays.asList(.5F, .5F));
		for (String key : teamsMap.keySet()) {
			if (teamsMap.get(key) == null) continue;

			List<Player> teamPlayers = teamsMap.get(key);
			Color teamColor = hex2Color((String) getVariable(key + "-armor-color"));
			String teamName = (String) getVariable(key + "-name");
			ChatColor chatColor = (ChatColor) getVariable(key + "-chat-color");
			List<Location> tps = (List<Location>) getVariable(key + "-tps");

			int i = 0;
			for (Player player : teamPlayers) {
				state.put(player, new PaintballState(key));
				PlayerInventory inv = player.getInventory();
				inv.setHelmet(createColoredLeatherArmor(Material.LEATHER_HELMET, teamColor));
				inv.setChestplate(createColoredLeatherArmor(Material.LEATHER_CHESTPLATE, teamColor));
				inv.setLeggings(createColoredLeatherArmor(Material.LEATHER_LEGGINGS, teamColor));
				inv.setBoots(createColoredLeatherArmor(Material.LEATHER_BOOTS, teamColor));

				inv.addItem(new ItemStack(Material.SNOWBALL, 128));

				Location tpLoc = tps.get(i);
				if (!tpLoc.getChunk().isLoaded()) {
					tpLoc.getChunk().load();
				}
				player.teleport(tpLoc);
				player.sendMessage(chatColor + "You are on §l" + teamName + chatColor + "!");
				i++;
			}
		}
	}

	private Set<String> remainingTeams() {
		Set<String> stillInGame = new HashSet<>();
		for (PaintballState ps : state.values()) {
			if (ps.health == 0 || stillInGame.contains(ps.team)) { continue; }
			stillInGame.add(ps.team);
		}
		return stillInGame;
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

			//hit.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, hit.getLocation(), 30, .1, .1, .1, .1);
			PlayerInventory inv = hit.getInventory();

			hitState.health -= 1;

			attacker.sendMessage("§aYou have hit " + hit.getName() + "!");
			attacker.playSound(attacker.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, );
			hit.sendMessage("§cYou have been hit by " + attacker.getName() + "!");

			Color damagedColor = hex2Color((String) getVariable(hitState.team + "-armor-color-damaged"));

			switch (hitState.health) {
				case 3:
					inv.setHelmet(createColoredLeatherArmor(Material.LEATHER_HELMET, damagedColor));
					break;
				case 2:
					inv.setChestplate(createColoredLeatherArmor(Material.LEATHER_CHESTPLATE, damagedColor));
					break;
				case 1:
					inv.setLeggings(createColoredLeatherArmor(Material.LEATHER_LEGGINGS, damagedColor));
					break;
				default:
					// remove players armor, they dead!
					inv.setArmorContents(null);
					if (remainingTeams().size() <= 1) {
						finishGame(new ArrayList<>(state.keySet()));
					} else {
						hit.sendMessage("§4§lYou have been eliminated!");
						hit.teleport((Location) getVariable("spectate-lobby"));
					}
					break;
			}
		}
	}

	@Override
	public void onPlayerLogout(Player p) {
		state.remove(p);
		if (remainingTeams().size() <= 1) {
			finishGame(new ArrayList<>(state.keySet()));
		}
	}

	@Override
	public void onGameFinish(List<Player> players) {
		state.clear();
		String remainingTeam = "";
		for (String rt : remainingTeams()) { remainingTeam = rt; }
		if (remainingTeam.isEmpty()) { return; }
		String teamName = (String) getVariable(remainingTeam + "-name");
		ChatColor chatColor = (ChatColor) getVariable(remainingTeam + "-chat-color");
		GameUtils.messagePlayerList(players, chatColor + "§l" + teamName + " won the game!");
	}
}

class PaintballState {

	int health = 4;
	String team;

	 public PaintballState(String team) {
		 this.team = team;
	 }
}
