package es.pyronixstudio.natures_betrayal.fabric;

import es.pyronixstudio.natures_betrayal.common.IModLoader;
import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import es.pyronixstudio.natures_betrayal.fabric.registries.RegistriesFabric;
import es.pyronixstudio.natures_betrayal.fabric.registries.RegistriesFabricClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;


public class NaturesBetrayalFabric implements ModInitializer, ClientModInitializer, IModLoader {

    @Override
    public void onInitialize() {
        NaturesBetrayal.initialize(this);
        NaturesBetrayal.onFinishPreInitialization();
        RegistriesFabric.register();
    }

    @Override
    public void onInitializeClient() {
        RegistriesFabricClient.register();
    }

    @Override
    public Path getConfigBasePath() {
        return FabricLoader.getInstance().getConfigDir();
    }
}