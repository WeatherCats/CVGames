package org.cubeville.cvgames.types;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.cubeville.commons.commands.CommandExecutionException;

public class GameVariableMaterial extends GameVariable {

	private Material blockMaterial;

	@Override
	public void setItem(Player player, String input) throws CommandExecutionException {
		Material mat;
		if (input != null) {
			mat = Material.valueOf(input.toUpperCase());
		} else {
			mat = player.getInventory().getItemInMainHand().getType();
		}
		if (!mat.isBlock()) throw new CommandExecutionException(input + " is not a block.");
		blockMaterial = mat;
	}

	@Override
	public Material getItem() {
		return blockMaterial;
	}

	@Override
	public String storeFormat() {
		if (blockMaterial == null) {
			return null;
		}
		return blockMaterial.toString();
	}

	@Override
	public boolean isValid() {
		return blockMaterial != null;
	}

}
