package es.pyronixstudio.natures_betrayal.common.entity.ai.hostility.animals;

import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import es.pyronixstudio.natures_betrayal.common.entity.ai.IHostilityInjector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class AggressiveInjector implements IHostilityInjector {
    @Override
    public void inject(Mob mob, GoalSelector goalSelector, GoalSelector targetSelector, EntityType<?> entityType, Level level) {
        if (!(mob instanceof PathfinderMob pf)) return;

        borrarGoal(goalSelector, MeleeAttackGoal.class);

        desplazarGoals(1, targetSelector);
        desplazarGoals(1, goalSelector);
        double speedModifier = mob instanceof WaterAnimal ? 1.0D : NaturesBetrayalConfig.GROUND_MOVEMENT_SPEED_MODIFIER.get();
        if (!containsGoal(goalSelector, MeleeAttackGoal.class))
            goalSelector.addGoal(0, new MeleeAttackGoal(pf, speedModifier, true));

        if (!containsGoal(targetSelector, NearestAttackableTargetGoal.class)) {
            targetSelector.addGoal(0, new HurtByTargetGoal(pf));
            targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(pf, Player.class, false, false));
        }
    }
}
