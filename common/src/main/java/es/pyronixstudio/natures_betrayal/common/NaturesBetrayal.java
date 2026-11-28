package es.pyronixstudio.natures_betrayal.common;

import com.mojang.logging.LogUtils;
import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import es.pyronixstudio.natures_betrayal.common.events.Events;
import es.pyronixstudio.natures_betrayal.common.registries.Registries;
import es.pyronixstudio.util.events.EventHandler;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;

public class NaturesBetrayal {

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "natures_betrayal";
    public static final EventHandler eventHandler = new EventHandler();

    private static NaturesBetrayal INSTANCE;
    private IModLoader iModLoader;
    private final Registries registries = new Registries();


    public static void initialize(IModLoader iModLoader){
        if(INSTANCE == null){
            INSTANCE = new NaturesBetrayal();
        }
        INSTANCE.iModLoader = iModLoader;
    }

    private NaturesBetrayal(){}


    public static void onFinishPreInitialization() {
        new NaturesBetrayalConfig(resolveConfigPath());
        INSTANCE.registries.load();
        Events.start();
    }

    public static void onFinishPreInitializationClient(){
        INSTANCE.registries.loadClientRegistries();
    }

    private static Path resolveConfigPath(){
        Path modConfigFolder =  INSTANCE.iModLoader.getConfigBasePath().resolve(NaturesBetrayal.MOD_ID);
        File modConfigFolderFile = modConfigFolder.toFile();

        if(!modConfigFolderFile.exists())
            modConfigFolderFile.mkdir();

        return modConfigFolder.resolve(NaturesBetrayal.MOD_ID+".toml");
    }

    public static ResourceLocation resource(String path){
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static String key(String key){
        return MOD_ID + ":" + key;
    }

}
