package es.pyronixstudio.natures_betrayal.neoforge.config;

import com.electronwill.nightconfig.core.ConfigSpec;
import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.EnumAttackMode;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class NeoConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<EnumAttackMode> ATTACK_MODE =
            BUILDER.comment("Specify the Attack mode").defineEnum("attack_mode", EnumAttackMode.ALL, EnumAttackMode.values());


    public static final  ModConfigSpec.ConfigValue<Integer> TARGET_RADIUS =
            BUILDER.comment("Specify the radius target").define("target_radius", 30);


    private static final ModConfigSpec SPEC = BUILDER.build();



    public static void registrar(ModContainer container){
        container.registerConfig(ModConfig.Type.COMMON, NeoConfig.SPEC,  NaturesBetrayal.MOD_ID + "/" + NaturesBetrayal.MOD_ID + ".toml");
    }


}
