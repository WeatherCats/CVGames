package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.cubeville.cvgames.CVGames;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.models.Arena;

import java.util.List;

public class RemoveArenaGame extends RunnableCommand {
    @Override
    public TextComponent execute(CommandSender sender, List<Object> params) throws Error {
        Arena arena = (Arena) params.get(0);
        String gameName = (String) params.get(1);
        ArenaManager.removeArenaGame(arena.getName(), gameName);

        return new TextComponent("§aRemoved the game " + gameName + " from arena " + arena.getName() + "!");
    }
}
