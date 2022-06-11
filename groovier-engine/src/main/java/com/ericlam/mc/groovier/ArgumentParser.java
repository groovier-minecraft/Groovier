package com.ericlam.mc.groovier;

import com.google.inject.TypeLiteral;

import java.lang.reflect.Type;
import java.util.function.Function;

public interface ArgumentParser {

    <T> T parse(Type type, String arg) throws ArgumentParseException;

    <T> T parse(TypeLiteral<T> typeLiteral, String arg) throws ArgumentParseException;
}
