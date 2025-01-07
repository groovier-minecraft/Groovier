package com.ericlam.mc.groovier

import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

class GroovierCacheManager implements ScriptCacheManager {

    private final Map<String, Class<?>> scriptCacheMap = new ConcurrentHashMap<>()
    private final Set<String> cached = new HashSet<>()

    @Override
    boolean isCached(File content) throws IOException {
        return false // let it load by groovy class loader
    }

    @Override
    boolean isCached(String content) {
        return this.scriptCacheMap.containsKey(generateMD5(content))
    }

    @Override
    Class<?> getScriptOrLoad(String content, GroovyClassLoader classLoader) throws Exception {
        var md5 = generateMD5(content)
        cached.add(md5)
        if (this.scriptCacheMap.containsKey(md5)) {
            return this.scriptCacheMap.get(md5)
        }

        var script = classLoader.parseClass(content)
        this.scriptCacheMap.put(md5, script)

        return script
    }

    @Override
    Class<?> getScriptOrLoad(File file, GroovyClassLoader classLoader) throws Exception {
        return classLoader.parseClass(file) // groovy class loader already cached the script
    }

    private static def generateMD5(String s){
        MessageDigest.getInstance("MD5").digest(s.bytes).encodeHex().toString()
    }


    void flush(){
        scriptCacheMap.removeAll { !cached.contains(it.key) }
        cached.clear()
    }

}
