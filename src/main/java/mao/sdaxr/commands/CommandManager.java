package mao.sdaxr.commands;

import mao.sdaxr.Sdaxr;
import mao.sdaxr.gui.InfoGUI;
import mao.sdaxr.utils.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {
    private final Sdaxr plugin;
    private final ConfigManager configManager;

    public CommandManager() {
        this.plugin = Sdaxr.getInstance();
        this.configManager = plugin.getConfigManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("sdaxr.info")) {
                    new InfoGUI().open(player);
                } else {
                    sender.sendMessage(configManager.getMessage("no-permission"));
                }
            } else {
                sender.sendMessage("Эта команда только для игроков!");
            }
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (sender.hasPermission("sdaxr.admin")) {
                    configManager.reloadConfig();
                    sender.sendMessage(configManager.getMessage("reload-success"));
                } else {
                    sender.sendMessage(configManager.getMessage("no-permission"));
                }
                return true;

            case "info":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("sdaxr.info")) {
                        new InfoGUI().open(player);
                    } else {
                        sender.sendMessage(configManager.getMessage("no-permission"));
                    }
                }
                return true;

            default:
                sender.sendMessage("§cИспользование: /sdaxr [info|reload]");
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("sdaxr.info")) {
                suggestions.add("info");
            }
            if (sender.hasPermission("sdaxr.admin")) {
                suggestions.add("reload");
            }
        }

        return suggestions;
    }
}