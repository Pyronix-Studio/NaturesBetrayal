package es.pyronixstudio.natures_betrayal.common.config;

import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostility.EnumAttackMode;

import java.nio.file.Path;

public class NaturesBetrayalConfig {

    public static ConfigSpec.EnumEntry<EnumAttackMode> ATTACK_MODE;
    public static ConfigSpec.Entry<Integer> TARGET_RADIUS;
    public static ConfigSpec.Entry<Boolean> XRAY_TARGET;
    public static ConfigSpec.Entry<Boolean> SPIDER_ATTACK_ON_LIGHT;
    public static ConfigSpec.Entry<Boolean> CREEPER_NOT_LINE_SIGHT;
    public static ConfigSpec.Entry<Boolean> GHAST_NOT_LINE_SIGHT;

    public NaturesBetrayalConfig(Path path){
        ConfigSpec.Builder builder = new ConfigSpec.Builder(path);

        ATTACK_MODE = builder.comment("Defines which entities become hostile towards players and how they react when attacked").defineEnum("attack_mode", EnumAttackMode.ALL, EnumAttackMode.values());
        TARGET_RADIUS = builder.comment("Maximum detection radius for finding valid targets and nearby entities to join an attack").define("target_radius", 30);
        XRAY_TARGET = builder.comment("Allows entities to target players through blocks, ignoring line of sight").define("xray_target", true);

        SPIDER_ATTACK_ON_LIGHT = builder.comment("Allows spiders to target players even in well-lit areas").define("spider_target_light", true);
        CREEPER_NOT_LINE_SIGHT = builder.comment("Allows creepers to explode without having a direct line of sight to their target").define("creeper_not_line_sight", true);
        GHAST_NOT_LINE_SIGHT = builder.comment("Allows ghast to shoot without having a direct line of sight to their target").define("ghast_not_line_sight", true);

        builder.build().load();
    }



}