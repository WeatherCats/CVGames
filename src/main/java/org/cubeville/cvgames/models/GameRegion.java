package org.cubeville.cvgames.models;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class GameRegion {
    Location min, max, entityMax;

    public GameRegion(Location min, Location max) {
        this.min = min;
        this.max = max;
        this.entityMax = max.clone().add(1, .9999, 1);
    }

    public Location getMin() {
        return min;
    }

    public Location getMax() {
        return max;
    }

    public boolean containsPlayer(Player player) {
        // for players and entities, we need to add 1 to the x and z values
        return containsLocation(player.getLocation(), min, entityMax);
    }

    public boolean containsEntity(Entity entity) {
        // for players and entities, we need to add 1 to the x and z values
        return containsLocation(entity.getLocation(), min, entityMax);
    }

    public boolean containsLocation(Location location) {
        return containsLocation(location, min, max);
    }

    public boolean containsLocation(Location location, Location min, Location max) {
        return (max.getX() >= location.getX() && location.getX() >= min.getX()) &&
                (max.getY() >= location.getY() && location.getY() >= min.getY()) &&
                (max.getZ() >= location.getZ() && location.getZ() >= min.getZ());
    }
}

