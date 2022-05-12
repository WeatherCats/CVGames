package org.cubeville.cvgames.commands;

import org.bukkit.entity.Player;
import java.util.List;

public abstract class RunnableCommand {

    public abstract String execute(Player player, List<Object> params) throws Error;
}
