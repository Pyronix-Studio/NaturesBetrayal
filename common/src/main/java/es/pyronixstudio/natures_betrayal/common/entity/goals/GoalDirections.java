package es.pyronixstudio.natures_betrayal.common.entity.goals;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;


public final class GoalDirections {

    private GoalDirections() {
    }


    public static Direction primary(LivingEntity from, LivingEntity to) {
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();

        if (Math.abs(dz) >= Math.abs(dx)) {
            return dz >= 0 ? Direction.SOUTH : Direction.NORTH;
        }
        return dx >= 0 ? Direction.EAST : Direction.WEST;
    }

    public static Direction toward(LivingEntity from, LivingEntity to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dz = to.getZ() - from.getZ();
        double horizontal = Math.sqrt(dx * dx + dz * dz);

        if (Math.abs(dy) > horizontal) {
            return dy >= 0 ? Direction.UP : Direction.DOWN;
        }
        return primary(from, to);
    }

    public static Direction secondary(LivingEntity from, LivingEntity to) {
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();

        if (Math.abs(dz) >= Math.abs(dx)) {
            return dx >= 0 ? Direction.EAST : Direction.WEST;
        }
        return dz >= 0 ? Direction.SOUTH : Direction.NORTH;
    }

    public static boolean isDiagonal(LivingEntity from, LivingEntity to) {
        return Math.abs(to.getX() - from.getX()) > 0.5
                && Math.abs(to.getZ() - from.getZ()) > 0.5;
    }
}
