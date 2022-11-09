package org.cubeville.cvgames.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.cubeville.cvgames.CVGames;
import org.cubeville.cvgames.models.Arena;
import org.cubeville.cvgames.models.MetricKey;

import java.util.List;
import java.util.Map;

public class MetricsArena extends RunnableCommand {
    @Override
    public TextComponent execute(CommandSender sender, List<Object> params) throws Error {
        Arena arena = (Arena) params.get(0);
        TextComponent output = new TextComponent("Metrics for arena " + arena.getName() + ":\n");
        output.setBold(true);
        output.setColor(ChatColor.of("#f2f2f2"));
        Map<MetricKey, Long> metricMap = CVGames.dataManager().getArenaMetrics(arena);
        for (MetricKey key : metricMap.keySet()) {
            TextComponent dash = new TextComponent("- ");
            dash.setColor(ChatColor.of("#f22e2e"));
            output.addExtra(dash);

            TextComponent gameName = new TextComponent("[game: " + key.gameName.toLowerCase() + "] ");
            gameName.setColor(ChatColor.of("#f26363"));
            output.addExtra(gameName);

            TextComponent metricName = new TextComponent(key.metricName.toLowerCase() + ": ");
            metricName.setColor(ChatColor.of("#f29494"));
            output.addExtra(metricName);

            TextComponent metricValue = new TextComponent(String.valueOf(metricMap.get(key)));
            metricValue.setColor(ChatColor.of("#f2f2f2"));
            output.addExtra(metricValue);
            output.addExtra("\n");
        }
        return output;
    }
}
