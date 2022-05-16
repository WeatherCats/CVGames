package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import java.util.List;

public abstract class RunnableCommand {

    public abstract TextComponent execute(Player player, List<Object> params) throws Error;
}
