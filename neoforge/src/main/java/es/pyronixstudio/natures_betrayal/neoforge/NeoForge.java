package es.pyronixstudio.natures_betrayal.neoforge;

import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;


@Mod(NaturesBetrayal.MOD_ID)
public class NeoForge {

    public NeoForge(IEventBus modEventBus, ModContainer modContainer) {
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.register(this);

    }

}
