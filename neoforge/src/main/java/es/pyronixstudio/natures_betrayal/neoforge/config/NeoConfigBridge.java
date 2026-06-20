package es.pyronixstudio.natures_betrayal.neoforge.config;

import es.pyronixstudio.natures_betrayal.common.config.IModConfig;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.EnumAttackMode;

public class NeoConfigBridge implements IModConfig {
    @Override
    public EnumAttackMode attackMode() {
        return NeoConfig.ATTACK_MODE.get();
    }

    @Override
    public int targetRadius() {
        return NeoConfig.TARGET_RADIUS.get();
    }
}
