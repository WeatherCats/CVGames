package org.cubeville.cvgames.managers;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.cvgames.commands.*;

import java.util.*;

public class CommandManager {

    private static final String DEFAULT_ERROR = "Invalid command, Enter \"/cvgames help\" to view all commands for this plugin.";
    private static final String DEFAULT_PERMISSIONS_ERROR = "Invalid command, Enter \"/cvgames help\" to view all commands for this plugin.";


    private static HashMap<String, RunnableCommand> commands = new HashMap<>() {{
        put("center", new CenterPosition());
        put("createarena", new CreateArena());
        put("deletearena", new DeleteArena());
        put("verify", new VerifyArena());
        put("setgame", new SetArenaGame());
        put("clearedit", new ClearEditingObjectVariable());
        put("addvar", new AddArenaVariable());
        put("setvar", new SetArenaVariable());
        put("setedit", new SetEditingObjectVariable());
        put("removevar", new RemoveArenaVariable());
        put("giveitem", new GiveItem());
        put("queuejoin", new QueueJoin());
        put("queueleave", new QueueLeave());
        put("help", new Help());
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

        switch (args[0].toLowerCase()) {
            case "help":
                if (!sender.hasPermission("cvgames.setup.help")) { return sendErrorMessage(sender, DEFAULT_PERMISSIONS_ERROR); }
                return runCommand("help", sender, new ArrayList<>());
            case "queue":
                if (args.length != 4) return sendErrorMessage(sender, DEFAULT_ERROR);
                String leaveOrJoin = args[1].toLowerCase();
                switch (leaveOrJoin) {
                    case "join":
                    case "leave":
                        String arenaName = args[2].toLowerCase();
                        if (!ArenaManager.hasArena(arenaName)) {
                            return sendErrorMessage(sender, "Arena with name " + arenaName + " does not exist!");
                        }

                        Player player = Bukkit.getPlayer(args[3]);
                        if (player == null) return sendErrorMessage(sender, "Player with name " + args[3] + " is not online!");
                        if (!sender.hasPermission("cvgames.queue." + leaveOrJoin)) { return sendErrorMessage(sender, DEFAULT_PERMISSIONS_ERROR); }
                        return runCommand("queue" + leaveOrJoin, sender, List.of(arenaName, player));

                }
            case "center":
                if (!sender.hasPermission("cvgames.setup.center")) { return sendErrorMessage(sender, DEFAULT_PERMISSIONS_ERROR); }
                return runCommand("center", sender, new ArrayList<>());
            case "giveitem":
                if (!sender.hasPermission("cvgames.setup.giveitem")) { return sendErrorMessage(sender, DEFAULT_PERMISSIONS_ERROR); }
                if (args.length != 2) return sendErrorMessage(sender, "Error: invalid parameter size. Did you mean \"/cvgames giveitem <path>\" ?");
                return runCommand("giveitem", sender, List.of(args[1].toLowerCase()));
            case "arena":
                if (args.length < 3) return sendErrorMessage(sender, DEFAULT_ERROR);
                // GAMES ARENA COMMANDS
                switch (args[1].toLowerCase()) {
                    case "create":
                    case "delete":
                        if (!sender.hasPermission("cvgames.setup." + args[1].toLowerCase())) { return sendErrorMessage(sender, DEFAULT_PERMISSIONS_ERROR); }
                        if (args.length != 3) return sendErrorMessage(sender, "Error: invalid parameter size. Did you mean \"/cvgames arena " + args[1].toLowerCase() + " <arena_name>\" ?");
                        return runCommand(args[1].toLowerCase() + "arena", sender, List.of(args[2].toLowerCase()));
                    default:
                        String arenaName = args[1].toLowerCase();
                        if (!ArenaManager.hasArena(arenaName)) {
                            return sendErrorMessage(sender, "Arena with name " + arenaName + " does not exist!");
                        }
                        switch (args[2].toLowerCase()) {
                            case "verify":
                                if (args.length > 4) return sendErrorMessage(sender, "Error: invalid parameter size. Did you mean \"/cvgames arena <arena_name> verify [path]\" ?");
                                if (!sender.hasPermission("cvgames.setup.verify")) { return sendErrorMessage(sender, DEFAULT_PERMISSIONS_ERROR); }
                                ArrayList<Object> verifyParams = new ArrayList<>(List.of(arenaName));
                                if (args.length == 4) { verifyParams.add(args[3].toLowerCase());}
                                return runCommand("verify", sender, verifyParams);
                            case "clearedit":
                                if (!sender.hasPermission("cvgames.setup.clearedit")) { return sendErrorMessage(sender, DEFAULT_PERMISSIONS_ERROR); }
                                return runCommand("clearedit", sender, List.of(arenaName));
                            case "setgame":
                                if (!sender.hasPermission("cvgames.setup.setgame")) { return sendErrorMessage(sender, DEFAULT_PERMISSIONS_ERROR); }
                                if (args.length != 4) return sendErrorMessage(sender, "Error: invalid parameter size. Did you mean \"/cvgames arena <arena_name> setgame <game_name>\" ?");
                                return runCommand("setgame", sender, List.of(arenaName, args[3].toLowerCase()));
                            case "addvar":
                            case "setvar":
                                if (!sender.hasPermission("cvgames.setup." + args[2].toLowerCase())) { return sendErrorMessage(sender, DEFAULT_PERMISSIONS_ERROR); }
                                if (args.length > 5 || args.length < 4) return sendErrorMessage(sender, "Error: invalid parameter size. Did you mean \"/cvgames arena <arena_name> " + args[2].toLowerCase() + " <var_name> [input]\" ?");
                                List<Object> params = new ArrayList<>(List.of(arenaName, args[3].toLowerCase()));
                                if (args.length == 5) params.add(args[4]);
                                return runCommand(args[2].toLowerCase(), sender, params);
                            case "setedit":
                                if (!sender.hasPermission("cvgames.setup.setedit")) { return sendErrorMessage(sender, DEFAULT_PERMISSIONS_ERROR); }
                                if (args.length != 5) return sendErrorMessage(sender, "Error: invalid parameter size. Did you mean \"/cvgames arena <arena_name> setedit <var_name> <index>\" ?");
                                return runCommand("setedit", sender, List.of(arenaName, args[3].toLowerCase(), args[4]));
                            case "removevar":
                                if (!sender.hasPermission("cvgames.setup.removevar")) { return sendErrorMessage(sender, DEFAULT_PERMISSIONS_ERROR); }
                                if (args.length != 5) return sendErrorMessage(sender, "Error: invalid parameter size. Did you mean \"/cvgames arena <arena_name> removevar <var_name> <index>\" ?");
                                return runCommand("removevar", sender, List.of(arenaName, args[3].toLowerCase(), args[4]));
                        }
                }
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
