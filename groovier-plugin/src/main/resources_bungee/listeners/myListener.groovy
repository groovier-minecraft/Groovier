package listeners

import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.event.EventHandler


@EventHandler
void onPlayerChat(ChatEvent e){
    if (!(e.sender instanceof ProxiedPlayer)) return
    var player = e.sender as ProxiedPlayer
    if (e.message == 'ping'){
        e.cancelled = true
        player.sendMessage(TextComponent.fromLegacyText("pong!"))
    } else if (e.message == 'pong') {
        e.cancelled = true
        player.sendMessage(TextComponent.fromLegacyText("ping!"))
    }
}

