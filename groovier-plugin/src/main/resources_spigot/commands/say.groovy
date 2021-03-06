package commands

import com.ericlam.mc.groovier.command.CommandScript
import com.ericlam.mc.groovier.command.CommandArg
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender


// showcase for multi args

@CommandScript(description = 'say a message', permission = 'hello.say')
void say(CommandSender sender, @CommandArg('message') String[] message) {
    Bukkit.broadcastMessage("${sender.name} says: ${String.join(' ', message)}")
}