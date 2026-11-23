package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.mobs;

import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.IClassTypeInjector;
import es.pyronixstudio.natures_betrayal.common.mobs.goals.MobBreakGoal;
import es.pyronixstudio.natures_betrayal.common.mobs.goals.ZombieBridgeGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ZombieInjector implements IClassTypeInjector {

    private static final Class<?>[] TARGETS = new Class<?>[]{Zombie.class};

    @Override
    public Class<?>[] targets() {
        return TARGETS;
    }

    @Override
    public void inject(PathfinderMob mob, GoalSelector goalSelector, GoalSelector targetSelector, EntityType<?> entityType, Level level) {
        borrarGoal(targetSelector, NearestAttackableTargetGoal.class); //borramos los nearest

        desplazarGoals(2, goalSelector);
        WrappedGoal zombieAttack = cutWrappedGoal(goalSelector, ZombieAttackGoal.class);
        if (zombieAttack != null) {
            int priority = zombieAttack.getPriority();

            //ASI  PONEMOS PRIORIDAD EL DE ATTACK Y LUEGO BRIDGE SI NO PUEDE

            targetSelector.addGoal(2, new NearestXRayAttackableTargetGoal<>(mob, Player.class, true));
            targetSelector.addGoal(3,  new NearestAttackableTargetGoal<>(mob, AbstractVillager.class, false));
            targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(mob, IronGolem.class, true));
            targetSelector.addGoal(5, new NearestAttackableTargetGoal(mob, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));

            goalSelector.addGoal(priority, zombieAttack.getGoal());
            goalSelector.addGoal(priority+1, new MobBreakGoal(mob));
            goalSelector.addGoal(priority+2, new ZombieBridgeGoal(mob));
            goalSelector.addGoal(priority+3, new PhantomRiderGoal(mob));


        }
    }
}
