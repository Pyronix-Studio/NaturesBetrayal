package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.animal;

import es.pyronixstudio.natures_betrayal.common.config.HostibilityType;
import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.IClassTypeDelegateInjector;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.IHostibilityInjector;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.npc.Villager;

public class AnimalInjector implements IClassTypeDelegateInjector {

    private static final Class<?>[] TARGETS = new Class<?>[]{Animal.class, WaterAnimal.class, Villager.class};

    private final IHostibilityInjector animalGenericHostibilityInjector;

    public AnimalInjector(){
        HostibilityType hostibilityType  = NaturesBetrayalConfig.get().hostibilityType();

        animalGenericHostibilityInjector = switch (hostibilityType){
            case AGGRESSIVE -> new AgressiveInjector();
            case NEUTRAL -> new NeutralInjector();
            case NEUTRAL_HORDE -> new NeutralHordeInjector();
            case PASSIVE -> null;
        };

    }


    @Override
    public Class<?>[] targets() {
        return TARGETS;
    }

    @Override
    public IHostibilityInjector delegate() {
        return animalGenericHostibilityInjector;
    }
}
