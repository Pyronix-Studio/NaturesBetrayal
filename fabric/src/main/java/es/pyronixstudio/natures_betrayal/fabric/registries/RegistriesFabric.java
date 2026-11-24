package es.pyronixstudio.natures_betrayal.fabric.registries;

import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import es.pyronixstudio.natures_betrayal.common.registries.DeferredRegistry.RegistryObject;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class RegistriesFabric {

    public static void register() {
        NaturesBetrayal.registries.ENTITY_TYPES.forEachEntity(RegistriesFabric::registerEntity);
        NaturesBetrayal.registries.ENTITY_ATTRIBUTES.forEach(FabricDefaultAttributeRegistry::register);
    }

    private static <T extends Entity> void registerEntity(String name, RegistryObject<EntityType.Builder<T>, EntityType<T>> object) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(NaturesBetrayal.MOD_ID, name);
        EntityType<T> type = Registry.register(BuiltInRegistries.ENTITY_TYPE, id, object.create().build(id.toString()));
        object.setRegistered(type);
    }
}
