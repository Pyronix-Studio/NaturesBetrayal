package es.pyronixstudio.natures_betrayal.common;

import com.mojang.logging.LogUtils;
import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import org.slf4j.Logger;

public class NaturesBetrayal {

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "natures_betrayal";
    private static NaturesBetrayal INSTANCE;

    private IModLoader iModLoader;

    public static void initialize(IModLoader iModLoader){
        if(INSTANCE == null){
            INSTANCE = new NaturesBetrayal();
        }
        INSTANCE.iModLoader = iModLoader;
    }

    private NaturesBetrayal(){}


    public static void onFinishPreInitialization() {
        NaturesBetrayalConfig.register(INSTANCE.iModLoader.config());
    }
}
