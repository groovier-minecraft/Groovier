package com.ericlam.mc.groovier;

import com.google.inject.TypeLiteral;

import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * mainly use for command argument
 */
public interface ArgumentParser {

    /**
     *
     * @param type argument type
     * @param arg argument string
     * @return argument value
     * @param <T> argument type
     * @throws ArgumentParseException if argument parse failed
     */
    <T> T parse(Type type, String arg) throws ArgumentParseException;

    /**
     *
     * @param typeLiteral argument type
     * @param arg argument string
     * @return argument value
     * @param <T> argument type
     * @throws ArgumentParseException if argument parse failed
     */
    <T> T parse(TypeLiteral<T> typeLiteral, String arg) throws ArgumentParseException;
}
