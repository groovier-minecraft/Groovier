package com.ericlam.mc.groovier.scriptloaders

interface GroovierLifeCycle {

    void onEnable();

    void onDisable();

    void onScriptLoad()

    void onScriptUnload()
}
