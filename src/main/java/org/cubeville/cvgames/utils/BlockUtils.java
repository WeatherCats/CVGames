package org.cubeville.cvgames.utils;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

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

    public static void setWESelection(Player player, World world, Vector pos1, Vector pos2) {
        WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        BlockVector3 wep1 = BlockVector3.at(pos1.getBlockX(), pos1.getBlockY(), pos1.getBlockZ());
        BlockVector3 wep2 = BlockVector3.at(pos2.getBlockX(), pos2.getBlockY(), pos2.getBlockZ());
        CuboidRegionSelector selector = new CuboidRegionSelector(BukkitAdapter.adapt(world), wep1, wep2);
        worldEdit.getSession(player).setRegionSelector(BukkitAdapter.adapt(world), selector);
    }


}
