package es.pyronixstudio.natures_betrayal.fabric;

import es.pyronixstudio.natures_betrayal.common.IModLoader;
import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;


public class NaturesBetrayalFabric implements ModInitializer, IModLoader {

    @Override
    public void onInitialize() {
        NaturesBetrayal.initialize(this);
        NaturesBetrayal.onFinishPreInitialization();
    }

    @Override
    public Path getConfigBasePath() {
        return FabricLoader.getInstance().getConfigDir();
    }
}