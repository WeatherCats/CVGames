package org.cubeville.cvgames;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandParameterEnumeratedString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvgames.arenas.ArenaManager;
import org.cubeville.cvgames.queues.FFAGameQueue;
import org.cubeville.cvgames.queues.QueueableFFAGame;

import java.util.*;

public class Paintball extends Command implements Listener, QueueableFFAGame {

	private ArenaManager am;

	Paintball(JavaPlugin plugin) {
		super("paintball queues");
		addBaseParameter(new CommandParameterEnumeratedString("join", "leave"));
		setPermission("cvgames.paintball");
		am = new ArenaManager(plugin, "paintball");
	}

	private TreeMap<UUID, Integer> paintball = new TreeMap<>();
	private FFAGameQueue queue = new FFAGameQueue(this, 2);

	private ItemStack createColoredLeatherArmor(Material armorType, Color color) {
		ItemStack armorItem = new ItemStack(armorType);
		if (armorItem.getItemMeta() instanceof LeatherArmorMeta) {
			LeatherArmorMeta meta = (LeatherArmorMeta) armorItem.getItemMeta();
			meta.setColor(color);
			armorItem.setItemMeta(meta);
		}
		return armorItem;
	}

	@Override
	public void startGame(Set<Player> players) {
		Map<String, List<Player>> teamMap = new HashMap<>();
		teamMap.put("ffa", new ArrayList<>(players));
		am.startTeleport(teamMap);
		for (Player player : players) {
			PlayerInventory inv = player.getInventory();

			inv.setHelmet(createColoredLeatherArmor(Material.LEATHER_HELMET, Color.BLUE));
			inv.setChestplate(createColoredLeatherArmor(Material.LEATHER_CHESTPLATE, Color.BLUE));
			inv.setLeggings(createColoredLeatherArmor(Material.LEATHER_LEGGINGS, Color.BLUE));
			inv.setBoots(createColoredLeatherArmor(Material.LEATHER_BOOTS, Color.BLUE));

			inv.addItem(new ItemStack(Material.SNOWBALL, 128));

			paintball.put(player.getUniqueId(), 4);

			player.sendMessage("§bPaintball has been started!");
		}
	}

	@Override
	public boolean canStartQueue() {
		return paintball.isEmpty();
	}

	@Override
	public CommandResponse execute(Player p, Set<String> flags, Map<String, Object> parameters,
		List<Object> baseParameters) {
		return queue.execute(p, baseParameters);
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {

		if (event.getEntityType().equals(EntityType.SNOWBALL) && event.getHitEntity() instanceof Player && event.getEntity().getShooter() instanceof Player) {
			Snowball s = (Snowball) event.getEntity();
			Player hit = (Player) event.getHitEntity();
			Player attacker = (Player) s.getShooter();
			UUID uuid = hit.getUniqueId();

			// if both the player being shot and the shooter are in paintball
			if (paintball.containsKey(uuid) && paintball.containsKey(attacker.getUniqueId())) {

				// if the player isn't shooting themselves
				if (!uuid.equals(attacker.getUniqueId())) {

					hit.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, hit.getLocation(), 30, .1, .1, .1, .1);

					PlayerInventory inv = hit.getInventory();

					// subtract 1 from player health
					paintball.put(uuid, paintball.get(uuid) - 1);

					attacker.sendMessage("§aYou have hit " + hit.getName() + "!");
					hit.sendMessage("§cYou have been hit by " + attacker.getName() + "!");

					switch (paintball.get(uuid)) {
						case 3:
							inv.setHelmet(createColoredLeatherArmor(Material.LEATHER_HELMET, Color.RED));
							break;
						case 2:
							inv.setChestplate(createColoredLeatherArmor(Material.LEATHER_CHESTPLATE, Color.RED));
							break;
						case 1:
							inv.setLeggings(createColoredLeatherArmor(Material.LEATHER_LEGGINGS, Color.RED));
							break;
						default:
							// remove players armor, they dead!
							inv.setArmorContents(null);
							paintball.remove(uuid);
							hit.sendMessage("§4§lYou have been eliminated!");

							// if there is 1 player left
							if (paintball.size() <= 1) {
								// they win!
								Player winner = Bukkit.getPlayer(paintball.firstKey());
								if (winner != null) {
									winner.sendMessage("§a§lYou have won!");
									winner.getInventory().setArmorContents(null);
								}
								paintball.clear();
							}
							break;
					}
				}
			}
		}
	}

}
