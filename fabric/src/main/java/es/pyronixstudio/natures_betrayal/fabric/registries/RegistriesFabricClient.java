package es.pyronixstudio.natures_betrayal.fabric.registries;

import es.pyronixstudio.natures_betrayal.common.registries.Registries;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class RegistriesFabricClient {

    public static void register() {
        Registries.ENTITY_RENDERER.forEachEntity(EntityRendererRegistry::register);
    }
}
