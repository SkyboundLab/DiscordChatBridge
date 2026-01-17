# Discord Chat Bridge

A Hytale server plugin that bridges chat between Discord and your game server.

## Features

- Relay player chat messages to Discord and vice versa
- Configurable event notifications (join, leave, death, kills, world changes)
- Customizable message templates
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
| `WebhookUrl`            | Discord webhook URL for sending chat messages                         |
| `IgnoreBotMessages`     | Ignore messages from other bots                                       |
| `IgnoreWebhookMessages` | Ignore webhook messages                                               |
| `AllowMentions`         | Allow @mentions from game chat                                        |

### Events

Configure event notifications with nested options:

```json
"Events": {
  "ServerStart": {
    "Enable": true,
    "Embed": {
      "Enable": true,
      "Color": "#008000"
    },
    "Message": "Server has started"
  },
  ...
}
```

Each event has:
- `Enable`: Toggle the event on/off
- `Embed`: Configure embed display and color
- `Message`: Custom message template

Available placeholders:
- `%player%` - Player name
- `%world%` - World name
- `%killer%` / `%victim%` - For kill events

Discord messages to game are formatted as: `[Discord] username: message`