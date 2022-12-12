package org.cubeville.cvgames.models;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class GameRegion {
    Location min;
    Location max;

    public GameRegion(Location min, Location max) {
        this.min = min;
        this.max = max;
    }

    public Location getMin() {
        return min;
    }

    public Location getMax() {
        return max;
    }

    public boolean containsPlayer(Player player) {
        return containsLocation(player.getLocation());
    }

    public boolean containsEntity(Entity entity) {
        return containsLocation(entity.getLocation());
    }

    public boolean containsLocation(Location location) {
        return (max.getX() >= location.getX() && location.getX() >= min.getX()) &&
                (max.getY() >= location.getY() && location.getY() >= min.getY()) &&
                (max.getZ() >= location.getZ() && location.getZ() >= min.getZ());
    }
}

