package es.pyronixstudio.natures_betrayal.neoforge.registries;

import es.pyronixstudio.natures_betrayal.common.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class RegistriesNeoforgeClient {

    public static void register(IEventBus eventBus) {
        eventBus.addListener(RegistriesNeoforgeClient::registerRenderers);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        Registries.ENTITY_RENDERER.forEachEntity(event::registerEntityRenderer);
    }
}
