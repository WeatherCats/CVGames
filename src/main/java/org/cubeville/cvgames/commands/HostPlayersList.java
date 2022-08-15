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
import org.cubeville.cvgames.models.BaseGame;
import org.cubeville.cvgames.models.TeamSelectorGame;
import org.cubeville.cvgames.vartypes.GameVariableList;
import org.cubeville.cvgames.vartypes.GameVariableTeam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostPlayersList extends RunnableCommand {
    @Override
    public TextComponent execute(CommandSender sender, List<Object> params) throws Error {
        Arena arena = PlayerManager.getPlayerArena((Player) sender);
        Map<Integer, List<Player>> playerTeams = arena.getQueue().getPlayerTeams();
        BaseGame game = arena.getQueue().getGame();
        TextComponent output = new TextComponent("§e§lPlayers playing " + game.getId() + " on " + arena.getName() + ":\n");
        if (game instanceof TeamSelectorGame) {
            List<HashMap<String, Object>> teams = ((TeamSelectorGame) game).getTeamVariable();
            for (int i = -1; i < teams.size(); i++) {
                if (i == -1) {
                    if (playerTeams.get(-1) == null || playerTeams.get(-1).size() == 0) { continue; }
                    TextComponent tc = new TextComponent("No team assigned:\n");
                    tc.setColor(ChatColor.WHITE);
                    output.addExtra(tc);
                } else {
                    GameVariableTeam teamVar = (GameVariableTeam) ((GameVariableList<?>) arena.getGameVariable("teams")).getVariableAtIndex(i);
                    TextComponent indexPrefix = new TextComponent("[" + (i + 1) + "] ");
                    indexPrefix.setColor((ChatColor) teams.get(i).get("chat-color"));
                    indexPrefix.setBold(true);
                    output.addExtra(indexPrefix);
                    // i wish i didnt have to do this
                    output.addExtra(teamVar.getVariableAtField("name").displayString(arena.getName()));
                    TextComponent indexSuffix = new TextComponent(":\n");
                    indexSuffix.setColor((ChatColor) teams.get(i).get("chat-color"));
                    output.addExtra(indexSuffix);
                }
                if (playerTeams.get(i) == null) { continue; }
                for (Player player : playerTeams.get(i)) {
                    TextComponent pText = new TextComponent("  - " + player.getDisplayName() + " ");
                    if (i == -1) {
                        pText.setColor(ChatColor.WHITE);
                        TextComponent setTeam = new TextComponent("[Set Team]\n");
                        setTeam.setColor(ChatColor.AQUA);
                        setTeam.setBold(true);
                        setTeam.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Set player team (click)")));
                        setTeam.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/cvgames host teams set " + player.getDisplayName() + " "));
                        pText.addExtra(setTeam);
                    } else {
                        pText.setColor((ChatColor) teams.get(i).get("chat-color"));
                        TextComponent clearTeam = new TextComponent("[Remove]\n");
                        clearTeam.setColor(ChatColor.AQUA);
                        clearTeam.setBold(true);
                        clearTeam.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Remove player team (click)")));
                        clearTeam.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cvgames host teams remove " + player.getDisplayName()));
                        pText.addExtra(clearTeam);
                    }
                    output.addExtra(pText);
                }
            }
        } else {
            for (Player player : playerTeams.get(-1)) {
                TextComponent pText = new TextComponent("  - " + player.getDisplayName() + "\n");
                pText.setColor(ChatColor.WHITE);
                output.addExtra(pText);
            }
        }
        TextComponent lobbyList = new TextComponent("[View Lobby]");
        lobbyList.setColor(ChatColor.AQUA);
        lobbyList.setBold(true);
        lobbyList.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to view full lobby")));
        lobbyList.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cvgames host lobby"));
        output.addExtra(lobbyList);
        return output;
    }
}
