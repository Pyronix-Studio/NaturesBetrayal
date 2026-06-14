package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility;

import net.minecraft.world.entity.Mob;

public interface IClassTypeInjector extends IHostibilityInjector{

    Class<?>[] targets();

     default boolean instance(Mob mob){

         for(Class<?> targetClass : targets()){
             if(targetClass.isInstance(mob))
                 return true;
         }
         return false;

     }

}
