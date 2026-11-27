package es.pyronixstudio.natures_betrayal.common.entity.ai.hostility.animals;

import es.pyronixstudio.natures_betrayal.common.entity.ai.IHostilityInjector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class AggressiveInjector implements IHostilityInjector {
    @Override
    public void inject(PathfinderMob mob, GoalSelector goalSelector, GoalSelector targetSelector, EntityType<?> entityType, Level level) {
        borrarGoal(goalSelector, MeleeAttackGoal.class);

        desplazarGoals(1, targetSelector);
        desplazarGoals(1, goalSelector);

        setDefaultAttributes(mob);
        if(!containsGoal(goalSelector, MeleeAttackGoal.class))
            goalSelector.addGoal(0, new MeleeAttackGoal(mob, 1.2D, true));

        if(!containsGoal(targetSelector, NearestAttackableTargetGoal.class))
            targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(mob, Player.class, false, false));

    }
}
