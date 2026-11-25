package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostility.monsters;

import es.pyronixstudio.natures_betrayal.common.mobs.ai.IClassTypeInjector;
import es.pyronixstudio.natures_betrayal.common.mobs.goals.SwellGoalNotLineSight;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

public class CreeperInjector implements IClassTypeInjector {

    private static final Class<?>[] TARGETS = new Class<?>[]{Creeper.class};

    @Override
    public Class<?>[] targets() {
        return TARGETS;
    }

    @Override
    public void inject(PathfinderMob mob, GoalSelector goalSelector, GoalSelector targetSelector, EntityType<?> entityType, Level level) {
        Creeper creeper = (Creeper)mob;
        goalSelector.addGoal(5, new SwellGoalNotLineSight(creeper));

    }
}
