package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.managers.SignManager;
import org.cubeville.cvgames.models.Arena;

import java.util.List;

public class QueueJoin extends RunnableCommand {

    @Override
    public TextComponent execute(CommandSender sender, List<Object> params) throws Error {
        Arena arena = ArenaManager.getArena((String) params.get(0));
        Player playerToAdd = (Player) params.get(1);
        if (!arena.getQueue().join(playerToAdd)) {
            playerToAdd.teleport((Location) arena.getGame().getVariable("exit"));
        } else {
            SignManager.updateArenaSignsFill(arena.getName());
        }
        return null;
    }
}
