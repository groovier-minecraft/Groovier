package com.ericlam.mc.groovier;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * for command scripts
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandScript {

    /**
     * command description
     * @return command description
     */
    String description() default "";

    /**
     * command permission if any
     * @return permission
     */
    String permission() default "";

}

