# RyxoNET

Advanced connection security plugin for Paper, Purpur, and Spigot servers.

Protect your backend from direct IP connections, proxy bypass attempts, IP spoofing, and enforce domain-only connections.

## Features

- 4 security modes: WHITELIST_ONLY, PROXY_PROTECTED, HYBRID, HOSTNAME_ONLY
- Force players to connect using your domain (with reverse DNS support)
- Server public IP binding check
- Powerful admin commands with tab completion
- Detailed configurable logging
- Clean, modular, and modern codebase (Java 17, Maven)

## Requirements

- Paper / Purpur / Spigot 1.19–1.21+
- Java 17+

## Installation

1. Download the latest release from [Releases](https://github.com/itzsepanta/RyxoNET/releases)
2. Put `RyxoNET.jar` in your server's `plugins` folder
3. Start/restart the server
4. Edit `plugins/RyxoNET/config.yml`
5. Use `/ryxonet reload` or restart

## Commands

| Command                | Description                          | Permission       |
|------------------------|--------------------------------------|------------------|
| /ryxonet reload        | Reload configuration                 | ryxonet.admin    |
| /ryxonet status        | Show current security status         | ryxonet.admin    |
| /ryxonet addhost       | Add allowed hostname                 | ryxonet.admin    |
| /ryxonet removehost    | Remove allowed hostname              | ryxonet.admin    |
| /ryxonet listhosts     | List all allowed hostnames           | ryxonet.admin    |

## Configuration

See [config.yml](https://github.com/yourusername/RyxoNET/blob/main/src/main/resources/config.yml) for all available options.

## Building from source

```bash
git clone https://github.com/yourusername/RyxoNET.git
cd RyxoNET
mvn clean package
