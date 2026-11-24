package es.pyronixstudio.natures_betrayal.common.registries;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class DeferredRegistry<B, R> {

    public static final class RegistryObject<B, R> {

        private final String name;
        private final Supplier<B> factory;
        private R value;

        private RegistryObject(String name, Supplier<B> factory) {
            this.name = name;
            this.factory = factory;
        }

        public String getName() {
            return name;
        }

        public B create() {
            return factory.get();
        }

        public R get() {
            if (value == null)
                throw new IllegalStateException("Registry object not registered yet: " + name);
            return value;
        }

        public boolean isRegistered() {
            return value != null;
        }

        public void setRegistered(R value) {
            if (this.value != null)
                throw new IllegalStateException("Registry object already registered: " + name);
            if (value == null)
                throw new IllegalArgumentException("Registered value cannot be null: " + name);
            this.value = value;
        }
    }

    private final Map<String, RegistryObject<B, R>> objects = new LinkedHashMap<>();

    public RegistryObject<B, R> register(String name, Supplier<B> factory) {
        if (objects.containsKey(name))
            throw new IllegalStateException("Duplicate registry object: " + name);
        RegistryObject<B, R> object = new RegistryObject<>(name, factory);
        objects.put(name, object);
        return object;
    }

    public void forEach(BiConsumer<String, RegistryObject<B, R>> consumer) {
        objects.forEach(consumer);
    }
}
