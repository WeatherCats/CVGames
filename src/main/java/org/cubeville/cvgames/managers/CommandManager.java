package org.cubeville.cvgames.managers;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.CVGames;
import org.cubeville.cvgames.commands.*;
import org.cubeville.cvgames.models.Arena;

import java.util.*;

public class CommandManager {

    private static final String DEFAULT_ERROR = "Invalid command, Enter \"/cvgames help\" to view all commands for this plugin.";
    private static final String DEFAULT_PERMISSIONS_ERROR = "You do not have permission to run this command";


    private static final HashMap<String, RunnableCommand> commands = new HashMap<>() {{
        put("center", new CenterPosition());
        put("createarena", new CreateArena());
        put("deletearena", new DeleteArena());
        put("verify", new VerifyArena());
        put("addgame", new AddArenaGame());
        put("removegame", new RemoveArenaGame());
        put("clearedit", new ClearEditingObjectVariable());
        put("addvar", new AddArenaVariable());
        put("setvar", new SetArenaVariable());
        put("setedit", new SetEditingObjectVariable());
        put("removevar", new RemoveArenaVariable());
        put("giveitem", new GiveItem());
        put("queuejoin", new QueueJoin());
        put("queueleave", new QueueLeave());
        put("help", new Help());
        put("hoststart", new HostStart());
        put("hostannounce", new HostAnnounce());
        put("hostend", new HostEnd());
        put("hostplayersadd", new HostPlayersAdd());
        put("hostplayersremove", new HostPlayersRemove());
        put("hostplayerslist", new HostPlayersList());
        put("hostteamsset", new HostTeamsSet());
        put("hostteamsremove", new HostTeamsRemove());
        put("hostlobby", new HostLobby());
        put("hostcountdown", new HostCountdown());
        put("arenas", new Arenas());
    }};

    public static boolean parse(CommandSender sender, String[] argsIn) {

        StringBuilder full = new StringBuilder();
        for (String arg : argsIn) {
            if (full.length() > 0) full.append(" ");
            full.append(arg);
        }

        String[] args;
        try {
            args = smartSplit(full.toString()).toArray(new String[0]);
        } catch (IllegalArgumentException e) {
            return sendErrorMessage(sender, e.getMessage());
        }

        if (args.length == 0) return sendErrorMessage(sender, DEFAULT_ERROR);

        // this file makes me want to cry, but at least i don't have to write a command manager
        switch (args[0].toLowerCase()) {
            case "help":
                if (!sender.hasPermission("cvgames.setup.help")) { return sendErrorMessage(sender, DEFAULT_PERMISSIONS_ERROR); }
                return runCommand("help", sender, new ArrayList<>());
            case "queue":
                String leaveOrJoin = args[1].toLowerCase();
                if (args.length != 4 && leaveOrJoin.equals("leave") || args.length != 5 && leaveOrJoin.equals("join")) return sendErrorMessage(sender, DEFAULT_ERROR);
                switch (leaveOrJoin) {
                    case "join":
                    case "leave":
                        if (!sender.hasPermission("cvgames.queue." + leaveOrJoin)) { return sendErrorMessage(sender, DEFAULT_PERMISSIONS_ERROR); }

                        Arena arena = ArenaManager.getArena(args[2].toLowerCase());
                        if (arena == null) {
                            return sendErrorMessage(sender, "Error: Arena with name " + args[2].toLowerCase() + " does not exist!");
                        }
                        if (arena.getVariables().size() == 0) return sendErrorMessage(sender,"You need to add a game to the arena " + arena.getName());

                        Player player = Bukkit.getPlayer(args[3]);
                        if (player == null) return sendErrorMessage(sender, "Error: Player with name " + args[3] + " is not online!");

                        ArrayList<Object> params = new ArrayList<>(List.of(arena, player));
                        if (args.length == 5) {
                            params.add(args[4].toLowerCase());
                        }
                        return runCommand("queue" + leaveOrJoin, sender, params);

                }
            case "center":
            case "centre":
                if (!sender.hasPermission("cvgames.setup.center")) { return sendErrorMessage(sender, DEFAULT_PERMISSIONS_ERROR); }
                return runCommand("center", sender, new ArrayList<>());
            case "giveitem":
                if (!sender.hasPermission("cvgames.setup.giveitem")) { return sendErrorMessage(sender, DEFAULT_PERMISSIONS_ERROR); }
                if (args.length != 2) return sendErrorMessage(sender, "Error: Invalid parameter size. Did you mean \"/cvgames giveitem <path>\" ?");
                return runCommand("giveitem", sender, List.of(args[1].toLowerCase()));
            case "arena":
                if (args.length < 3) return sendErrorMessage(sender, DEFAULT_ERROR);
                // GAMES ARENA COMMANDS
                switch (args[1].toLowerCase()) {
                    case "create":
                    case "delete":
                        if (!sender.hasPermission("cvgames.setup." + args[1].toLowerCase())) { return sendErrorMessage(sender, DEFAULT_PERMISSIONS_ERROR); }
                        if (args.length != 3) return sendErrorMessage(sender, "Error: Invalid parameter size. Did you mean \"/cvgames arena " + args[1].toLowerCase() + " <arena_name>\" ?");
                        return runCommand(args[1].toLowerCase() + "arena", sender, List.of(args[2].toLowerCase()));
                    default:
                        Arena arena = ArenaManager.getArena(args[1].toLowerCase());
                        if (arena == null) {
                            return sendErrorMessage(sender, "Error: Arena with name " + args[1].toLowerCase() + " does not exist!");
                        }
                        if (arena.getVariables().size() == 0 && !args[2].equalsIgnoreCase("addgame")) return sendErrorMessage(sender,"You need to add a game to the arena " + arena.getName());

                        return parseArenaCommands(sender, arena, Arrays.copyOfRange(args, 2, args.length));
                }
            case "arenas":
                if (!sender.hasPermission("cvgames.setup.list")) { return sendErrorMessage(sender, DEFAULT_PERMISSIONS_ERROR); }
                if (args.length > 2) return sendErrorMessage(sender, "Error: Invalid parameter size. Did you mean \"/cvgames arenas [game]\" ?");
                List<Object> arenasParameters = new ArrayList<>();
                if (args.length == 2) {
                    if (!CVGames.gameManager().hasGame(args[1].toLowerCase())) return sendErrorMessage(sender, "Error: Game with name " + args[1].toLowerCase() + " does not exist!");
                    arenasParameters.add(args[1].toLowerCase());
                }
                return runCommand("arenas", sender, arenasParameters);
            case "host":
                return parseHostingCommands(sender, Arrays.copyOfRange(args, 1, args.length));
            default:
                return sendErrorMessage(sender, DEFAULT_ERROR);
        }
    }

    private static boolean runCommand(String commandName, CommandSender sender, List<Object> parameters) {
        try {
            TextComponent response = commands.get(commandName).execute(sender, parameters);
            if (response != null) sender.spigot().sendMessage(response);
            return true;
        } catch (Error e) {
            return sendErrorMessage(sender, e.getMessage());
        }
    }

    private static boolean parseArenaCommands(CommandSender sender, Arena arena, String[] args) {
        String actionArg = args[0].toLowerCase();
        ArrayList<Object> parametersList = new ArrayList<>(List.of(arena));
        switch (actionArg) {
            case "verify":
                if (args.length > 2) return sendErrorMessage(sender, "Error: Invalid parameter size. Did you mean \"/cvgames arena <arena_name> verify [path]\" ?");
                if (!sender.hasPermission("cvgames.setup.verify")) { return sendErrorMessage(sender, DEFAULT_PERMISSIONS_ERROR); }
                if (args.length == 2) { parametersList.add(args[1].toLowerCase());}
                return runCommand("verify", sender, parametersList);
            case "clearedit":
                if (!sender.hasPermission("cvgames.setup.clearedit")) { return sendErrorMessage(sender, DEFAULT_PERMISSIONS_ERROR); }
                return runCommand("clearedit", sender, parametersList);
            case "addgame":
            case "removegame":
                if (!sender.hasPermission("cvgames.setup." + actionArg)) { return sendErrorMessage(sender, DEFAULT_PERMISSIONS_ERROR); }
                if (args.length != 2) return sendErrorMessage(sender, "Error: Invalid parameter size. Did you mean \"/cvgames arena <arena_name>" + actionArg + "<game_name>\" ?");
                String gameName = args[1].toLowerCase();
                if (!CVGames.gameManager().hasGame(gameName))  return sendErrorMessage(sender, "Error: game with name \"" + gameName + "\" does not exist");
                // add game name
                parametersList.add(gameName);
                return runCommand("addgame", sender, parametersList);
            case "addvar":
            case "setvar":
                if (!sender.hasPermission("cvgames.setup." + actionArg)) { return sendErrorMessage(sender, DEFAULT_PERMISSIONS_ERROR); }
                if (args.length > 3 || args.length < 2) return sendErrorMessage(sender, "Error: Invalid parameter size. Did you mean \"/cvgames arena <arena_name> " + actionArg + " <var_name> [input]\" ?");
                parametersList.add(args[1].toLowerCase());
                if (args.length == 3) parametersList.add(args[2]);
                return runCommand(actionArg, sender, parametersList);
            case "setedit":
            case "removevar":
                if (!sender.hasPermission("cvgames.setup." + actionArg)) { return sendErrorMessage(sender, DEFAULT_PERMISSIONS_ERROR); }
                if (args.length != 3) return sendErrorMessage(sender, "Error: Invalid parameter size. Did you mean \"/cvgames arena <arena_name> " + actionArg + " <var_name> <index>\" ?");
                parametersList.add(args[1].toLowerCase());
                parametersList.add(args[2]);
                return runCommand(actionArg, sender, parametersList);
        }
        return sendErrorMessage(sender, DEFAULT_ERROR);
    }

    private static boolean parseHostingCommands(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return sendErrorMessage(sender, "Error: cannot host a game from the console");
        Player player = (Player) sender;

        if (args.length == 0) return sendErrorMessage(sender, DEFAULT_ERROR);

        String actionArg = args[0].toLowerCase();
        if (actionArg.equals("start")) {
            if (!player.hasPermission("cvgames.hosting.start")) { return sendErrorMessage(player, DEFAULT_PERMISSIONS_ERROR); }
            if (args.length != 3) { return sendErrorMessage(player, "Error: Invalid parameter size. Did you mean \"/cvgames host start <arena_name> <game_name>\" ?"); }
            String arenaName = args[1].toLowerCase();
            String gameName = args[2].toLowerCase();

            Arena arena = ArenaManager.getArena(arenaName);
            if (arena == null) { return sendErrorMessage(player, "Arena with name " + arenaName + " does not exist!"); }
            if (!CVGames.gameManager().hasGame(gameName)) { return sendErrorMessage(player, "Game with name " + gameName + " does not exist!"); }
            if (!arena.getGameNames().contains(gameName)) { return sendErrorMessage(player, "Arena " + arenaName + " cannot host the game " + gameName); }
            return runCommand("hoststart", player, List.of(arena, gameName));
        }

        Arena arena = PlayerManager.getPlayerArena(player);
        if (arena == null || arena.getQueue().getHost() == null || !arena.getQueue().getHost().equals(player)) return sendErrorMessage(player, "You need to be hosting an arena in order to run any hosting commands!");

        switch (actionArg) {
            case "announce":
                if (!player.hasPermission("cvgames.hosting.announce")) { return sendErrorMessage(player, DEFAULT_PERMISSIONS_ERROR); }
                if (args.length > 2) return sendErrorMessage(sender, "Error: Invalid parameter size. Did you mean \"/cvgames host announce [minutes]\" ?");
                ArrayList<Object> announceParams = new ArrayList<>();
                if (args.length == 2) { announceParams.add(Integer.parseInt(args[1])); }
                return runCommand("hostannounce", player, announceParams);
            case "countdown":
                if (!player.hasPermission("cvgames.hosting.countdown")) { return sendErrorMessage(player, DEFAULT_PERMISSIONS_ERROR); }
                if (args.length > 2) return sendErrorMessage(sender, "Error: Invalid parameter size. Did you mean \"/cvgames host countdown [seconds]\" ?");
                ArrayList<Object> countdownParams = new ArrayList<>();
                if (args.length == 2) { countdownParams.add(args[1]); }
                return runCommand("hostcountdown", player, countdownParams);
            case "end":
                if (!player.hasPermission("cvgames.hosting.end")) { return sendErrorMessage(player, DEFAULT_PERMISSIONS_ERROR); }
                if (args.length != 1) return sendErrorMessage(sender, "Error: Invalid parameter size. Did you mean \"/cvgames host end\" ?");
                return runCommand("hostend", player, List.of(arena));
            case "transfer":
                // would be cool but dw about it for now
                break;
            case "help":
                break;
            case "lobby":
                if (!player.hasPermission("cvgames.hosting.lobby")) { return sendErrorMessage(player, DEFAULT_PERMISSIONS_ERROR); }
                if (args.length != 1) return sendErrorMessage(sender, "Error: Invalid parameter size. Did you mean \"/cvgames host lobby\" ?");
                return runCommand("hostlobby", player, List.of());
            case "players":
                if (args.length == 1) {
                    // gonna make this an alias for players list because i am lazy and don't want to type
                    if (!player.hasPermission("cvgames.hosting.players.list")) { return sendErrorMessage(player, DEFAULT_PERMISSIONS_ERROR); }
                    return runCommand("hostplayerslist", player, List.of());
                }
                String subActionArg = args[1].toLowerCase();
                switch (subActionArg) {
                    case "add":
                    case "remove":
                        if (!player.hasPermission("cvgames.hosting.players." + subActionArg)) { return sendErrorMessage(player, DEFAULT_PERMISSIONS_ERROR); }
                        if (args.length != 3) return sendErrorMessage(sender, "Error: Invalid parameter size. Did you mean \"/cvgames host players " + subActionArg + " <player>\" ?");
                        return runCommand("hostplayers" + subActionArg, player, List.of(args[2]));
                    case "list":
                        if (!player.hasPermission("cvgames.hosting.players.list")) { return sendErrorMessage(player, DEFAULT_PERMISSIONS_ERROR); }
                        if (args.length != 2) return sendErrorMessage(sender, "Error: Invalid parameter size. Did you mean \"/cvgames host players list\" ?");
                        return runCommand("hostplayerslist", player, List.of());
                }
                return sendErrorMessage(sender, DEFAULT_ERROR);
            case "teams":
                if (args.length == 1) sendErrorMessage(sender, DEFAULT_ERROR);
                String subTeamsArg = args[1].toLowerCase();
                switch (subTeamsArg) {
                    case "add":
                    case "set":
                        if (!player.hasPermission("cvgames.hosting.teams.set")) { return sendErrorMessage(player, DEFAULT_PERMISSIONS_ERROR); }
                        if (args.length != 4) return sendErrorMessage(sender, "Error: Invalid parameter size. Did you mean \"/cvgames host players " + subTeamsArg + " <player> <team>\" ?");
                        return runCommand("hostteamsset", player, List.of(args[2], args[3]));
                    case "remove":
                    case "delete":
                    case "unset":
                    case "clear":
                        if (!player.hasPermission("cvgames.hosting.teams.remove")) { return sendErrorMessage(player, DEFAULT_PERMISSIONS_ERROR); }
                        if (args.length != 3) return sendErrorMessage(sender, "Error: Invalid parameter size. Did you mean \"/cvgames host players " + subTeamsArg + " <player>\" ?");
                        return runCommand("hostteamsremove", player, List.of(args[2]));
                }
        }

        return sendErrorMessage(player, DEFAULT_ERROR);
    }

        // Written by Fredi, I don't want to have to rewrite this
    private static List<String> smartSplit(String full) {
        // I don't like this piece of code....
        if(full.length() == 0) return new ArrayList<>();
        boolean inQuotes = false;
        List<String> ret = new ArrayList<>();
        ret.add("");
        for(int i = 0; i < full.length(); i++) {
            String current = ret.get(ret.size() - 1);
            int lsize = ret.size();
            int size = current.length();
            char c = full.charAt(i);
            char last = (size > 0 ? current.charAt(size - 1) : ' ');

            if(c == '"' && (size == 0 || last == ' ' || last == ':') && !inQuotes) {
                inQuotes = true;
            }
            else if(c == '"' && inQuotes && (i == full.length() - 1 || full.charAt(i + 1) == ' ')) {
                inQuotes = false;
            }
            else if(c == ' ' && !inQuotes) {
                ret.add("");
            }
            else {
                ret.set(lsize - 1, ret.get(lsize - 1) + full.charAt(i));
            }
        }

        if(inQuotes) throw new IllegalArgumentException("Unbalanced quotes!");
        return ret;
    }

    private static boolean sendErrorMessage(CommandSender sender, String message) {
        sender.sendMessage("§c" + message);
        return false;
    }
}
