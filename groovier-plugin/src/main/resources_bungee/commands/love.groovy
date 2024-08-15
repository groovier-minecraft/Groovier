package commands

import com.ericlam.mc.groovier.command.CommandArg
import com.ericlam.mc.groovier.command.CommandScript
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent


// showcase for tab complete

@CommandScript(description = 'love what?')
void love(CommandSender sender, @CommandArg('target') String target) {
    sender.sendMessage(TextComponent.fromLegacy("oh, you love $target"))
}

List<String> tabComplete(CommandSender sender, String[] args) {
    return List.of("apple", "banana", "cake", "orange")
}