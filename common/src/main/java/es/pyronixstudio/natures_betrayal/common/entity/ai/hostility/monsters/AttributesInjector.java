package es.pyronixstudio.natures_betrayal.common.entity.ai.hostility.monsters;

import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import es.pyronixstudio.natures_betrayal.common.entity.ai.IClassTypeInjector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.level.Level;

public class AttributesInjector implements IClassTypeInjector {

    private static final Class<?>[] TARGETS = new Class<?>[]{Mob.class};

    @Override
    public void inject(Mob mob, GoalSelector goalSelector, GoalSelector targetSelector, EntityType<?> entityType, Level level) {
        AttributeInstance attributeInstance = mob.getAttribute(Attributes.FOLLOW_RANGE);
        if (attributeInstance != null)
            attributeInstance.setBaseValue(NaturesBetrayalConfig.TARGET_RADIUS.get());

        AttributeInstance damageInstance = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damageInstance != null)
            damageInstance.setBaseValue(NaturesBetrayalConfig.ATTACK_DAMAGE.get());
    }

    @Override
    public Class<?>[] targets() {
        return TARGETS;
    }
}
