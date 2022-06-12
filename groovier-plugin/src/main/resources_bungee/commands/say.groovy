package commands

import com.ericlam.mc.groovier.command.CommandArg
import com.ericlam.mc.groovier.command.CommandScript
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent


// showcase for multi args

@CommandScript(description = 'say something')
void say(CommandSender sender, @CommandArg('message') String[] message) {
    sender.sendMessage(TextComponent.fromLegacyText("${sender.name} says: ${String.join(' ', message)}"))
}