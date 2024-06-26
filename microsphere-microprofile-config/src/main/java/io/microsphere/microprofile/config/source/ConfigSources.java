package io.microsphere.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import static java.util.ServiceLoader.load;
import static java.util.stream.Stream.of;

public class ConfigSources implements Iterable<ConfigSource> {

    private boolean addedDefaultConfigSources;

    private boolean addedDiscoveredConfigSources;

    private SortedSet<ConfigSource> configSources = new ConcurrentSkipListSet<>(ConfigSourceOrdinalComparator.INSTANCE);

    private ClassLoader classLoader;

    public ConfigSources(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void addDefaultSources() {
        if (addedDefaultConfigSources) {
            return;
        }
        addConfigSources(JavaSystemPropertiesConfigSource.class,
                OperationSystemEnvironmentVariablesConfigSource.class,
                DefaultResourceConfigSources.class
        );
        addedDefaultConfigSources = true;
    }

    public void addDiscoveredSources() {
        if (addedDiscoveredConfigSources) {
            return;
        }

        addConfigSources(load(ConfigSource.class, classLoader));
        addedDiscoveredConfigSources = true;
    }

    public void addConfigSources(Class<? extends ConfigSource>... configSourceClasses) {
        addConfigSources(
                of(configSourceClasses)
                        .map(this::newInstance)
                        .toArray(ConfigSource[]::new)
        );
    }

    public void addConfigSources(ConfigSource... configSources) {
        addConfigSources(Arrays.asList(configSources));
    }

    public void addConfigSources(Iterable<ConfigSource> configSources) {
        configSources.forEach(this.configSources::add);
    }

    private ConfigSource newInstance(Class<? extends ConfigSource> configSourceClass) {
        ConfigSource instance = null;
        try {
            instance = configSourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        return instance;
    }

    @Override
    public Iterator<ConfigSource> iterator() {
        return configSources.iterator();
    }

    public boolean isAddedDefaultConfigSources() {
        return addedDefaultConfigSources;
    }

    public boolean isAddedDiscoveredConfigSources() {
        return addedDiscoveredConfigSources;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
