package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cubeville.cvgames.CVGames;

import java.util.List;

// I hate that I have to do this
public class GiveItem extends RunnableCommand {

    @Override
    public TextComponent execute(CommandSender sender, List<Object> parameters) throws Error {
        if (!(sender instanceof Player)) throw new Error("You cannot run this command from console!");
        Player player = (Player) sender;
        String path = (String) parameters.get(0);
        ItemStack item =  CVGames.getInstance().getConfig().getItemStack(path);
        if (item == null) throw new Error("Could not find an item at the path " + path);
        player.getInventory().addItem(item);
        return new TextComponent("§bItem Get!");
    }
}
