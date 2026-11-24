package es.pyronixstudio.natures_betrayal.common.mixins.mobs.accessor;

import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NearestAttackableTargetGoal.class)
public interface NearestAttackableTargetGoalAcessor {
    @Accessor("targetConditions")
    TargetingConditions targetingConditions();

}
