package es.pyronixstudio.natures_betrayal.common.config;

public class NaturesBetrayalConfig {

    private static IModConfig INSTANCE;

    public static void register(IModConfig iModConfig){
        INSTANCE = iModConfig;
    }

    public static IModConfig get(){
        return INSTANCE;
    }

}
