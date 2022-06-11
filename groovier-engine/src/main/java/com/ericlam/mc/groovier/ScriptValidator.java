package com.ericlam.mc.groovier;

/**
 * script validation
 */
public interface ScriptValidator {

    /**
     * validate script
     * @param scriptClass script class
     * @throws ValidateFailedException if validation failed
     */
    void validate(Class<?> scriptClass) throws ValidateFailedException;

}
