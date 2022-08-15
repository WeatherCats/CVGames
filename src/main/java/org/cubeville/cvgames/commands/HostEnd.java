package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.PlayerManager;
import org.cubeville.cvgames.models.Arena;

import java.util.List;

public class HostEnd extends RunnableCommand {
    @Override
    public TextComponent execute(CommandSender sender, List<Object> params) throws Error {
        Arena arena = (Arena) params.get(0);
        Player player = (Player) sender;
        arena.getQueue().clearHostedLobby();
        PlayerManager.removePlayer(player);
        player.teleport((Location) arena.getVariable("exit"));
        TextComponent result = new TextComponent("You are no longer hosting on the arena " + arena.getName());
        result.setColor(ChatColor.AQUA);
        return result;
    }
}
