package org.cubeville.cvgames.vartypes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.cubeville.commons.commands.CommandExecutionException;

import javax.annotation.Nullable;

public class GameVariableMaterial extends GameVariable {

	private Material blockMaterial;

	@Override
	public void setItem(Player player, String input, String arenaName) throws CommandExecutionException {
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
	public String displayString() {
		return "Material";
	}

	@Override
	public void setItem(@Nullable String string, String arenaName) {
		if (string == null) {
			blockMaterial = null;
		} else {
			blockMaterial = Material.valueOf(string.toUpperCase());
		}
	}

	@Override
	public Material getItem() {
		return blockMaterial;
	}

	@Override
	public String itemString() {
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
