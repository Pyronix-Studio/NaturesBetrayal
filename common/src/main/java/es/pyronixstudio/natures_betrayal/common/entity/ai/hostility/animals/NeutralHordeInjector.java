package es.pyronixstudio.natures_betrayal.common.entity.ai.hostility.animals;

import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import es.pyronixstudio.natures_betrayal.common.entity.ai.IHostilityInjector;
import es.pyronixstudio.natures_betrayal.common.entity.goals.HurtIgnoreSpecieByTargetGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;

public class NeutralHordeInjector implements IHostilityInjector {
    @Override
    public void inject(Mob mob, GoalSelector goalSelector, GoalSelector targetSelector, EntityType<?> entityType, Level level) {
        if (!(mob instanceof PathfinderMob pf)) return;

        borrarGoal(targetSelector, HurtByTargetGoal.class);
        borrarGoal(goalSelector, PanicGoal.class);
        desplazarGoals(1, targetSelector);
        desplazarGoals(1, goalSelector);

        double speedModifier = mob instanceof WaterAnimal ? 1.0D : NaturesBetrayalConfig.GROUND_MOVEMENT_SPEED_MODIFIER.get();
        goalSelector.addGoal(0, new MeleeAttackGoal(pf, speedModifier, true));
        targetSelector.addGoal(0, new HurtIgnoreSpecieByTargetGoal(pf).setAlertOthers());
    }
}
