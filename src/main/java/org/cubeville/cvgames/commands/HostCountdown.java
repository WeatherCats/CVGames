package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.PlayerManager;
import org.cubeville.cvgames.models.Arena;

import java.util.List;

public class HostCountdown extends RunnableCommand {
    @Override
    public TextComponent execute(CommandSender sender, List<Object> params) throws Error {
        Arena arena = PlayerManager.getPlayerArena((Player) sender);
        int index = 5;
        if (params.size() > 0) {
            String indexString = (String) params.get(0);
            try {
                index = Integer.parseInt(indexString);
            } catch (NumberFormatException e) {
                throw new Error(indexString + " is not a valid number.");
            }
        }
        arena.getQueue().startHostedCountdown(index);
        return null;
    }
}
