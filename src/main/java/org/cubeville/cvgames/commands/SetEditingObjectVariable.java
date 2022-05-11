package org.cubeville.cvgames.commands;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.*;
import org.cubeville.cvgames.managers.ArenaManager;
import org.cubeville.cvgames.managers.EditingManager;
import org.cubeville.cvgames.models.Game;
import org.cubeville.cvgames.vartypes.GameVariable;
import org.cubeville.cvgames.vartypes.GameVariableList;
import org.cubeville.cvgames.vartypes.GameVariableObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SetEditingObjectVariable extends Command {

    // Sets
    public SetEditingObjectVariable() {
        super("setedit");
        addBaseParameter(new CommandParameterString()); // arena name
        addBaseParameter(new CommandParameterString()); // list var name
        addBaseParameter(new CommandParameterInteger()); // list int
        setPermission("cvgames.arenas.setedit");
    }

    @Override
    public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
            throws CommandExecutionException {
        String arenaName = ArenaManager.filterArenaInput((String) baseParameters.get(0));
        Game arenaGame = ArenaManager.getArena(arenaName).getGame();
        if (arenaGame == null) throw new CommandExecutionException("You need to set the game for the arena " + arenaName);
        String variable = ((String) baseParameters.get(1)).toLowerCase();
        if (!arenaGame.hasVariable(variable)) throw new CommandExecutionException("That variable does not exist for the game " + arenaGame.getId());
        GameVariable gameVariable = arenaGame.getGameVariable(variable);
        if (!(gameVariable instanceof GameVariableList)) throw new CommandExecutionException("The variable " + variable +" is not a list");
        int index = (int) baseParameters.get(2);
        GameVariable editingVar = ((GameVariableList<?>) gameVariable).getVariableAtIndex(index - 1);
        if (editingVar == null) throw new CommandExecutionException("The list " + variable +" does not have an index of " + index);
        if (!(editingVar instanceof GameVariableObject)) throw new CommandExecutionException("The list " + variable + " is not a list of objects");

        String path = variable + "." + (index - 1);
        EditingManager.setEditObject(ArenaManager.getArena(arenaName), player, (GameVariableObject) editingVar, path);
        return new CommandResponse("&aEditing object number " + index + " in list " + variable + " for arena " + arenaName);
    }

}
