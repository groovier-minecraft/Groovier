package commands

import com.ericlam.mc.groovier.command.CommandArg
import com.ericlam.mc.groovier.command.CommandScript
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer

// showcase for custom argument parsing

@CommandScript(description = 'check player info')
void info(CommandSender sender, @CommandArg(value = 'player', optional = true) ProxiedPlayer player = null){

    if (player == null && sender instanceof ProxiedPlayer) {
        player = (ProxiedPlayer)sender
    } else if (player == null) {
        sender.sendMessage(TextComponent.fromLegacy("Please specify a player"))
        return
    }

    sender.sendMessage(TextComponent.fromLegacy("Player: ${player.name}"))
    sender.sendMessage(TextComponent.fromLegacy("UUID: ${player.uniqueId}"))
    sender.sendMessage(TextComponent.fromLegacy("Ping: ${player.ping}"))
}

