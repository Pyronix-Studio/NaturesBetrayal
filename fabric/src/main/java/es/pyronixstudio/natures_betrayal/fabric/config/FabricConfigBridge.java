package es.pyronixstudio.natures_betrayal.fabric.config;

import es.pyronixstudio.natures_betrayal.common.config.IModConfig;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.EnumAttackMode;

public class FabricConfigBridge implements IModConfig {
    @Override
    public EnumAttackMode attackMode() {
        return FabricConfig.ATTACK_MODE.get();
    }

    @Override
    public int targetRadius() {
        return FabricConfig.TARGET_RADIUS.get();
    }


}
