package commands

import com.ericlam.mc.groovier.command.CommandArg
import com.ericlam.mc.groovier.command.CommandScript
import org.bukkit.command.CommandSender


// showcase for tab complete

@CommandScript(description = 'love what?')
void love(CommandSender sender, @CommandArg('target') String target) {
    sender.sendMessage("oh, you love $target")
}

List<String> tabComplete(CommandSender sender, String[] args) {
    return List.of("apple", "banana", "cake", "orange")
}