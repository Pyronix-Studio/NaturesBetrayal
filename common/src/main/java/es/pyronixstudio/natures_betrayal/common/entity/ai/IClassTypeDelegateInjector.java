package es.pyronixstudio.natures_betrayal.common.entity.ai;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.level.Level;

public interface IClassTypeDelegateInjector extends IClassTypeInjector {

    @Override
    default void inject(Mob mob, GoalSelector goalSelector, GoalSelector targetSelector, EntityType<?> entityType, Level level){
        delegate().inject(mob, goalSelector, targetSelector, entityType, level);
    }

    IHostilityInjector delegate();
}
