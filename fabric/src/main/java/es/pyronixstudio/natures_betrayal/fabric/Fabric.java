package es.pyronixstudio.natures_betrayal.fabric;

import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import net.fabricmc.api.ModInitializer;


public class Fabric implements ModInitializer {


    @Override
    public void onInitialize() {

        NaturesBetrayal.LOGGER.info("Hello Fabric world!");
    }
}