package commands

import com.ericlam.mc.groovier.command.CommandArg
import com.ericlam.mc.groovier.command.CommandScript
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


// showcase for custom argument parsing


@CommandScript(description = 'check player info')
void checkInfo(CommandSender sender, @CommandArg(value = 'player', optional = true) Player player){

    if (player == null && sender instanceof Player){
        player = (Player)sender
    } else if (player == null){
        sender.sendMessage("Please specify a player")
        return
    }

    sender.sendMessage("Player: ${player.name}")
    sender.sendMessage("UUID: ${player.uniqueId}")
    sender.sendMessage("GameMode: ${player.gameMode}")
}

