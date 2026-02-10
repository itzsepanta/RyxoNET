package com.ryxon.command;

import com.ryxon.RyxoNet;
import com.ryxon.config.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Handles /ryxonet commands with tab completion.
 */
public class RyxoNetCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUBCOMMANDS = Arrays.asList("reload", "status", "addhost", "removehost", "listhosts", "help");

    private final RyxoNet plugin;

    public RyxoNetCommand(RyxoNet plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        if (!sender.hasPermission("ryxonet.admin")) {
            sender.sendMessage("§cNo permission.");
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "reload":
                plugin.reloadPlugin();
                sender.sendMessage("§aRyxoNET reloaded.");
                break;
            case "status":
                sendStatus(sender);
                break;
            case "addhost":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /ryxonet addhost <hostname>");
                    break;
                }
                addHostname(sender, args[1]);
                break;
            case "removehost":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /ryxonet removehost <hostname>");
                    break;
                }
                removeHostname(sender, args[1]);
                break;
            case "listhosts":
                listHostnames(sender);
                break;
            case "help":
            default:
                sendHelp(sender);
                break;
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6RyxoNET Commands:");
        sender.sendMessage("§7/ryxonet reload §8- Reload config");
        sender.sendMessage("§7/ryxonet status §8- View security status");
        sender.sendMessage("§7/ryxonet addhost <host> §8- Add allowed hostname");
        sender.sendMessage("§7/ryxonet removehost <host> §8- Remove allowed hostname");
        sender.sendMessage("§7/ryxonet listhosts §8- List allowed hostnames");
        sender.sendMessage("§7/ryxonet help §8- This message");
    }

    private void sendStatus(CommandSender sender) {
        var cfg = plugin.getConfigManager();
        sender.sendMessage("§6RyxoNET Status:");
        sender.sendMessage("§7Mode: §f" + cfg.getSecurityMode());
        sender.sendMessage("§7Hostname Protection: §f" + (cfg.isHostnameEnabled() ? "Enabled (" + cfg.getAllowedHostnames().size() + " hosts)" : "Disabled"));
        sender.sendMessage("§7Whitelist: §f" + (cfg.isWhitelistEnabled() ? "Enabled (" + cfg.getWhitelistedIps().size() + " IPs)" : "Disabled"));
        sender.sendMessage("§7Proxy Protection: §f" + (cfg.isProxyProtectionEnabled() ? "Enabled" : "Disabled"));
    }

    private void addHostname(CommandSender sender, String host) {
        var cfg = plugin.getConfigManager();
        List<String> hosts = cfg.getAllowedHostnames();
        if (hosts.contains(host)) {
            sender.sendMessage("§cHostname already added.");
            return;
        }
        hosts.add(host);
        plugin.getConfig().set("hostname.allowed-hostnames", hosts);
        plugin.saveConfig();
        plugin.reloadPlugin();
        sender.sendMessage("§aAdded hostname: " + host);
    }

    private void removeHostname(CommandSender sender, String host) {
        var cfg = plugin.getConfigManager();
        List<String> hosts = cfg.getAllowedHostnames();
        if (!hosts.remove(host)) {
            sender.sendMessage("§cHostname not found.");
            return;
        }
        plugin.getConfig().set("hostname.allowed-hostnames", hosts);
        plugin.saveConfig();
        plugin.reloadPlugin();
        sender.sendMessage("§aRemoved hostname: " + host);
    }

    private void listHostnames(CommandSender sender) {
        var cfg = plugin.getConfigManager();
        sender.sendMessage("§6Allowed Hostnames:");
        for (String host : cfg.getAllowedHostnames()) {
            sender.sendMessage("§7- " + host);
        }
        if (cfg.getAllowedHostnames().isEmpty()) {
            sender.sendMessage("§cNone configured.");
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            StringUtil.copyPartialMatches(args[0], SUBCOMMANDS, completions);
            Collections.sort(completions);
            return completions;
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("removehost"))) {
            return StringUtil.copyPartialMatches(args[1], plugin.getConfigManager().getAllowedHostnames(), new ArrayList<>());
        }
        return Collections.emptyList();
    }
}