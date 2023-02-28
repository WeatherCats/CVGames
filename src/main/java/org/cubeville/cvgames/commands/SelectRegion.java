package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cubeville.cvgames.CVGames;
import org.cubeville.cvgames.models.GameRegion;
import org.cubeville.cvgames.utils.BlockUtils;
import org.cubeville.cvgames.utils.GameUtils;

import java.util.List;

public class SelectRegion extends RunnableCommand {
    @Override
    public TextComponent execute(CommandSender sender, List<Object> params) throws Error {
        if (!(sender instanceof Player)) throw new Error("You cannot run this command from console!");
        Player player = (Player) sender;
        String path = (String) params.get(0);
        String rgString =  CVGames.getInstance().getConfig().getString(path);
        String[] locations = rgString.split(" ~ ");
        if (locations.length != 2) {
            throw new Error("Region is not valid!");
        } else {
            BlockUtils.setWESelection(player, player.getWorld(),
                GameUtils.parseBlockLocation(locations[0]).toVector(), // min
                GameUtils.parseBlockLocation(locations[1]).toVector() // max
            );
        }
        return new TextComponent("§bRegion Selected!");
    }
}
