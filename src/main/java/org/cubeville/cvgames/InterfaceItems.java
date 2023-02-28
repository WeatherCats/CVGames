package org.cubeville.cvgames;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.cubeville.cvgames.utils.GameUtils;

public final class InterfaceItems {
    public static final ItemStack QUEUE_LEAVE_ITEM = GameUtils.customHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTdjOGIwYjU2MmQ2YjBlMTBkZTUzMDNjZjgzNjk3ODExNGE0ZDA1ZDhmMTk3NDE2YTRhZDFhOWJkNDVlNTZjZCJ9fX0=", "§c§l§oLeave Queue §7§o(Right Click)");
    public static final ItemStack SPECTATE_LEAVE_ITEM = GameUtils.customHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTdjOGIwYjU2MmQ2YjBlMTBkZTUzMDNjZjgzNjk3ODExNGE0ZDA1ZDhmMTk3NDE2YTRhZDFhOWJkNDVlNTZjZCJ9fX0=", "§c§l§oLeave §7§o(Right Click)");
    public static final ItemStack SPECTATE_PLAYER_NAV_ITEM = GameUtils.customItem(Material.COMPASS, "§e§l§oSpectate Player §7§o(Right Click)");
    public static final ItemStack TEAM_SELECTOR_ITEM = GameUtils.customHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmM2YWQwYmMwNGI0NDdjZmIxMmViYjVkMDRmMGNmZWE1OTcyMWFjZGQ3MjhlOTZlYzJhY2YzOWM3MWIxMGE5NCJ9fX0=", "§b§l§oSelect Team §7§o(Right Click)");
    public static final ItemStack RANDOMIZE_TEAM_ITEM = GameUtils.customItem(Material.BARRIER, "§c§l§oRandomized Team");
}
