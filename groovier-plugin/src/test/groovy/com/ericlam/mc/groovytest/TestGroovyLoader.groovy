package com.ericlam.mc.groovytest

import org.junit.jupiter.api.Test

class TestGroovyLoader {

    private final GroovyClassLoader classLoader = new GroovyClassLoader(getClass().getClassLoader())

    @Test
    void testGroovyLoadDependencies() {
        classLoader.addClasspath(new File("src/test/resources").path)
        println classLoader.getURLs().collect { it.toString() }
        println classLoader.getLoadedClasses()
        //def serviceBCls = classLoader.parseClass(new File("src/test/resources/services/ServiceB.groovy"))
        //def serviceACls = classLoader.parseClass(new File("src/test/resources/services/ServiceA.groovy"))
        def mainCls = classLoader.parseClass(new File("src/test/resources/main/main.groovy"))
        println classLoader.getLoadedClasses()
        def main = mainCls.getConstructor().newInstance()
        main.main()
    }
}
