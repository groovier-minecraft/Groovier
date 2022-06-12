package com.ericlam.mc.groovier.scriptloaders

import com.ericlam.mc.groovier.ScriptValidator

interface CommandRegister extends ScriptValidator {

    void register(Map<String, Object> commandScripts);

    void unregister(Set<String> commandScripts);

}
