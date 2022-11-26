package org.cubeville.cvgames.vartypes;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import static org.cubeville.cvgames.CVGames.getInstance;

public class GameVariableMaterial extends GameVariable {

	private Material blockMaterial;

	public GameVariableMaterial() {}

	public GameVariableMaterial(String description) {
		super(description);
	}

	@Override
	public void setItem(Player player, String input, String arenaName) throws Error {
		Material mat;
		if (input != null) {
			mat = Material.valueOf(input.toUpperCase());
		} else {
			mat = player.getInventory().getItemInMainHand().getType();
		}
		if (!mat.isBlock()) throw new Error(input + " is not a block.");
		blockMaterial = mat;
	}

	@Override
	public String typeString() {
		return "Material";
	}

	@Override
	public void setItem(@Nullable Object object, String arenaName) {
		if (!(object instanceof String)) {
			blockMaterial = null;
		} else {
			blockMaterial = Material.valueOf(((String) object).toUpperCase());
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
