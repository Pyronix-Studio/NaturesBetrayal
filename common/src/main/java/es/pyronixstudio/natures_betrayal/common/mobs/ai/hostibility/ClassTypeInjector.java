package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility;

import net.minecraft.world.entity.Mob;

public interface ClassTypeInjector extends IHostibilityInjector{

     Class<? extends Mob> clazz();

     default boolean instance(Mob mob){
         return clazz().isInstance(mob);
     }

}
