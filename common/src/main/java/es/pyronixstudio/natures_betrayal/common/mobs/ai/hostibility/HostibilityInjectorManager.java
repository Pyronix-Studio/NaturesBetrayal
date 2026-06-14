package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility;

import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.animal.AnimalInjector;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.mobs.ZombieInjector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class HostibilityInjectorManager {

    private final Set<IClassTypeInjector> specificHostibilyInjector = new HashSet<>();

    private void registerInjector(Supplier<IClassTypeInjector> supplier) {
        registerInjector(supplier.get());
    }

    private void registerInjector(IClassTypeInjector iHostibilityInjector) {
        specificHostibilyInjector.add(iHostibilityInjector);
    }

    private void registersSpecificInjectors() {
        registerInjector(ZombieInjector::new);
        registerInjector(AnimalInjector::new);
    }

    public HostibilityInjectorManager() {
        registersSpecificInjectors();
    }

    public void inject(Mob mob, GoalSelector goalSelector, GoalSelector targetSelector, EntityType<?> entityType, Level level) {
        if (mob instanceof PathfinderMob pathfinderMob) {
            for (IClassTypeInjector classTypeInjector : specificHostibilyInjector) {
                if (!classTypeInjector.instance(pathfinderMob))
                    continue;
                classTypeInjector.inject(pathfinderMob, goalSelector, targetSelector, entityType, level);
                break;
            }

        }


    }


}
