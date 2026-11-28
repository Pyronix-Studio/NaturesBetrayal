package es.pyronixstudio.natures_betrayal.common.mixins.mobs.injects;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HurtByTargetGoal.class)
public abstract class HurtByTargerGoalMixin extends TargetGoal {

    public HurtByTargerGoalMixin(Mob mob, boolean mustSee) {
        super(mob, mustSee);
    }

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void naturesbetrayal$canUse(CallbackInfoReturnable<Boolean> cir) {
       if(this.mob instanceof NeutralMob)
           return;
        LivingEntity attacker = this.mob.getLastHurtByMob();
        if(attacker instanceof Mob && !(attacker instanceof NeutralMob))
            cir.setReturnValue(false);
    }

}
