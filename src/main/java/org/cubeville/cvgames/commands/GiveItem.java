package org.cubeville.cvgames.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cubeville.cvgames.CVGames;

import java.util.List;

// I hate that I have to do this
public class GiveItem extends RunnableCommand {

    @Override
    public String execute(Player player, List<Object> parameters) throws Error {
        String path = (String) parameters.get(0);
        ItemStack item =  CVGames.getInstance().getConfig().getItemStack(path);
        if (item == null) throw new Error("Could not find ItemStack here!");
        player.getInventory().addItem(item);
        return "&bItem Get!";
    }
}
