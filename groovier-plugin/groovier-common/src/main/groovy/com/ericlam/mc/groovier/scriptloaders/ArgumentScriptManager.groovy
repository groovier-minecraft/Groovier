package com.ericlam.mc.groovier.scriptloaders

import com.ericlam.mc.groovier.*
import com.google.inject.TypeLiteral

import javax.inject.Inject
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function

class ArgumentScriptManager implements ScriptLoader, ArgumentParser, ScriptValidator {

    private final Map<Type, Class<?>> parserMap = new ConcurrentHashMap<>()
    private final Map<Type, Function<String, ?>> parserCache = new ConcurrentHashMap<>()

    @Inject
    private ScriptPlugin plugin

    @Inject
    private ServiceInjector provider;

    @Inject
    private ScriptCacheManager cacheManager;

    private final GroovierAPI api = GroovierCore.api


    @Override
    void unload() {
        this.parserMap.clear()
        this.parserCache.clear()
    }

    @Override
    void load(GroovyClassLoader classLoader) {
        File argFolder = new File(plugin.getPluginFolder(), "arguments")
        var files = argFolder.listFiles()

        if (files == null) {
            plugin.getLogger().info("No argument scripts found.")
            return
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".groovy")) {
                try {
                    Class<?> script = cacheManager.getScriptOrLoad(file, classLoader)
                    this.validate(script)
                    var applier = script.getMethod("apply", String.class)
                    var type = applier.getGenericReturnType()
                    this.parserMap.put(type, script)
                } catch (ValidateFailedException e) {
                    plugin.getLogger().severe("Argument script '${file.getName()}' validation failed: ${e.getMessage()}")
                } catch (Exception e) {
                    e.printStackTrace()
                    plugin.getLogger().severe("Argument script ${file.getName()} failed to load: ${e.getMessage()}")
                }
            }
        }
    }

    @Override
    void afterLoad() {
        var injectorReady = api.serviceInjector.get()
        if (injectorReady.isEmpty()) {
            throw new IllegalStateException("Service injector is not ready")
        }
        var injector = injectorReady.get()
        parserMap.forEach((type, script) -> {
            var parser = injector.getInstance(script as Class<Object>) as Function<String, ?>
            parserCache.put(type, parser)
        })
    }

    @Override
    <T> T parse(Type type, String arg) throws ArgumentParseException {
        return (T) this.parse(TypeLiteral.get(type), arg)
    }

    @Override
    <T> T parse(TypeLiteral<T> literal, String arg) throws ArgumentParseException {
        var parser = this.parserCache.get(literal.type) as Function<String, T>
        if (parser == null) {
            try {
                return (T) arg.asType(literal.rawType)
            }catch(Exception ignored){
                throw new ArgumentParseException("your input '${arg}' is not appliable to type ${literal.rawType.simpleName}!")
            }
        }
        try {
            return parser.apply(arg)
        } catch (ArgumentParseException e) {
            throw e
        } catch (Exception e) {
            throw new ArgumentParseException(e.getMessage())
        }
    }

    @Override
    void validate(Class<?> scriptClass) throws ValidateFailedException {
        try {
            var method = scriptClass.getMethod("apply", String.class)
            if (method.returnType == void.class || method.returnType == Void.class) {
                throw new ValidateFailedException("return type must be a non-void type")
            }
        } catch (NoSuchMethodException | SecurityException ignored) {
            throw new ValidateFailedException("cannot find public method apply(String)")
        }
    }
}
