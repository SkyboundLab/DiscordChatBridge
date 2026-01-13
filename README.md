# Discord Chat Bridge

A Hytale server plugin that bridges chat between Discord and your game server.

## Features

- Relay player chat messages to Discord and vice versa
- Configurable event notifications (join, leave, death, kills, world changes)
- Customizable message templates
- Role colors displayed in-game
- Mention prevention (optional)

## Installation

1. Download the latest JAR and place it in your server's plugins folder.
2. Start the server to generate the default `config.json`.
3. Configure your Discord bot token and channel ID in `config.json`.
4. Restart the server.

## Configuration

| Option               | Description                                  |
|----------------------|----------------------------------------------|
| `Enabled`            | Enable or disable the plugin's functionality |
| `RelayGameToDiscord` | Send game chat to Discord                    |
| `RelayDiscordToGame` | Send Discord chat to in-game chat            |

### Discord Settings

| Option                  | Description                                                           |
|-------------------------|-----------------------------------------------------------------------|
| `BotToken`              | Your [Discord bot token](https://discord.com/developers/applications) |
| `ChannelId`             | The Discord channel ID to bridge                                      |
| `PresenceMessage`       | Bot status message                                                    |
| `IgnoreBotMessages`     | Ignore messages from other bots                                       |
| `IgnoreWebhookMessages` | Ignore webhook messages                                               |
| `AllowMentions`         | Allow @mentions from game chat                                        |

### Events

Toggle which events are broadcast to Discord:
- `ServerStart` / `ServerStop`
- `PlayerJoin` / `PlayerLeave`
- `WorldEnter` / `WorldLeave`
- `PlayerDeath` / `PlayerKill`

### Messages

Customize the message templates for each event. Available placeholders:
- `%player%` - Player name
- `%world%` - World name
- `%killer%` / `%victim%` - For kill events