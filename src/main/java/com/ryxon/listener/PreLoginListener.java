package com.ryxon.listener;

import com.ryxon.RyxoNet;
import com.ryxon.config.ConfigManager.SecurityMode;
import com.ryxon.util.IpUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PreLoginListener implements Listener {

    private final RyxoNet plugin;
    private final Set<String> whitelistCache = new HashSet<>();

    public PreLoginListener(RyxoNet plugin) {
        this.plugin = plugin;
        reloadCache();
    }

    public void reloadCache() {
        whitelistCache.clear();
        if (plugin.getConfigManager().isWhitelistEnabled()) {
            List<String> ips = plugin.getConfigManager().getWhitelistedIps();
            whitelistCache.addAll(ips);
            plugin.getLogger().info("[RyxoNET] Loaded " + whitelistCache.size() + " whitelisted IPs");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;

        String ip = event.getAddress().getHostAddress();
        String name = event.getName();

        var cfg = plugin.getConfigManager();
        SecurityMode mode = cfg.getSecurityMode();

        boolean whitelistPassed = !cfg.isWhitelistEnabled() || whitelistCache.contains(ip);
        boolean proxyPassed = !cfg.isProxyProtectionEnabled() || validateProxyAuthentication(event, ip);
        boolean hostnamePassed = !cfg.isHostnameEnabled() || validateHostname(ip);

        if (!whitelistPassed || !proxyPassed || !hostnamePassed) {
            String kickMsg = cfg.isHostnameEnabled() && !hostnamePassed ? cfg.getHostnameKickMessage() : "§cConnection rejected by security";
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, kickMsg);

            if (cfg.shouldLogBlockedConnections()) {
                plugin.getLogger().warning("[RyxoNET] Blocked " + name + " (" + ip + ") - Whitelist: " + whitelistPassed + ", Proxy: " + proxyPassed + ", Hostname: " + hostnamePassed);
            }
            return;
        }

        if (cfg.shouldLogAllowedConnections()) {
            plugin.getLogger().info("[RyxoNET] Allowed " + name + " (" + ip + ")");
        }
    }

    private boolean validateHostname(String ip) {
        var cfg = plugin.getConfigManager();
        if (!cfg.isUseReverseDns()) return true;  // Skip if disabled

        try {
            InetAddress addr = InetAddress.getByName(ip);
            String resolved = addr.getCanonicalHostName().toLowerCase();

            if (cfg.shouldLogHostnameDetails()) {
                plugin.getLogger().fine("[RyxoNET] Resolved hostname for " + ip + ": " + resolved);
            }

            for (String allowed : cfg.getAllowedHostnames()) {
                String lowerAllowed = allowed.toLowerCase();
                if (lowerAllowed.startsWith("*.")) {
                    String domain = lowerAllowed.substring(2);
                    if (resolved.endsWith("." + domain) || resolved.equals(domain)) return true;
                } else if (resolved.equals(lowerAllowed) || resolved.endsWith("." + lowerAllowed)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            plugin.getLogger().warning("[RyxoNET] Hostname validation failed for " + ip + ": " + e.getMessage());
            return false;
        }
    }

    private boolean validateProxyAuthentication(AsyncPlayerPreLoginEvent event, String ip) {
        // Placeholder for proxy auth - implement based on proxy type (e.g., Velocity forwarding)
        return true;  // Replace with real logic
    }
}