package com.ericlam.mc.groovytest

import com.ericlam.mc.groovier.GroovierCore;
import java.util.function.Function;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestPluginOrder {

    @Test
    void testPluginOrder() {
        Assertions.assertThrows(IllegalStateException.class, GroovierCore::getApi);
    }

}
