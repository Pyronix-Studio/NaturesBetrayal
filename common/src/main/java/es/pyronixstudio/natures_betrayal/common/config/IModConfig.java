package es.pyronixstudio.natures_betrayal.common.config;

import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.EnumAttackMode;

public interface IModConfig {

    EnumAttackMode attackMode();
    int targetRadius();

}
