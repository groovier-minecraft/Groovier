package arguments

import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo


ServerInfo apply(String arg){
    return ProxyServer.instance.getServerInfo(arg)
}

