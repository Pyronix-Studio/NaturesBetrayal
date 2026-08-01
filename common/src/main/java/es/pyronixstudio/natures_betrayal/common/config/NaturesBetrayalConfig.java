package es.pyronixstudio.natures_betrayal.common.config;

import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.EnumAttackMode;

import java.nio.file.Path;

public class NaturesBetrayalConfig {

    public static ConfigSpec.EnumEntry<EnumAttackMode> ATTACK_MODE;
    public static ConfigSpec.Entry<Integer> TARGET_RADIUS;

    public NaturesBetrayalConfig(Path path){
        ConfigSpec.Builder builder = new ConfigSpec.Builder(path);
        ATTACK_MODE = builder.comment("Specify the Attack mode").defineEnum("attack_mode", EnumAttackMode.ALL, EnumAttackMode.values());
        TARGET_RADIUS = builder.comment("Specify the radius target").define("target_radius", 30);
        builder.build().load();
    }



}