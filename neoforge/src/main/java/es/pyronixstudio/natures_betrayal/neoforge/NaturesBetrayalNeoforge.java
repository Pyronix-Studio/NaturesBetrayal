package es.pyronixstudio.natures_betrayal.neoforge;

import es.pyronixstudio.natures_betrayal.common.IModLoader;
import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;


@Mod(NaturesBetrayal.MOD_ID)
public class NaturesBetrayalNeoforge implements IModLoader {

    public NaturesBetrayalNeoforge(IEventBus modEventBus, ModContainer modContainer) {
        NaturesBetrayal.initialize(this);
        NaturesBetrayal.onFinishPreInitialization();
    }

    @Override
    public Path getConfigBasePath() {
        return FMLPaths.CONFIGDIR.get();
    }
}
