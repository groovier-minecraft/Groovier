package spigot.arguments


import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

OfflinePlayer apply(String arg) {
    return Bukkit.getOfflinePlayer(arg)
}

