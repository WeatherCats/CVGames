package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.models.Arena;

import java.util.List;

public class HostStart extends RunnableCommand {
    @Override
    public TextComponent execute(CommandSender sender, List<Object> params) throws Error {
        Arena arena = (Arena) params.get(0);
        String gameName = (String) params.get(1);
        Player player = (Player) sender;
        arena.getQueue().setHostedLobby(player, gameName);
        TextComponent result = new TextComponent("You are now hosting a game of " + gameName + " on the arena " + arena.getName());
        result.setColor(ChatColor.AQUA);
        return result;
    }
}
