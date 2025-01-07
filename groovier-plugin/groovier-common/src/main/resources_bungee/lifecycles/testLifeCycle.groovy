package lifecycles

import com.ericlam.mc.groovier.lifecycle.OnDisable
import com.ericlam.mc.groovier.lifecycle.OnEnable
import com.ericlam.mc.groovier.lifecycle.OnScriptLoad
import com.ericlam.mc.groovier.lifecycle.OnScriptUnload
import groovy.transform.Field
import net.md_5.bungee.api.plugin.Plugin

import javax.inject.Inject


@Field @Inject Plugin plugin

@OnEnable
void printHelloWorld() {
    plugin.logger.info("hello world! this is a groovy script printed when enable")
}

@OnDisable
void printGoodBye(){
    plugin.logger.info("good bye! this is a groovy script printed when disable")
}

@OnScriptLoad
void scriptLoaded(){
    plugin.logger.info("script loaded! printed from groovy script")
}

@OnScriptUnload
void scriptUnloaded(){
    plugin.logger.info("script unloaded! printed from groovy script")
}
