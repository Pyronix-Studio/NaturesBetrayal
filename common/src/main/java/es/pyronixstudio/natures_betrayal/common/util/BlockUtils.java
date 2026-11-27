package es.pyronixstudio.natures_betrayal.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockUtils {

    private BlockUtils() {
    }

    public static boolean isSolid(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return !state.canBeReplaced() && !state.getCollisionShape(level, pos).isEmpty();
    }
}
