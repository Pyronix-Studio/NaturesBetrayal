package es.pyronixstudio.natures_betrayal.common.entity.ai.hostility.monsters;

import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import es.pyronixstudio.natures_betrayal.common.entity.ai.IClassTypeInjector;
import es.pyronixstudio.natures_betrayal.common.entity.goals.SwellGoalNotLineSight;
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
        if(NaturesBetrayalConfig.CREEPER_NOT_LINE_SIGHT.get())
            goalSelector.addGoal(5, new SwellGoalNotLineSight(creeper));

    }
}
