package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cubeville.cvgames.CVGames;

import java.util.List;

// I hate that I have to do this
public class GiveItem extends RunnableCommand {

    @Override
    public TextComponent execute(Player player, List<Object> parameters) throws Error {
        String path = (String) parameters.get(0);
        ItemStack item =  CVGames.getInstance().getConfig().getItemStack(path);
        if (item == null) throw new Error("Can't get this item due to developer laziness, wait for server reboot or do /cvgames giveitem <path>");
        player.getInventory().addItem(item);
        return new TextComponent("§bItem Get!");
    }
}
