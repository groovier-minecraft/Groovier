package com.ericlam.mc.groovytest

import java.util.function.Function

class TestGroovy {

    static void main(String[] args) throws Exception {
        Function<String, String> f = MyFunction.class.newInstance() as Function<String, String>
        print(f.apply("awdawda"))

        var m = A.class.getMethod("abc", List.class, String.class)
        println(m.genericParameterTypes.toArrayString())
        println(m.parameterTypes.toArrayString())

        println("awda" as Boolean)
        println(0 as Boolean)
        println(null as Boolean)
        println("" as Boolean)
        println(1 as Boolean)


        println(Set.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10).sort { v -> v}.toList())
    }

    static class A {

        void abc(List<String> a, String b){
        }

    }


    static class MyFunction {
        int apply(String arg){
            try {
                return arg as Double
            } catch (NumberFormatException ignored) {
                return 0.0
            }
        }
    }
}
