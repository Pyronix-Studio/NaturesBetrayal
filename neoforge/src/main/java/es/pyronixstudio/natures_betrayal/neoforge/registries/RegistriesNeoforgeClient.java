package es.pyronixstudio.natures_betrayal.neoforge.registries;

import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class RegistriesNeoforgeClient {

    public static void register(IEventBus eventBus) {
        eventBus.addListener(RegistriesNeoforgeClient::registerRenderers);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        NaturesBetrayal.registries.ENTITY_RENDERER.forEachEntity(event::registerEntityRenderer);
    }
}
