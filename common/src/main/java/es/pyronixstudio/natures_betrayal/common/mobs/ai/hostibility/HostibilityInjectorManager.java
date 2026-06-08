package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility;

import es.pyronixstudio.natures_betrayal.common.config.HostibilityType;
import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.animal.AgressiveInjector;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.animal.NeutralHordeInjector;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.animal.NeutralInjector;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.mobs.ZombieInjector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class HostibilityInjectorManager {

    private final IHostibilityInjector animalGenericHostibilityInjector;


    private final Set<ClassTypeInjector> specificHostibilyInjector = new HashSet<>();

    private void registerInjector(Supplier<ClassTypeInjector> supplier){
        specificHostibilyInjector.add(supplier.get());
    }

    private void registerInjector(ClassTypeInjector iHostibilityInjector){
        specificHostibilyInjector.add(iHostibilityInjector);
    }

    private void registersSpecificInjectors(){
        registerInjector(ZombieInjector::new);
    }

    public HostibilityInjectorManager(){
        registersSpecificInjectors();
        HostibilityType hostibilityType  = NaturesBetrayalConfig.get().hostibilityType();

        animalGenericHostibilityInjector = switch (hostibilityType){
            case AGGRESSIVE -> new AgressiveInjector();
            case NEUTRAL -> new NeutralInjector();
            case NEUTRAL_HORDE -> new NeutralHordeInjector();
            case PASSIVE -> null;
        };
    }

    public void inject(Mob mob, GoalSelector goalSelector, GoalSelector targetSelector, EntityType<?> entityType, Level level){
        if(animalGenericHostibilityInjector == null)
            return;

        if(mob instanceof PathfinderMob pathfinderMob){
            for(ClassTypeInjector classTypeInjector : specificHostibilyInjector){
                if(classTypeInjector.instance(pathfinderMob)) {
                    classTypeInjector.inject(pathfinderMob, goalSelector, targetSelector, entityType, level);
                    break;
                }
            }

            if (pathfinderMob instanceof Animal animal) {
                animalGenericHostibilityInjector.inject(animal, goalSelector, targetSelector, entityType, level);
            } else if (pathfinderMob instanceof WaterAnimal waterAnimal) {
                animalGenericHostibilityInjector.inject(waterAnimal, goalSelector, targetSelector, entityType, level);
            }

        }


    }


}
