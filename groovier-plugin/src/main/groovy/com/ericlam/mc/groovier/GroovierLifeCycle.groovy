package com.ericlam.mc.groovier

interface GroovierLifeCycle {

    void onLoad(ScriptPlugin plugin);

    void onEnable(ScriptPlugin plugin);

    void onDisable(ScriptPlugin plugin);

}
