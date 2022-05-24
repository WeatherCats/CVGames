package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public abstract class RunnableCommand {

    public abstract TextComponent execute(CommandSender sender, List<Object> params) throws Error;
}
