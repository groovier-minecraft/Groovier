package arguments

import com.ericlam.mc.groovier.ArgumentParseException
import org.bukkit.Bukkit
import org.bukkit.entity.Player


Player apply(String arg){
    var player = Bukkit.getPlayer(arg)
    if (player == null){
        throw new ArgumentParseException("player ${arg} is not online or not exist.")
    }
    return player
}

