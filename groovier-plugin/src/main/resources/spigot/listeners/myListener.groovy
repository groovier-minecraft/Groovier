package spigot.listeners


import org.bukkit.event.EventHandler
import org.bukkit.event.player.AsyncPlayerChatEvent


@EventHandler
void onPlayerChat(AsyncPlayerChatEvent e) {
    if (e.message == "ping") {
        e.player.sendMessage("pong!")
    }
}