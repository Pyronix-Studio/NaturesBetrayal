package es.pyronixstudio.natures_betrayal.common.registries;

public class Registries {

    public EntityTypeRegistry ENTITY_TYPES;
    public EntityAttributeRegistry ENTITY_ATTRIBUTES;
    public EntityRendererRegistry ENTITY_RENDERER;

    private boolean loaded = false;

    public Registries() {
    }

    public void load() {
        if (loaded) return;
        ENTITY_TYPES = new EntityTypeRegistry();
        ENTITY_ATTRIBUTES = new EntityAttributeRegistry();
        ENTITY_RENDERER = new EntityRendererRegistry();
        loaded = true;
    }
}
