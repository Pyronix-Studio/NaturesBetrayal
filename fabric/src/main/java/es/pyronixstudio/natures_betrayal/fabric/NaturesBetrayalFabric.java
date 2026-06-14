package es.pyronixstudio.natures_betrayal.fabric;

import es.pyronixstudio.natures_betrayal.common.IModLoader;
import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import es.pyronixstudio.natures_betrayal.common.config.IModConfig;
import es.pyronixstudio.natures_betrayal.fabric.config.FabricConfig;
import es.pyronixstudio.natures_betrayal.fabric.config.FabricConfigBridge;
import net.fabricmc.api.ModInitializer;


public class NaturesBetrayalFabric implements ModInitializer, IModLoader {

    @Override
    public void onInitialize() {
        NaturesBetrayal.initialize(this);
        FabricConfig.registrar();
        NaturesBetrayal.onFinishPreInitialization();
    }

    @Override
    public IModConfig config() {
        return new FabricConfigBridge();
    }
}