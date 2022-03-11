package org.cubeville.cvgames.vartypes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.cvgames.GameUtils;
import org.cubeville.cvgames.SignManager;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GameVariableQueueSign extends GameVariableSign {

	@Override
	public void setItem(Player player, String input, String arenaName)
		throws CommandExecutionException {
		super.setItem(player, input, arenaName);
		SignManager.addSign(sign, arenaName);
	}

	@Override
	public void setItem(@Nullable String string, String arenaName) {
		super.setItem(string, arenaName);
		SignManager.addSign(sign, arenaName);
	}
}
