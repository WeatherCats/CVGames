package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.PlayerManager;
import org.cubeville.cvgames.models.Arena;

import java.util.List;

public class HostTeamsSet extends RunnableCommand {
    @Override
    public TextComponent execute(CommandSender sender, List<Object> params) throws Error {
        Arena arena = PlayerManager.getPlayerArena((Player) sender);
        String playerName = (String) params.get(0);
        Player playerAdding = Bukkit.getPlayer(playerName);
        if (playerAdding == null) throw new Error("Player with name " + playerName + " is not online!");
        String indexString = (String) params.get(1);
        int index;
        try
        {
            index = Integer.parseInt(indexString);
        }
        catch (NumberFormatException e)
        {
            throw new Error(indexString + " is not a valid number, please input the index of the team you want to assign " + playerAdding.getDisplayName() + " to.");
        }
        arena.getQueue().setPlayerAsTeamMember(playerAdding, index - 1);
        return null;
    }
}
