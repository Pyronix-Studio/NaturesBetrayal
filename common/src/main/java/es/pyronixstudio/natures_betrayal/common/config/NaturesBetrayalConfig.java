package es.pyronixstudio.natures_betrayal.common.config;

import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.EnumAttackMode;

import java.nio.file.Path;

public class NaturesBetrayalConfig {

    public static ConfigSpec.EnumEntry<EnumAttackMode> ATTACK_MODE;
    public static ConfigSpec.Entry<Integer> TARGET_RADIUS;
    public static ConfigSpec.Entry<Boolean> XRAY_TARGET;
    public static ConfigSpec.Entry<Boolean> SPIDER_ATTACK_ON_LIGHT;

    public NaturesBetrayalConfig(Path path){
        ConfigSpec.Builder builder = new ConfigSpec.Builder(path);

        ATTACK_MODE = builder.comment("Specify the Attack mode").defineEnum("attack_mode", EnumAttackMode.ALL, EnumAttackMode.values());
        TARGET_RADIUS = builder.comment("Specify the radius target").define("target_radius", 30);
        XRAY_TARGET = builder.comment("Target the player even if it is not in light of sight").define("xray_target", true);
        SPIDER_ATTACK_ON_LIGHT = builder.comment("Control if Spider can Target in Light").define("spider_target_light", true);
        builder.build().load();
    }



}