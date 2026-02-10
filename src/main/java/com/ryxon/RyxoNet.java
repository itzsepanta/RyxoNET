package com.ryxon;

import com.ryxon.command.RyxoNetCommand;
import com.ryxon.config.ConfigManager;
import com.ryxon.listener.PreLoginListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class RyxoNet extends JavaPlugin {

    private ConfigManager configManager;
    private PreLoginListener preLoginListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.configManager.load();

        this.preLoginListener = new PreLoginListener(this);
        getServer().getPluginManager().registerEvents(preLoginListener, this);

        var cmd = getCommand("ryxonet");
        if (cmd != null) {
            RyxoNetCommand commandHandler = new RyxoNetCommand(this);
            cmd.setExecutor(commandHandler);
            cmd.setTabCompleter(commandHandler);
        } else {
            getLogger().severe("Command 'ryxonet' not found in plugin.yml");
        }

        getLogger().info("[RyxoNET] v" + getDescription().getVersion() + " enabled");
        getLogger().info("[RyxoNET] Security mode: " + configManager.getSecurityMode());
    }

    @Override
    public void onDisable() {
        getLogger().info("[RyxoNET] disabled");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void reloadPlugin() {
        reloadConfig();
        configManager.load();
        preLoginListener.reloadCache();
        getLogger().info("[RyxoNET] Configuration reloaded");
    }
}