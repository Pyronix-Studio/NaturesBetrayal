package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public interface IHostibilityInjector {

    void inject(PathfinderMob mob, GoalSelector goalSelector, GoalSelector targetSelector, EntityType<?> entityType, Level level);

    default void borrarGoal(GoalSelector goalSelector, Class<? extends Goal> goalClazz){
        List<WrappedGoal> existing = new ArrayList<>(goalSelector.getAvailableGoals());
        for(WrappedGoal wrappedGoal : existing){
            Goal goal = wrappedGoal.getGoal();
            if(goalClazz.isAssignableFrom(goal.getClass())){
                goalSelector.removeGoal(goal);
            }

        }
    }

    default WrappedGoal cutWrappedGoal(GoalSelector goalSelector, Class<? extends Goal> goalClazz){
        List<WrappedGoal> existing = new ArrayList<>(goalSelector.getAvailableGoals());
        for(WrappedGoal wrappedGoal : existing){
            Goal goal = wrappedGoal.getGoal();
            if(goalClazz.isInstance(goal)){
                goalSelector.removeGoal(goal);
                return wrappedGoal;
            }

        }
        return null;
    }

    default void desplazarGoals(int cantidad, GoalSelector goalSelector) {
        List<WrappedGoal> existing = new ArrayList<>(goalSelector.getAvailableGoals());

        for (WrappedGoal wrappedGoal : existing) {
            goalSelector.removeGoal(wrappedGoal.getGoal());
        }

        for (WrappedGoal wrappedGoal : existing) {
            goalSelector.addGoal(wrappedGoal.getPriority() + cantidad, wrappedGoal.getGoal());
        }
    }
    //TODO TIENE K VENIR DE CONFIG
    default void setDefaultAttributes(Mob mob){
        AttributeMap attributeMap = mob.getAttributes();
        attributeMap.getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(2.0D);
    }
}
