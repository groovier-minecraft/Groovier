package com.ericlam.mc.groovier;

public interface ScriptValidator {

    void validate(Class<?> scriptClass) throws ValidateFailedException;

}
