package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.managers.PlayerManager;
import org.cubeville.cvgames.models.Arena;

import java.util.List;

public class HostLobby extends RunnableCommand {
    @Override
    public TextComponent execute(CommandSender sender, List<Object> params) throws Error {
        Arena arena = PlayerManager.getPlayerArena((Player) sender);
        TextComponent output = new TextComponent("§e§lLobby for " + arena.getQueue().getGame().getId() + " on " + arena.getName() + ":\n");
        for (Player player : arena.getQueue().getPlayerSet()) {
            output.addExtra(new TextComponent("  - " + player.getDisplayName() + " "));
            if (arena.getQueue().getPlayerTeams().values().stream().anyMatch(players -> players.contains(player))) {
                TextComponent marker = new TextComponent("[✓]\n");
                marker.setColor(ChatColor.GREEN);
                marker.setBold(true);
                marker.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Added to next game (click to toggle)")));
                marker.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cvgames host players remove " + player.getDisplayName()));
                output.addExtra(marker);
            } else {
                TextComponent marker = new TextComponent("[✗]\n");
                marker.setColor(ChatColor.RED);
                marker.setBold(true);
                marker.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Not in next game (click to toggle)")));
                marker.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cvgames host players add " + player.getDisplayName()));
                output.addExtra(marker);
            }
        }
        TextComponent playersList = new TextComponent("[View Players]");
        playersList.setColor(ChatColor.AQUA);
        playersList.setBold(true);
        playersList.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to view players for next game")));
        playersList.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cvgames host players list"));
        output.addExtra(playersList);
        return output;
    }
}
