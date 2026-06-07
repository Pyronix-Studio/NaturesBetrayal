package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility;

import es.pyronixstudio.natures_betrayal.common.config.HostibilityType;
import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;

public class HostibilityInjectorManager {

    private final IHostibilityInjector hostibilityInjector;

    public HostibilityInjectorManager(){
        HostibilityType hostibilityType  = NaturesBetrayalConfig.get().hostibilityType();

        hostibilityInjector = switch (hostibilityType){
            case AGGRESSIVE -> new AgressiveInjector();
            case NEUTRAL -> new NeutralInjector();
            case NEUTRAL_HORDE -> new NeutralHordeInjector();
            case PASSIVE -> null;
        };
    }

    public void inject(Mob mob, GoalSelector goalSelector, GoalSelector targetSelector, EntityType<?> entityType, Level level){
        if(hostibilityInjector == null)
            return;

        if (mob instanceof Animal animal) {
            hostibilityInjector.inject(animal, goalSelector, targetSelector, entityType, level);
        } else if (mob instanceof WaterAnimal waterAnimal) {
            hostibilityInjector.inject(waterAnimal, goalSelector, targetSelector, entityType, level);
        }

    }


}
