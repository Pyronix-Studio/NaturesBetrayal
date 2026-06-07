package es.pyronixstudio.natures_betrayal.neoforge;

import es.pyronixstudio.natures_betrayal.common.IModLoader;
import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import es.pyronixstudio.natures_betrayal.common.config.IModConfig;
import es.pyronixstudio.natures_betrayal.neoforge.config.NeoConfig;
import es.pyronixstudio.natures_betrayal.neoforge.config.NeoConfigBridge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;


@Mod(NaturesBetrayal.MOD_ID)
public class NaturesBetrayalNeoforge implements IModLoader {

    public NaturesBetrayalNeoforge(IEventBus modEventBus, ModContainer modContainer) {
        NaturesBetrayal.initialize(this);

        NeoConfig.registrar(modContainer);

        NaturesBetrayal.onFinishPreInitialization();
    }

    @Override
    public IModConfig config() {
        return new NeoConfigBridge();
    }
}
