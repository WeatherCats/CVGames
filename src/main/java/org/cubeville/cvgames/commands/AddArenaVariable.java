package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.managers.EditingManager;
import org.cubeville.cvgames.models.Arena;
import org.cubeville.cvgames.models.BaseGame;
import org.cubeville.cvgames.vartypes.GameVariableObject;

import java.util.List;

public class AddArenaVariable extends RunnableCommand {

    @Override
    public TextComponent execute(CommandSender sender, List<Object> baseParameters)
        throws Error {
        if (!(sender instanceof Player)) throw new Error("You cannot run this command from console!");
        Player player = (Player) sender;
        Arena arena = (Arena) baseParameters.get(0);
        String variable = ((String) baseParameters.get(1)).toLowerCase();

        String input = null;
        if (baseParameters.size() > 2) input = (String) baseParameters.get(2);

        GameVariableObject gameVariableObject = EditingManager.getEditObject(arena, player);
        if (gameVariableObject != null) {
            gameVariableObject.addToField(arena.getName(), variable, player, input);
        } else {
            if (!arena.hasVariable(variable)) throw new Error("That variable does not exist for the arena " + arena.getName());
            arena.addGameVariable(variable, player, input);
        }
        return new TextComponent("§bSuccessfully added item to variable " + variable + " for " + arena.getName());
    }
}
