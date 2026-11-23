package es.pyronixstudio.natures_betrayal.neoforge;

import es.pyronixstudio.natures_betrayal.common.IModLoader;
import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import es.pyronixstudio.natures_betrayal.common.registries.Registries;
import es.pyronixstudio.natures_betrayal.neoforge.registries.RegistriesNeoforge;
import net.minecraft.client.renderer.entity.PhantomRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

import java.nio.file.Path;


@Mod(NaturesBetrayal.MOD_ID)
public class NaturesBetrayalNeoforge implements IModLoader {

    public NaturesBetrayalNeoforge(IEventBus modEventBus, ModContainer modContainer) {
        NaturesBetrayal.initialize(this);
        NaturesBetrayal.onFinishPreInitialization();
        RegistriesNeoforge.register(modEventBus);
    }

    @Override
    public Path getConfigBasePath() {
        return FMLPaths.CONFIGDIR.get();
    }

}
