package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostility.monsters;

import es.pyronixstudio.natures_betrayal.common.mobs.ai.IClassTypeInjector;
import es.pyronixstudio.natures_betrayal.common.mobs.entity.phantom.PhantomRiderGoal;
import es.pyronixstudio.natures_betrayal.common.mobs.goals.MobBreakGoal;
import es.pyronixstudio.natures_betrayal.common.mobs.goals.ZombieBridgeGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class ZombieInjector implements IClassTypeInjector {

    private static final Class<?>[] TARGETS = new Class<?>[]{Zombie.class};

    @Override
    public Class<?>[] targets() {
        return TARGETS;
    }

    @Override
    public void inject(PathfinderMob mob, GoalSelector goalSelector, GoalSelector targetSelector, EntityType<?> entityType, Level level) {
        desplazarGoals(2, goalSelector);
        WrappedGoal zombieAttack = cutWrappedGoal(goalSelector, ZombieAttackGoal.class);
        if (zombieAttack != null) {
            int priority = zombieAttack.getPriority();

            goalSelector.addGoal(priority, new MobBreakGoal(mob));
            goalSelector.addGoal(priority + 1, new ZombieBridgeGoal(mob));
            goalSelector.addGoal(priority + 2, zombieAttack.getGoal());

            goalSelector.addGoal(priority + 3, new PhantomRiderGoal(mob));


        }
    }
}
