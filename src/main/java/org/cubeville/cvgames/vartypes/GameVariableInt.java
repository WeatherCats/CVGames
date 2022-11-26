package org.cubeville.cvgames.vartypes;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import static org.cubeville.cvgames.CVGames.getInstance;

public class GameVariableInt extends GameVariable {

	private Integer number;

	public GameVariableInt() {}

	public GameVariableInt(String description) {
		super(description);
	}

	@Override
	public void setItem(Player player, String input, String arenaName) throws Error {
		number = Integer.valueOf(input);
	}

	@Override
	public void setItem(@Nullable Object num, String arenaName) {
		if (!(num instanceof Integer)) number = null;
		else number = (Integer) num;
	}

	@Override
	public Integer getFromPath(String path) {
		return getInstance().getConfig().getInt(path);
	}

	@Override
	public String typeString() {
		return "Integer";
	}

	@Override
	public Integer getItem() {
		return number;
	}

	@Override
	public Integer itemString() { return number == null ? null : number; }

	@Override
	public boolean isValid() {
		return number != null;
	}
}
