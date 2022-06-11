package com.ericlam.mc.groovier.relodables

import com.ericlam.mc.groovier.ScriptValidator

interface CommandRegister extends ScriptValidator {

    void register(Map<String, Object> commandScripts);

    void unregister(Set<String> commandScripts);

}
