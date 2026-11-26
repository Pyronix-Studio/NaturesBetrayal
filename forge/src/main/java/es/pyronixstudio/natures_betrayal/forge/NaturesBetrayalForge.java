package es.pyronixstudio.natures_betrayal.forge;

import es.pyronixstudio.natures_betrayal.common.IModLoader;
import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import es.pyronixstudio.natures_betrayal.forge.registries.RegistriesForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

@Mod(NaturesBetrayal.MOD_ID)
public class NaturesBetrayalForge implements IModLoader {

    public NaturesBetrayalForge(FMLJavaModLoadingContext context) {
        NaturesBetrayal.initialize(this);
        NaturesBetrayal.onFinishPreInitialization();
        RegistriesForge.register(context.getModEventBus());
    }

    @Override
    public Path getConfigBasePath() {
        return FMLPaths.CONFIGDIR.get();
    }
}
