package es.pyronixstudio.natures_betrayal.common.entity.ai.hostility.monsters;

import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import es.pyronixstudio.natures_betrayal.common.entity.ai.IClassTypeInjector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class GhastInjector implements IClassTypeInjector {

    private static final Class<?>[] TARGETS = new Class<?>[]{Ghast.class};


    @Override
    public Class<?>[] targets() {
        return TARGETS;
    }

    @Override
    public void inject(Mob mob, GoalSelector goalSelector, GoalSelector targetSelector, EntityType<?> entityType, Level level) {
        Ghast ghast = (Ghast)mob;
        //Borramos el nearest de mojang pk tiene un predicate de altura vertical hardcodeado
        WrappedGoal wrappedGoal = cutWrappedGoal(targetSelector, NearestAttackableTargetGoal.class);
        if(wrappedGoal == null)
            return;
        int priority = wrappedGoal.getPriority();
        double configDistance = NaturesBetrayalConfig.GHAST_VERTICAL_RANGE.get();
        targetSelector.addGoal(priority, new NearestAttackableTargetGoal<>(ghast, Player.class, 10, true, false,
                living -> Math.abs(living.getY() - ghast.getY()) <= configDistance));

    }
}
