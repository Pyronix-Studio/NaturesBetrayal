package es.pyronixstudio.natures_betrayal.fabric.config;

import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import es.pyronixstudio.natures_betrayal.common.config.HostibilityType;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.nio.file.Path;

public class FabricConfig {


    public static final ConfigSpec.Builder BUILDER =
            new ConfigSpec.Builder(resolvePath());

    public static Path resolvePath(){
        Path configFolder = FabricLoader.getInstance().getConfigDir();
        Path modConfigFolder =  configFolder.resolve(NaturesBetrayal.MOD_ID);
        File modConfigFolderFile = modConfigFolder.toFile();
        if(!modConfigFolderFile.exists())
            modConfigFolderFile.mkdir();
        return modConfigFolder.resolve(NaturesBetrayal.MOD_ID+".toml");
    }

    public static final ConfigSpec.EnumEntry<HostibilityType> HOSTIBILITY_TYPE =
            BUILDER.comment("Set the Hostibility Type").defineEnum("hostibility_type", HostibilityType.AGGRESSIVE, HostibilityType.values());



    private static final ConfigSpec SPEC = BUILDER.build();

    public static void registrar(){
        SPEC.load();
    }

}
