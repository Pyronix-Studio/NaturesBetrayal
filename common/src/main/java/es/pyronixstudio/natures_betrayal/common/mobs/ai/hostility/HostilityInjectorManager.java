package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostility;

import es.pyronixstudio.natures_betrayal.common.mobs.ai.IClassTypeInjector;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostility.animals.AttackModeInjector;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostility.monsters.CreeperInjector;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostility.monsters.TargetRadiusInjector;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostility.monsters.ZombieInjector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class HostilityInjectorManager {

    private static HostilityInjectorManager INSTANCE;

    public static HostilityInjectorManager getManager() {
        if (INSTANCE == null) {
            INSTANCE = new HostilityInjectorManager();
        }
        return INSTANCE;
    }


    private final Set<IClassTypeInjector> specificHostibilyInjector = new HashSet<>();

    private void registerInjector(Supplier<IClassTypeInjector> supplier) {
        registerInjector(supplier.get());
    }

    private void registerInjector(IClassTypeInjector iHostibilityInjector) {
        specificHostibilyInjector.add(iHostibilityInjector);
    }

    private void registersSpecificInjectors() {
        registerInjector(TargetRadiusInjector::new);
        registerInjector(ZombieInjector::new);
        registerInjector(CreeperInjector::new);
        registerInjector(AttackModeInjector::new);
    }

    public HostilityInjectorManager() {
        registersSpecificInjectors();
    }

    public void inject(Mob mob, GoalSelector goalSelector, GoalSelector targetSelector, EntityType<?> entityType, Level level) {
        if (mob instanceof PathfinderMob pathfinderMob) {
            for (IClassTypeInjector classTypeInjector : specificHostibilyInjector) {
                if (!classTypeInjector.instance(pathfinderMob))
                    continue;
                classTypeInjector.inject(pathfinderMob, goalSelector, targetSelector, entityType, level);
            }

        }


    }


}
