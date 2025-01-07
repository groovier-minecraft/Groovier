package com.ericlam.mc.groovytest

import com.ericlam.mc.groovier.MCGroovierCore
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestPluginOrder {

    @Test
    void testPluginOrder() {
        Assertions.assertThrows(IllegalStateException.class, MCGroovierCore::getApi);
    }

}
