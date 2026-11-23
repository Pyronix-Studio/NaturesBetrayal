package es.pyronixstudio.natures_betrayal.common.registries;

public class Registries {

    public  EntityTypeRegistry ENTITY_TYPES;
    public  EntityAttributeRegistry ENTITY_ATTRIBUTES;
    private boolean loaded = false;
    public Registries(){}

    public void load(){
        if(loaded) return;
        ENTITY_TYPES = new EntityTypeRegistry();
        ENTITY_ATTRIBUTES = new EntityAttributeRegistry();
        loaded = true;
    }
}
