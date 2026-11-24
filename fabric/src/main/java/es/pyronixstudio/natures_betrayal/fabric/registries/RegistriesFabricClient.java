package es.pyronixstudio.natures_betrayal.fabric.registries;

import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class RegistriesFabricClient {

    public static void register() {
        NaturesBetrayal.registries.ENTITY_RENDERER.forEachEntity(EntityRendererRegistry::register);
    }
}
