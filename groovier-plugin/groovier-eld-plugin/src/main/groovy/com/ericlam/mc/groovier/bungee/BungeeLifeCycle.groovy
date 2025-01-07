package com.ericlam.mc.groovier.bungee

import com.ericlam.mc.eld.bungee.ELDLifeCycle
import com.ericlam.mc.groovier.ELDGroovierCore
import com.ericlam.mc.groovier.GroovierAPI
import com.ericlam.mc.groovier.GroovierCore
import com.google.inject.Injector
import net.md_5.bungee.api.plugin.Plugin

import javax.inject.Inject

class BungeeLifeCycle implements ELDLifeCycle {

    @Inject
    private GroovierAPI core
    @Inject
    private Injector injector
    @Inject
    private Plugin plugin

    @Override
    void onEnable(Plugin plugin) {
        (core as ELDGroovierCore).onEnable(injector)
        plugin.proxy.pluginManager.registerCommand(plugin, new BungeeGroovierCommand(core as GroovierCore, plugin))
    }

    @Override
    void onDisable(Plugin plugin) {
        (core as ELDGroovierCore).onDisable()
    }

}
