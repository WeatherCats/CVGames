package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.PlayerManager;
import org.cubeville.cvgames.models.Arena;

import java.util.List;

public class HostTeamsRemove extends RunnableCommand {
    @Override
    public TextComponent execute(CommandSender sender, List<Object> params) throws Error {
        Arena arena = PlayerManager.getPlayerArena((Player) sender);
        String playerName = (String) params.get(0);
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) throw new Error("Player with name " + playerName + " is not online!");
        arena.getQueue().removePlayerFromTeams(player);
        return null;
    }
}
