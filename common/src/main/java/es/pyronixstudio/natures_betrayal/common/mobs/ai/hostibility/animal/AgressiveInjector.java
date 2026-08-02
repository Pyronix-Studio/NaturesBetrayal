package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.animal;

import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.IHostibilityInjector;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.mobs.NearestXRayAttackableTargetGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class AgressiveInjector implements IHostibilityInjector {
    @Override
    public void inject(PathfinderMob mob, GoalSelector goalSelector, GoalSelector targetSelector, EntityType<?> entityType, Level level) {
        borrarGoal(targetSelector, NearestAttackableTargetGoal.class);
        borrarGoal(goalSelector, MeleeAttackGoal.class);

        desplazarGoals(1, targetSelector);
        desplazarGoals(1, goalSelector);

        setDefaultAttributes(mob);
        goalSelector.addGoal(0, new MeleeAttackGoal(mob, 1.2D, true));
        targetSelector.addGoal(0, new NearestXRayAttackableTargetGoal<>(mob, Player.class, false, false));

    }
}
