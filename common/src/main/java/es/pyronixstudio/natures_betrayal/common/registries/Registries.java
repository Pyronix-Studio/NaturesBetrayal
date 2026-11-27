package es.pyronixstudio.natures_betrayal.common.registries;

public class Registries {

    public static EntityTypeRegistry ENTITY_TYPES;
    public static EntityAttributeRegistry ENTITY_ATTRIBUTES;
    public static EntityRendererRegistry ENTITY_RENDERER;
    public static KeysRegistry KEYS;

    private boolean loaded = false;

    public Registries() {
    }

    public void load() {
        if (loaded) return;
        ENTITY_TYPES = new EntityTypeRegistry();
        ENTITY_ATTRIBUTES = new EntityAttributeRegistry();
        ENTITY_RENDERER = new EntityRendererRegistry();
        KEYS = new KeysRegistry();
        loaded = true;
    }
}
