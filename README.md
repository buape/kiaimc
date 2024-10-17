<h1 align="center">KiaiMC</h1>

<p align="center">

![Modrinth Downloads](https://img.shields.io/modrinth/dt/KiaiMC?style=for-the-badge)
![Modrinth Game Versions](https://img.shields.io/modrinth/game-versions/KiaiMC?style=for-the-badge)
![Modrinth Version](https://img.shields.io/modrinth/v/KiaiMC?style=for-the-badge)

</p>

KiaiMC is a simple plugin that enables players to receive XP in [Kiai](https://kiai.app) for their messages sent in-game.

There are three ways that players can receive XP

1. Players can send messages in-game that will be converted into XP, just like Discord messages would
2. Every X seconds, the user will receive a fixed amount of XP.
3. Every X seconds, the user will receive a "bonus message", where they get the same amount of XP calculated as they would have received if they had sent a message, based on your settings in Kiai on Discord.

This plugin uses [DiscordSRV](https://modrinth.com/plugin/discordsrv) as the linking system between your Minecraft server and Discord server, so make sure that you have it installed and setup using the `linking.yml` file in DiscordSRV's configuration, and have a chat channel set within Discord.

You can use the `global` channel you setup in DiscordSRV to add any multipliers or blacklists to in-game chat. User and role blacklists will be applied based on the Minecraft player's linked Discord account.

## Installation

Installation of KiaiMC is simple. Drop the .jar file into your server's plugins folder, and restart the server. A config.yml file will be generated, and you can place an API token in there.

You can get a token to use for your application by joining [our support server](https://discord.gg/ZmzdX8pKUW) and posting in the #kiai-support channel there.
We are working on a web dashboard to make this process automated, but in the meantime, you will need to provide the following:

- A unique ID (typically your bot's ID)
- An application name
