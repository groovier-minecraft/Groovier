package com.ericlam.mc.groovier;

import com.ericlam.mc.groovier.providers.ArgumentParserProvider;
import com.ericlam.mc.groovier.providers.GroovierLifeCycleProvider;
import com.ericlam.mc.groovier.providers.ServiceInjectorProvider;
import com.ericlam.mc.groovier.scriptloaders.*;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

import org.jetbrains.annotations.NotNull;

import javax.inject.Provider;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public abstract class GroovierCore implements GroovierAPI, GroovierAddon {

    protected final GroovierModule groovierModule = new GroovierModule();
    private static GroovierAPI api;

    public GroovierCore() {
        api = this;

        this.bindInstance(GroovierAPI.class, this);
        this.addScriptLoader(ServiceScriptsManager.class);
        this.addScriptLoader(CommandScriptsManager.class);
        this.addScriptLoader(EventScriptsManager.class);
        this.addScriptLoader(ArgumentScriptManager.class);
        this.addScriptLoader(LifeCycleScriptsManager.class);
        this.bindType(ScriptManager.class, GroovierScriptLoader.class);
        this.bindProvider(ArgumentParser.class, ArgumentParserProvider.class);
        this.bindProvider(ServiceInjector.class, ServiceInjectorProvider.class);
        this.bindProvider(GroovierLifeCycle.class, GroovierLifeCycleProvider.class);
    }

    public static GroovierAPI getApi() {
        return Optional.ofNullable(api).orElseThrow(() -> new IllegalStateException("groovier not initialized"));
    }

    private Injector injector;
    private GroovierScriptLoader loader;
    private ScriptPlugin plugin;

    private GroovierLifeCycle lifeCycle;


    protected void onLoad(ScriptPlugin plugin) {
        this.plugin = plugin;
        groovierModule.bindScriptPlugin(plugin);
        plugin.copyResources();
    }


    protected void onEnable(Injector injector) {
        this.injector = injector;
        loader = injector.getInstance(GroovierScriptLoader.class);
        loader.addClassPath();
        loader.loadAllScripts().whenComplete((v, e) -> {
            if (e != null) {
                plugin.getLogger().log(Level.SEVERE, e, () -> "error while loading scripts: " + e.getMessage());
            }
            lifeCycle = injector.getInstance(GroovierLifeCycle.class);
            plugin.runSyncTask(() -> lifeCycle.onEnable());
        });
    }

    public void onDisable() {
        lifeCycle.onDisable();
        loader.unloadAllScripts();
    }

    public CompletableFuture<Void> reloadAllScripts() {
        return loader.reloadAllScripts();
    }

    public CompletableFuture<Void> reloadScript(Class<? extends ScriptLoader> script) {
        return loader.reloadScript(script);
    }

    public Set<ScriptLoader> getScriptLoaders() {
        return this.injector.getInstance(new Key<>() {
        });
    }

    @Override
    public void addScriptLoader(Class<? extends ScriptLoader> scriptLoader) {
        this.groovierModule.addReloadable(scriptLoader);
    }

    @Override
    public <T extends ScriptValidator> void bindRegisters(Class<T> validator, T ins) {
        this.groovierModule.bindRegisters(validator, ins);
    }

    @Override
    public <T> void bindInstance(Class<T> type, T ins) {
        this.groovierModule.bindInstance(type, ins);
    }

    @Override
    public <T, V extends T> void bindType(Class<T> type, Class<V> clazz) {
        this.groovierModule.bindType(type, clazz);
    }

    @Override
    public <T, P extends Provider<T>> void bindProvider(Class<T> type, Class<P> clazz) {
        this.groovierModule.bindProvider(type, clazz);
    }

    @Override
    public void installModule(Module module) {
        this.groovierModule.installModule(module);
    }

    @Override
    public void extractFromJar(Class<?> jar, String sourceFolder, String targetFolder) throws URISyntaxException, IOException {

        if (!plugin.isCopyDefaults()) return;

        URL url = jar.getResource("");

        if (url == null) {
            throw new IllegalStateException("can't find resource inside jar");
        }

        URI resource = url.toURI();

        try (FileSystem fileSystem = FileSystems.newFileSystem(
                resource,
                Collections.<String, String>emptyMap()
        )) {

            final Path target = new File(plugin.getPluginFolder(), targetFolder).toPath();
            final Path jarPath = fileSystem.getPath(sourceFolder);

            Files.walkFileTree(jarPath, new SimpleFileVisitor<>() {

                @Override
                public @NotNull FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path currentTarget = target.resolve(jarPath.relativize(dir).toString());
                    if (Files.notExists(currentTarget)) Files.createDirectories(currentTarget);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public @NotNull FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    var targetPath = target.resolve(jarPath.relativize(file).toString());
                    if (Files.notExists(targetPath)) Files.copy(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }

            });

        }
    }

    @Override
    public Injector getBaseInjector() {
        return Optional.ofNullable(injector).orElseThrow(() -> new IllegalStateException("groovier not initialized"));
    }

    @Override
    public ServiceInjector getServiceInjector() {
        return getBaseInjector().getInstance(ServiceInjector.class);
    }

    @Override
    public ArgumentParser getArgumentParser() {
        return getBaseInjector().getInstance(ArgumentParser.class);
    }

    public void copyFromJar(String source) throws URISyntaxException, IOException {
        extractFromJar(getClass(), source, "");
    }
}
