package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.mobs;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

import java.util.function.Predicate;

public class NearestXRayAttackableTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

    public NearestXRayAttackableTargetGoal(Mob mob, Class<T> class_, boolean bl) {
        this(mob, class_, bl, false);

    }

    public NearestXRayAttackableTargetGoal(Mob mob, Class<T> class_, boolean bl, boolean bl2) {
        super(mob, class_, 10, bl, bl2, (Predicate)null);
        targetConditions.ignoreLineOfSight();
    }




}
