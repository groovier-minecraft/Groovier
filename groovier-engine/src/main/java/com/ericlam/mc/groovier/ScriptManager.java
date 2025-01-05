package com.ericlam.mc.groovier;

import java.util.concurrent.CompletableFuture;

/**
 * script manager
 */
public interface ScriptManager {

    /**
     * reload all scripts
     * @return future
     */
    CompletableFuture<Void> reloadAllScripts();

    /**
     * reload a specific script loader
     * @param loader script loader class
     * @return future
     */
    CompletableFuture<Void> reloadScript(Class<? extends ScriptLoader> loader);


}
