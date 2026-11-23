package es.pyronixstudio.natures_betrayal.neoforge.registries;

import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.mobs.CustomPhatomRenderer;
import es.pyronixstudio.natures_betrayal.common.registries.RegistryEntry;
import es.pyronixstudio.natures_betrayal.neoforge.NaturesBetrayalNeoforge;
import net.minecraft.client.renderer.entity.PhantomRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RegistriesNeoforge {

    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, NaturesBetrayal.MOD_ID);

    public static void register(IEventBus modBus) {
        NaturesBetrayal.registries.ENTITY_TYPES.forEach(RegistriesNeoforge::registerEntity);
        ENTITY_TYPES.register(modBus);

        modBus.addListener(RegistriesNeoforge::registerAttributes);
        modBus.addListener(RegistriesNeoforge::registerRenderers);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        NaturesBetrayal.registries.ENTITY_ATTRIBUTES.forEach((type, attributes) -> event.put(type, attributes.build()));
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(NaturesBetrayal.registries.ENTITY_TYPES.CUSTOM_PHANTOM.get(), CustomPhatomRenderer::new);
    }

    private static <T extends Entity> void registerEntity(String name, RegistryEntry<EntityType.Builder<T>, EntityType<T>> entry) {
        ENTITY_TYPES.register(name, () -> {
            EntityType<T> type = entry.getValue().build(name);
            entry.setRegistered(type);
            return type;
        });
    }


}