# DiscordChatBridge

Bridges chat between Discord and a Hytale game server.

## Installation

Drop the JAR into your server's `plugins` folder, start the server, edit `config.json` with your bot token and channel ID, then restart.

## Placeholders

| Message | Placeholders |
|---------|-------------|
| Player join/leave | `%player%` |
| World enter/leave/change | `%player%` `%world%` `%from%` `%to%` |
| Player kill | `%killer%` `%victim%` `%player%` `%cause%` `%item%` `%projectile%` |
| Player death | `%player%` `%cause%` |
| Zone discovery | `%player%` `%zone%` `%region%` |
