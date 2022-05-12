package org.cubeville.cvgames.utils;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Objects;

public class BlockUtils {

    public static Region getWESelection(Player player) {
        WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        Region selection = null;
        try {
            assert worldEdit != null;
            selection = worldEdit.getSession(player).getSelection(worldEdit.getSession(player).getSelectionWorld());
        }
        catch(IncompleteRegionException e) {
            throw new IllegalArgumentException("Incomplete region selection.");
        }
        if(selection == null) throw new IllegalArgumentException("No region selected.");
        return selection;
    }

    public static Location getWESelectionMin(Player player) {
        BlockVector3 min = getWESelection(player).getMinimumPoint();
        return new Location(BukkitAdapter.adapt(Objects.requireNonNull(getWESelection(player).getWorld())), min.getX(), min.getY(), min.getZ());
    }

    public static Location getWESelectionMax(Player player) {
        BlockVector3 min = getWESelection(player).getMaximumPoint();
        return new Location(BukkitAdapter.adapt(Objects.requireNonNull(getWESelection(player).getWorld())), min.getX(), min.getY(), min.getZ());
    }

}
