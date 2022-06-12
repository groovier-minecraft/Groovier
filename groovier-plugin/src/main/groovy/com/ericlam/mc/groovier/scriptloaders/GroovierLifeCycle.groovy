package com.ericlam.mc.groovier.scriptloaders

import com.ericlam.mc.groovier.ScriptPlugin

interface GroovierLifeCycle {

    void onEnable();

    void onDisable();

    void onScriptLoad()

    void onScriptUnload()
}
