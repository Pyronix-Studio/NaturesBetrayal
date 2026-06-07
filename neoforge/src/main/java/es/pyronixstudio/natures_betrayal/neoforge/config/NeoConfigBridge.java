package es.pyronixstudio.natures_betrayal.neoforge.config;

import es.pyronixstudio.natures_betrayal.common.config.HostibilityType;
import es.pyronixstudio.natures_betrayal.common.config.IModConfig;

public class NeoConfigBridge implements IModConfig {
    @Override
    public HostibilityType hostibilityType() {
        return NeoConfig.HOSTIBILITY_TYPE.get();
    }
}
