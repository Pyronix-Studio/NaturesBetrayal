package es.pyronixstudio.natures_betrayal.common.entity.ai.hostility;

import es.pyronixstudio.natures_betrayal.common.entity.ai.IClassTypeInjector;
import es.pyronixstudio.natures_betrayal.common.entity.ai.hostility.animals.AttackModeInjector;
import es.pyronixstudio.natures_betrayal.common.entity.ai.hostility.monsters.AttributesInjector;
import es.pyronixstudio.natures_betrayal.common.entity.ai.hostility.monsters.CreeperInjector;
import es.pyronixstudio.natures_betrayal.common.entity.ai.hostility.monsters.GhastInjector;
import es.pyronixstudio.natures_betrayal.common.entity.ai.hostility.monsters.SpiderInjector;import es.pyronixstudio.natures_betrayal.common.entity.ai.hostility.monsters.ZombieInjector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.monster.Spider;import net.minecraft.world.level.Level;

import java.util.LinkedHashSet;
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


    private final Set<IClassTypeInjector> specificHostibilyInjector = new LinkedHashSet<>();

    private void registerInjector(Supplier<IClassTypeInjector> supplier) {
        registerInjector(supplier.get());
    }

    private void registerInjector(IClassTypeInjector iHostibilityInjector) {
        specificHostibilyInjector.add(iHostibilityInjector);
    }

    private void registersSpecificInjectors() {
        registerInjector(AttributesInjector::new);
        registerInjector(AttackModeInjector::new);
        registerInjector(SpiderInjector::new);
        registerInjector(ZombieInjector::new);
        registerInjector(CreeperInjector::new);
        registerInjector(GhastInjector::new);
    }

    public HostilityInjectorManager() {
        registersSpecificInjectors();
    }

    public void inject(Mob mob, GoalSelector goalSelector, GoalSelector targetSelector, EntityType<?> entityType, Level level) {
        for (IClassTypeInjector classTypeInjector : specificHostibilyInjector) {
            if (!classTypeInjector.instance(mob))
                continue;
            classTypeInjector.inject(mob, goalSelector, targetSelector, entityType, level);
        }
    }


}
