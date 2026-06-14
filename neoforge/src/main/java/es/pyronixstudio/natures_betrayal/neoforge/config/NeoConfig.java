package es.pyronixstudio.natures_betrayal.neoforge.config;

import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import es.pyronixstudio.natures_betrayal.common.config.HostibilityType;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class NeoConfig{

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<HostibilityType> HOSTIBILITY_TYPE =
            BUILDER.comment("Set the Hostibility Type").defineEnum("hostibility_type", HostibilityType.AGGRESSIVE, HostibilityType.values());

    private static final ModConfigSpec SPEC = BUILDER.build();



    public static void registrar(ModContainer container){
        container.registerConfig(ModConfig.Type.COMMON, NeoConfig.SPEC,  NaturesBetrayal.MOD_ID + "/" + NaturesBetrayal.MOD_ID + ".toml");
    }


}
