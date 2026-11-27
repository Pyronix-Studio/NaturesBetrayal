package es.pyronixstudio.natures_betrayal.common.entity.ai;

import net.minecraft.world.entity.Mob;

public interface IClassTypeInjector extends IHostilityInjector{

    Class<?>[] targets();

     default boolean instance(Mob mob){

         for(Class<?> targetClass : targets()){
             if(targetClass.isInstance(mob))
                 return true;
         }
         return false;

     }

}
