package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.mobs;

import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.ClassTypeInjector;
import es.pyronixstudio.natures_betrayal.common.mobs.goals.ZombieBridgeGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class ZombieInjector implements ClassTypeInjector {

    @Override
    public Class<? extends Mob> clazz() {
        return Zombie.class;
    }

    @Override
    public void inject(PathfinderMob mob, GoalSelector goalSelector, GoalSelector targetSelector, EntityType<?> entityType, Level level) {
        desplazarGoals(1, goalSelector);
        WrappedGoal zombieAttack = cutWrappedGoal(goalSelector, ZombieAttackGoal.class);
        int priority = zombieAttack.getPriority();

        //ASI  PONEMOS PRIORIDAD EL DE ATTACK Y LUEGO BRIDGE SI NO PUEDE
        goalSelector.addGoal(priority, zombieAttack.getGoal());
        goalSelector.addGoal(priority - 1, new ZombieBridgeGoal(mob));
    }
}
