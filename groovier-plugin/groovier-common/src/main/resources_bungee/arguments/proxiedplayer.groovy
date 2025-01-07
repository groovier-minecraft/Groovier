package arguments

import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer

ProxiedPlayer apply(String arg){
    return ProxyServer.instance.getPlayer(arg)
}

