package org.cubeville.cvgames.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class CenterPosition extends RunnableCommand {

	@Override
	public String execute(Player player, List<Object> parameters)
		throws Error {
		Location newLoc = player.getLocation();
		newLoc.setX(Math.round(newLoc.getX() * 2.0) / 2.0);
		newLoc.setZ(Math.round(newLoc.getZ() * 2.0) / 2.0);
		newLoc.setPitch((float)(Math.round(newLoc.getPitch() / 45.0) * 45.0));
		newLoc.setYaw((float)(Math.round(newLoc.getYaw() / 45.0) * 45.0));
		player.teleport(newLoc);
		return "&eCentered your position in the block";
	}
}
