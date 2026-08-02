package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.animal;

import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.IHostibilityInjector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;

public class NeutralInjector implements IHostibilityInjector {
    @Override
    public void inject(PathfinderMob mob, GoalSelector goalSelector, GoalSelector targetSelector, EntityType<?> entityType, Level level) {
        borrarGoal(goalSelector, PanicGoal.class);
        desplazarGoals(1, targetSelector);
        desplazarGoals(1, goalSelector);
        setDefaultAttributes(mob);

        goalSelector.addGoal(0, new MeleeAttackGoal(mob, 1.2D, true));
        targetSelector.addGoal(0,new HurtByTargetGoal(mob).setAlertOthers());
    }
}
