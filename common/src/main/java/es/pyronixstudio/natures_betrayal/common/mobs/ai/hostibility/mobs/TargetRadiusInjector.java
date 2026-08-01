package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.mobs;

import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.IClassTypeInjector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.level.Level;

public class TargetRadiusInjector implements IClassTypeInjector {

    private static final Class<?>[] TARGETS = new Class<?>[]{PathfinderMob.class};

    @Override
    public void inject(PathfinderMob mob, GoalSelector goalSelector, GoalSelector targetSelector, EntityType<?> entityType, Level level) {
        AttributeInstance attributeInstance = mob.getAttribute(Attributes.FOLLOW_RANGE);
            if(attributeInstance != null)
                attributeInstance.setBaseValue(NaturesBetrayalConfig.TARGET_RADIUS.get());
    }

    @Override
    public Class<?>[] targets() {
        return TARGETS;
    }
}
