package com.ryxon.config;

import com.ryxon.RyxoNet;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Manages configuration loading, caching and validation for RyxoNET.
 */
public class ConfigManager {

    private final RyxoNet plugin;
    private FileConfiguration config;

    // ────────────────────────────────────────────────
    // Cached configuration values
    // ────────────────────────────────────────────────

    // Security mode
    private SecurityMode securityMode = SecurityMode.WHITELIST_ONLY;

    // Whitelist settings
    private boolean whitelistEnabled = true;
    private List<String> whitelistedIps = new ArrayList<>();

    // Proxy protection settings
    private boolean proxyProtectionEnabled = false;
    private String passphrase = "";
    private int sessionExpirySeconds = 600;

    // Hostname protection settings
    private boolean hostnameEnabled = false;
    private List<String> allowedHostnames = new ArrayList<>();
    private boolean useReverseDns = true;
    private String serverPublicIp = "";
    private String hostnameKickMessage = "§cPlease connect using the official domain";

    // Logging settings
    private boolean logBlockedConnections = true;
    private boolean logAllowedConnections = false;
    private boolean logHostnameDetails = true;

    public ConfigManager(RyxoNet plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads and validates the configuration.
     */
    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        reload();
    }

    /**
     * Reloads all cached values from config.yml
     */
    public void reload() {
        // Security mode
        String modeStr = config.getString("security-mode", "WHITELIST_ONLY")
                .trim().toUpperCase(Locale.ROOT);
        try {
            this.securityMode = SecurityMode.valueOf(modeStr);
        } catch (IllegalArgumentException e) {
            this.securityMode = SecurityMode.WHITELIST_ONLY;
            plugin.getLogger().warning("Invalid security-mode: " + modeStr + " → fallback to WHITELIST_ONLY");
        }

        // Whitelist
        this.whitelistEnabled = config.getBoolean("whitelist.enabled", true);
        this.whitelistedIps = config.getStringList("whitelist.ips");

        // Proxy protection
        this.proxyProtectionEnabled = config.getBoolean("proxy-protection.enabled", false);
        this.passphrase = config.getString("proxy-protection.passphrase", "").trim();
        this.sessionExpirySeconds = Math.max(60, config.getInt("proxy-protection.session-expiry-seconds", 600));

        // Hostname protection
        this.hostnameEnabled = config.getBoolean("hostname.enabled", false);
        this.allowedHostnames = config.getStringList("hostname.allowed-hostnames");
        this.useReverseDns = config.getBoolean("hostname.use-reverse-dns", true);
        this.serverPublicIp = config.getString("hostname.server-public-ip", "").trim();
        this.hostnameKickMessage = ChatColor.translateAlternateColorCodes('&',
                config.getString("hostname.kick-message",
                        "§cPlease connect using the official domain: §6ryxo.space"));

        // Logging
        this.logBlockedConnections = config.getBoolean("logging.log-blocked-connections", true);
        this.logAllowedConnections = config.getBoolean("logging.log-allowed-connections", false);
        this.logHostnameDetails = config.getBoolean("logging.log-hostname-details", true);

        validateConfiguration();
    }

    private void validateConfiguration() {
        // Proxy protection validation
        if (proxyProtectionEnabled) {
            if (passphrase.isEmpty() || passphrase.contains("change_to")) {
                plugin.getLogger().severe("Proxy passphrase is empty or still default! Proxy protection is NOT secure.");
                proxyProtectionEnabled = false;
            }
        }

        // Hostname protection validation
        if (hostnameEnabled) {
            if (allowedHostnames.isEmpty()) {
                plugin.getLogger().warning("Hostname protection enabled but no allowed hostnames are defined.");
            }
            if (serverPublicIp.isEmpty()) {
                plugin.getLogger().warning("server-public-ip is not set while hostname protection is enabled.");
            }
        }

        // Whitelist validation
        if (whitelistEnabled && whitelistedIps.isEmpty()) {
            plugin.getLogger().warning("Whitelist is enabled but contains no IP addresses.");
        }

        // Mode consistency warnings
        if (securityMode == SecurityMode.PROXY_PROTECTED && !proxyProtectionEnabled) {
            plugin.getLogger().warning("Mode is PROXY_PROTECTED but proxy-protection is disabled.");
        }
        if (securityMode == SecurityMode.HYBRID && (!whitelistEnabled || !proxyProtectionEnabled)) {
            plugin.getLogger().warning("Mode is HYBRID but one or more protections are disabled.");
        }
    }

    // ────────────────────────────────────────────────
    // Getters
    // ────────────────────────────────────────────────

    public SecurityMode getSecurityMode() {
        return securityMode;
    }

    public boolean isWhitelistEnabled() {
        return whitelistEnabled;
    }

    public List<String> getWhitelistedIps() {
        return new ArrayList<>(whitelistedIps);
    }

    public boolean isProxyProtectionEnabled() {
        return proxyProtectionEnabled;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public int getSessionExpirySeconds() {
        return sessionExpirySeconds;
    }

    public boolean isHostnameEnabled() {
        return hostnameEnabled;
    }

    public List<String> getAllowedHostnames() {
        return new ArrayList<>(allowedHostnames);
    }

    public boolean isUseReverseDns() {
        return useReverseDns;
    }

    public String getServerPublicIp() {
        return serverPublicIp;
    }

    public String getHostnameKickMessage() {
        return hostnameKickMessage;
    }

    public boolean shouldLogBlockedConnections() {
        return logBlockedConnections;
    }

    public boolean shouldLogAllowedConnections() {
        return logAllowedConnections;
    }

    public boolean shouldLogHostnameDetails() {
        return logHostnameDetails;
    }

    /**
     * Available security modes
     */
    public enum SecurityMode {
        WHITELIST_ONLY,
        PROXY_PROTECTED,
        HYBRID,
        HOSTNAME_ONLY
    }
}