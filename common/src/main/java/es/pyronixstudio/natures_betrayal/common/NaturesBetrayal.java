package es.pyronixstudio.natures_betrayal.common;

import com.mojang.logging.LogUtils;
import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;

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
        new NaturesBetrayalConfig(resolveConfigPath());
    }

    private static Path resolveConfigPath(){
        Path modConfigFolder =  INSTANCE.iModLoader.getConfigBasePath().resolve(NaturesBetrayal.MOD_ID);
        File modConfigFolderFile = modConfigFolder.toFile();

        if(!modConfigFolderFile.exists())
            modConfigFolderFile.mkdir();

        return modConfigFolder.resolve(NaturesBetrayal.MOD_ID+".toml");
    }

}
