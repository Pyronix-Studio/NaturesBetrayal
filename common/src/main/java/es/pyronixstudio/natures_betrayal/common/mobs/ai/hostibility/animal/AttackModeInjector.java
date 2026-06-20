package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.animal;

import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.EnumAttackMode;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.IClassTypeDelegateInjector;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.IHostibilityInjector;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.npc.Villager;

public class AttackModeInjector implements IClassTypeDelegateInjector {

    private static final Class<?>[] TARGETS = new Class<?>[]{Animal.class, WaterAnimal.class, AbstractPiglin.class, NeutralMob.class, Villager.class};

    private final IHostibilityInjector animalGenericHostibilityInjector;

    public AttackModeInjector(){
        EnumAttackMode attackMode  = NaturesBetrayalConfig.get().attackMode();
        animalGenericHostibilityInjector = switch (attackMode){
            case ALL -> new AgressiveInjector();
            case RADIUS -> new NeutralInjector();
            case SPECIES -> new NeutralHordeInjector();
        };

    }


    @Override
    public Class<?>[] targets() {
        return TARGETS;
    }

    @Override
    public IHostibilityInjector delegate() {
        return animalGenericHostibilityInjector;
    }
}
