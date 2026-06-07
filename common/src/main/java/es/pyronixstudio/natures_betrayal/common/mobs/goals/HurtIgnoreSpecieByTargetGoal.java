package es.pyronixstudio.natures_betrayal.common.mobs.goals;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.phys.AABB;

import java.util.Iterator;
import java.util.List;

public class HurtIgnoreSpecieByTargetGoal extends HurtByTargetGoal {

    public HurtIgnoreSpecieByTargetGoal(PathfinderMob pathfinderMob, Class<?>... classs) {
        super(pathfinderMob, classs);
    }

    @Override
    protected void alertOthers() {
        double d = this.getFollowDistance();
        AABB aABB = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(d, 10.0F, d);
        List<Entity> list = this.mob.level().getEntities(this.mob, aABB);
        Iterator<Entity> it = list.iterator();
        while(it.hasNext()){
            Entity entity = it.next();
            if(!(entity instanceof Mob mobAllied)) {
                it.remove();
                continue;
            }

            LivingEntity hurtted = this.mob.getLastHurtByMob();
            if(hurtted == null)
                continue;

            this.alertOther(mobAllied, hurtted);
        }

    }
}
