package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.level.Level;

public interface IClassTypeDelegateInjector extends IClassTypeInjector {

    @Override
    default void inject(PathfinderMob mob, GoalSelector goalSelector, GoalSelector targetSelector, EntityType<?> entityType, Level level){
        delegate().inject(mob, goalSelector, targetSelector, entityType, level);
    }

    IHostibilityInjector delegate();
}
