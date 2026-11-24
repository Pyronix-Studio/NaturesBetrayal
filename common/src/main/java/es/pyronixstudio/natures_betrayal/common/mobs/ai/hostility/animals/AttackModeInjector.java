package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostility.animals;

import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.IClassTypeDelegateInjector;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.IHostilityInjector;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostility.EnumAttackMode;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.npc.Villager;

public class AttackModeInjector implements IClassTypeDelegateInjector {

    private static final Class<?>[] TARGETS = new Class<?>[]{Animal.class, WaterAnimal.class, AbstractPiglin.class, NeutralMob.class, Spider.class, Villager.class};

    private final IHostilityInjector animalGenericHostibilityInjector;

    public AttackModeInjector(){
        EnumAttackMode attackMode  = NaturesBetrayalConfig.ATTACK_MODE.get();
        animalGenericHostibilityInjector = switch (attackMode){
            case ALL -> new AggressiveInjector();
            case RADIUS -> new NeutralHordeInjector();
            case SPECIES -> new NeutralInjector();
        };

    }


    @Override
    public Class<?>[] targets() {
        return TARGETS;
    }

    @Override
    public IHostilityInjector delegate() {
        return animalGenericHostibilityInjector;
    }
}
