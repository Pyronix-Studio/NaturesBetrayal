package es.pyronixstudio.natures_betrayal.common.mobs.entity.phantom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Navegacion de vuelo propia sobre PathNavigation (la base), usando el
 * FlyNodeEvaluator y el PathFinder vanilla. esto da waypoints 3D naturales sin
 * heredar los atajos de FlyingPathNavigation que cortaban el camino abajo.
 */
public class PhantomFlightNavigation extends PathNavigation {

    private static final int MAX_VISITED = 4096;
    private static final float SEARCH_RANGE = 160.0F;
    private static final int MAX_SCAN = 64;
    private static final int RIDER_HEAD_CLEARANCE = 2;

    public PhantomFlightNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int maxVisitedNodes) {
        FlyNodeEvaluator evaluator = new FlyNodeEvaluator();
        this.nodeEvaluator = evaluator;
        evaluator.setCanPassDoors(true);
        evaluator.setCanFloat(true);
        return new PathFinder(evaluator, Math.max(maxVisitedNodes, MAX_VISITED));
    }

    /** Path exacto: termina en el nodo objetivo, no en un vecino. */
    public Path pathTo(Set<BlockPos> targets) {
        return this.createPath(targets, 8, false, 0, SEARCH_RANGE);
    }

    @Override
    protected Vec3 getTempMobPos() {
        return this.mob.position();
    }

    @Override
    protected boolean canUpdatePath() {
        return true;
    }

    @Override
    protected boolean canMoveDirectly(Vec3 from, Vec3 to) {
        return isClearForMovementBetween(this.mob, from, to, true);
    }

    /**
     * La deteccion de atascado de vanilla borra el path al primer contacto con
     * el suelo de la estructura. Aqui eso deja al phantom sin waypoint abajo;
     * preferimos que siga intentando el path hasta llegar.
     */
    @Override
    protected void doStuckDetection(Vec3 pos) {
        // No-op a proposito.
    }

    @Override
    public boolean isStableDestination(BlockPos pos) {
        return !this.level.getBlockState(pos).isSolid();
    }

    /** Igual que el tick de vuelo vanilla, pero con nuestro PathFinder. */
    @Override
    public void tick() {
        ++this.tick;
        if (this.hasDelayedRecomputation) {
            this.recomputePath();
        }
        if (this.isDone()) {
            return;
        }
        this.followThePath();
        DebugPackets.sendPathFindingPacket(this.level, this.mob, this.path, this.maxDistanceToWaypoint);
        if (this.isDone()) {
            return;
        }
        Vec3 next = this.path.getNextEntityPos(this.mob);
        this.mob.getMoveControl().setWantedPosition(next.x, next.y, next.z, this.speedModifier);
    }

    /**
     * Primera superficie segura en el nivel del jugador o por encima. Nunca
     * devuelve nada por debajo. En el nivel exacto solo vale la columna del
     * jugador; si tiene un bloque encima, se sigue subiendo.
     */
    @Nullable
    public BlockPos findLandingTarget(BlockPos from) {
        int top = Math.min(from.getY() + MAX_SCAN, this.level.getMaxBuildHeight() - 2);
        for (int y = from.getY(); y <= top; y++) {
            int radius = y == from.getY() ? 0 : 3;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) continue;
                    BlockPos pos = new BlockPos(from.getX() + dx, y, from.getZ() + dz);
                    if (isSafeSurface(pos)) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    /** Superficie segura mas alta, usada solo si el objetivo normal no es alcanzable. */
    @Nullable
    public BlockPos findHighestSafeSurface(BlockPos from) {
        int top = Math.min(from.getY() + MAX_SCAN, this.level.getMaxBuildHeight() - 2);
        for (int y = top; y >= from.getY(); y--) {
            for (int dx = -3; dx <= 3; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    BlockPos pos = new BlockPos(from.getX() + dx, y, from.getZ() + dz);
                    if (isSafeSurface(pos)) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    /**
     * El bloque actual es seguro y esta a 1-2 bloques del objetivo. Asi no hace
     * falta clavarse en el nodo exacto para soltar al jinete.
     */
    public boolean isNearSafeSurface(BlockPos target, BlockPos here) {
        if (!isSafeSurface(here)) {
            return false;
        }
        double dx = here.getX() - target.getX();
        double dz = here.getZ() - target.getZ();
        // Misma Y: solo nos movemos horizontalmente desde el nodo objetivo.
        return here.getY() == target.getY() && dx * dx + dz * dz <= 4.0D;
    }

    /** Suelo solido debajo + hueco para el phantom y el jinete. */
    public boolean isSafeSurface(BlockPos pos) {
        if (!this.level.getBlockState(pos.below()).isSolid()) {
            return false;
        }
        // 2 bloques de hueco contando el propio bloque (dy=0 y dy=1).
        for (int dy = 0; dy < RIDER_HEAD_CLEARANCE; dy++) {
            BlockState state = this.level.getBlockState(pos.above(dy));
            if (state.isSolid()) {
                return false;
            }
        }
        return true;
    }
}
