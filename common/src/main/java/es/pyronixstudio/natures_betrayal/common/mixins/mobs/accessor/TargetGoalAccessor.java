package es.pyronixstudio.natures_betrayal.common.mixins.mobs.accessor;

import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TargetGoal.class)
public interface TargetGoalAccessor {

    @Accessor("mustSee")
    @Mutable
    void naturesBetrayal$setMustSee(boolean mustSee);
}
