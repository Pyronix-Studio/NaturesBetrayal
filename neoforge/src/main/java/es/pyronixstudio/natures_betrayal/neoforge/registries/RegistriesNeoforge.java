package es.pyronixstudio.natures_betrayal.neoforge.registries;

import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import es.pyronixstudio.natures_betrayal.common.registries.DeferredRegistry.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RegistriesNeoforge {

    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, NaturesBetrayal.MOD_ID);

    public static void register(IEventBus modBus) {
        NaturesBetrayal.registries.ENTITY_TYPES.forEachEntity(RegistriesNeoforge::registerEntity);
        ENTITY_TYPES.register(modBus);

        modBus.addListener(RegistriesNeoforge::registerAttributes);
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
