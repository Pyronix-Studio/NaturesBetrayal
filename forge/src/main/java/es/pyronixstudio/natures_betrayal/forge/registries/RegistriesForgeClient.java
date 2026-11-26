package es.pyronixstudio.natures_betrayal.forge.registries;

import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NaturesBetrayal.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistriesForgeClient {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        NaturesBetrayal.registries.ENTITY_RENDERER.forEachEntity(event::registerEntityRenderer);
    }
}
