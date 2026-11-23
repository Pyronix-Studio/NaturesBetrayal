package es.pyronixstudio.natures_betrayal.common.registries;

public class RegistryEntry<T, K> {

    private final String name;
    private T value;
    private K registered;

    public RegistryEntry(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        if (value == null)
            throw new IllegalStateException("Entry Already Registered can get original value: " + name);
        return value;
    }

    public K get() {
        if (registered == null)
            throw new IllegalStateException("Entry not registered: " + name);

        return registered;
    }

    public void setRegistered(K registered) {
        if (this.registered != null)
            throw new IllegalStateException("Entry already registered: " + name);
        value = null;
        this.registered = registered;
    }
}