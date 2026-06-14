package es.pyronixstudio.natures_betrayal.fabric.config;

import es.pyronixstudio.natures_betrayal.common.config.HostibilityType;
import es.pyronixstudio.natures_betrayal.common.config.IModConfig;

public class FabricConfigBridge implements IModConfig {
    @Override
    public HostibilityType hostibilityType() {
        return FabricConfig.HOSTIBILITY_TYPE.get();
    }
}
