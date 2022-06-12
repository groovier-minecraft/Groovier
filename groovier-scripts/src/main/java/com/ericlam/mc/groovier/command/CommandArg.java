package com.ericlam.mc.groovier.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * for command scripts
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandArg {

    /**
     * argument name
     * @return argument name
     */
    String value();

    /**
     * if argument is optional
     * @return if argument is optional
     */
    boolean optional() default false;

}
