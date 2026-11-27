package es.pyronixstudio.natures_betrayal.common.config;

import es.pyronixstudio.natures_betrayal.common.entity.ai.hostility.EnumAttackMode;

import java.nio.file.Path;

public class NaturesBetrayalConfig {

    private static final int MAX_FOLLOW_RANGE = 2048;
    private static final double MAX_DISTANCE = Integer.MAX_VALUE;

    public  static ConfigSpec.Entry<Boolean> CAN_SLEEP;

    public static ConfigSpec.EnumEntry<EnumAttackMode> ATTACK_MODE;
    public static ConfigSpec.Entry<Double> ATTACK_DAMAGE;
    public static ConfigSpec.Entry<Integer> TARGET_RADIUS;
    public static ConfigSpec.Entry<Boolean> XRAY_TARGET;
    public static ConfigSpec.Entry<Double> GROUND_MOVEMENT_SPEED_MODIFIER;
    public static ConfigSpec.Entry<Double> WATER_MOVEMENT_SPEED_MODIFIER;


    public static ConfigSpec.Entry<Boolean> SPIDER_ATTACK_ON_LIGHT;

    public static ConfigSpec.Entry<Boolean> CREEPER_NOT_LINE_SIGHT;

    public static ConfigSpec.Entry<Double> SKELETON_MELEE_WEAPON;

    public static ConfigSpec.Entry<Boolean> GHAST_NOT_LINE_SIGHT;
    public static ConfigSpec.Entry<Double> GHAST_SHOOT_RANGE;
    public static ConfigSpec.Entry<Double> GHAST_VERTICAL_RANGE;

    public NaturesBetrayalConfig(Path path){
        ConfigSpec.Builder builder = new ConfigSpec.Builder(path);

        ConfigSpec.Builder.Section playerSection = builder.push("Player");
        CAN_SLEEP = playerSection.comment("If false, players cannot sleep in beds").define("can_sleep", false);
        playerSection.pop();

        ConfigSpec.Builder.Section generalSection = builder.push("General");
        ATTACK_MODE = generalSection.comment("Which mobs turn hostile towards players and how they react when attacked").defineEnum("attack_mode", EnumAttackMode.ALL, EnumAttackMode.values());
        ATTACK_DAMAGE = generalSection.comment("Damage per hit dealt by normally non-hostile mobs affected by attack_mode").define("attack_damage", 2.0D, 0.0D, Double.MAX_VALUE);
        TARGET_RADIUS = generalSection.comment("Radius in blocks to detect players and to call nearby mobs to join an attack").define("target_radius", 30, 0, MAX_FOLLOW_RANGE);
        XRAY_TARGET = generalSection.comment("If true, mobs can detect and target players through walls and blocks").define("xray_target", true);
        GROUND_MOVEMENT_SPEED_MODIFIER = generalSection.comment("Speed multiplier for land mobs while chasing a target").define("ground_movement_speed_modifier", 1.2D, 0.0D, 1024.0D);
        WATER_MOVEMENT_SPEED_MODIFIER = generalSection.comment("Swimming speed multiplier, horizontal and vertical").define("water_movement_speed_modifier", 1.0D, 0.0D, 1024.0D);

        generalSection.pop();

        ConfigSpec.Builder.Section spiderSection = builder.push("Spider");
        SPIDER_ATTACK_ON_LIGHT = spiderSection.comment("If true, spiders stay hostile in well-lit areas (daylight, torches, etc.) instead of turning neutral").define("target_light", true);
        spiderSection.pop();

        ConfigSpec.Builder.Section creeperSection = builder.push("Creeper");
        CREEPER_NOT_LINE_SIGHT = creeperSection.comment("If true, creepers can ignite and explode even with blocks between them and their target").define("not_line_sight", true);
        creeperSection.pop();


        ConfigSpec.Builder.Section skeletonSection = builder.push("Skeleton");
        SKELETON_MELEE_WEAPON = skeletonSection.comment("Distance in blocks at which skeletons put away the bow and fight with a melee weapon").define("melee_weapon", 3.5D, 0.0D, MAX_DISTANCE);
        skeletonSection.pop();


        ConfigSpec.Builder.Section ghastSection = builder.push("Ghast");
        GHAST_NOT_LINE_SIGHT = ghastSection.comment("If true, ghasts can fire at their target even without a clear line of sight").define("not_line_sight", true);
        GHAST_SHOOT_RANGE = ghastSection.comment("Maximum distance in blocks from which a ghast can shoot at its target").define("shoot_range", 64.0D, 0.0D, MAX_DISTANCE);
        GHAST_VERTICAL_RANGE = ghastSection.comment("Maximum vertical distance in blocks at which a ghast can still target an entity").define("vertical_target_distance", 4.0D, 0.0D, MAX_DISTANCE);
        ghastSection.pop();





        builder.build().load();
    }



}