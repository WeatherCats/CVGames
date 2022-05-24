package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CenterPosition extends RunnableCommand {

	@Override
	public TextComponent execute(CommandSender sender, List<Object> parameters)
		throws Error {
		if (!(sender instanceof Player)) throw new Error("You cannot run this command from console!");
		Player player = (Player) sender;
		Location newLoc = player.getLocation();
		newLoc.setX(Math.round(newLoc.getX() * 2.0) / 2.0);
		newLoc.setZ(Math.round(newLoc.getZ() * 2.0) / 2.0);
		newLoc.setPitch((float)(Math.round(newLoc.getPitch() / 45.0) * 45.0));
		newLoc.setYaw((float)(Math.round(newLoc.getYaw() / 45.0) * 45.0));
		player.teleport(newLoc);
		return new TextComponent("§eCentered your position in the block");
	}
}
