package es.pyronixstudio.natures_betrayal.common.entity.ai.hostility.monsters;

import es.pyronixstudio.natures_betrayal.common.entity.ai.IClassTypeInjector;
import es.pyronixstudio.natures_betrayal.common.entity.goals.ShootWebGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;

public class SpiderInjector implements IClassTypeInjector {

    private static final Class<?>[] TARGETS = new Class<?>[]{Spider.class};


    @Override
    public Class<?>[] targets() {
        return TARGETS;
    }

    @Override
    public void inject(Mob mob, GoalSelector goalSelector, GoalSelector targetSelector, EntityType<?> entityType, Level level) {
            goalSelector.addGoal(3, new ShootWebGoal(mob));
    }
}
