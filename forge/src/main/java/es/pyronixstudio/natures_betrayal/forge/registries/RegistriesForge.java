package es.pyronixstudio.natures_betrayal.forge.registries;

import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import es.pyronixstudio.natures_betrayal.common.registries.DeferredRegistry.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;

public class RegistriesForge {

    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, NaturesBetrayal.MOD_ID);

    public static void register(IEventBus modBus) {
        NaturesBetrayal.registries.ENTITY_TYPES.forEachEntity(RegistriesForge::registerEntity);
        ENTITY_TYPES.register(modBus);
        modBus.addListener(RegistriesForge::registerAttributes);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        NaturesBetrayal.registries.ENTITY_ATTRIBUTES.forEach((type, attributes) -> event.put(type, attributes.build()));
    }

    private static <T extends Entity> void registerEntity(String name, RegistryObject<EntityType.Builder<T>, EntityType<T>> object) {
        ENTITY_TYPES.register(name, () -> {
            EntityType<T> type = object.create().build(name);
            object.setRegistered(type);
            return type;
        });
    }
}
