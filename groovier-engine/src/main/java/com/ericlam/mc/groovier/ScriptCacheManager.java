package com.ericlam.mc.groovier;

import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.IOException;

/**
 * a script cache manager
 */
public interface ScriptCacheManager {

    /**
     * check if the script is cached (with md5 hash mapping)
     * @param content script content
     * @return true if cached
     * @throws IOException error while reading text
     */
    boolean isCached(File content) throws IOException;

    /**
     * check if the script is cached (with md5 hash mapping)
     * @param content script content
     * @return true if cached
     */
    boolean isCached(String content);

    /**
     * get the script class from cache, if not cached, load it and cache it
     * @param content script content
     * @param classLoader groovy class loader
     * @return script class
     * @throws Exception error while loading script
     */
    Class<?> getScriptOrLoad(String content, GroovyClassLoader classLoader) throws Exception;

    /**
     * get the script class from cache, if not cached, load it and cache it
     * @param file script file
     * @param classLoader groovy class loader
     * @return script class
     * @throws Exception error while loading script
     */
    Class<?> getScriptOrLoad(File file, GroovyClassLoader classLoader) throws Exception;

}