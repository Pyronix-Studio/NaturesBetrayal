package es.pyronixstudio.natures_betrayal.common.entity.custom_phantom;

import es.pyronixstudio.natures_betrayal.common.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
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
    /** El phantom no buscaba "el primero" sino hasta el techo del mundo. */
    private static final int MAX_LANDING_RISE = 6;
    /** 2 para el cuerpo del jinete + 1 por la fracción de Y del phantom. */
    private static final int RIDER_HEAD_CLEARANCE = 3;

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
        return !BlockUtils.isSolid(this.level, pos);
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
        int top = Math.min(from.getY() + MAX_LANDING_RISE, this.level.getMaxBuildHeight() - 2);
        for (int y = from.getY(); y <= top; y++) {
            // En el nivel del jugador también miramos las columnas de al lado:
            // si tiene techo, antes de subir al tejado probamos a dejar al
            // jinete junto a él, dentro de la cueva.
            int radius = Math.min(3, 1 + y - from.getY());
            // Anillos desde el centro: primero lo más cerca del objetivo.
            for (int r = 0; r <= radius; r++) {
                for (int dx = -r; dx <= r; dx++) {
                    for (int dz = -r; dz <= r; dz++) {
                        if (Math.max(Math.abs(dx), Math.abs(dz)) != r) continue;
                        BlockPos pos = new BlockPos(from.getX() + dx, y, from.getZ() + dz);
                        if (isSafeSurface(pos)) {
                            return pos;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Cubo cerrado: primer bloque solido en la columna de {@code from} antes
     * de llegar a la altura de {@code to}. Si no hay techo, devuelve -1.
     */
    public int firstCeilingBelow(BlockPos from, int toY) {
        int top = Math.min(toY, this.level.getMaxBuildHeight() - 1);
        for (int y = from.getY() + 1; y <= top; y++) {
            if (BlockUtils.isSolid(this.level, from.atY(y))) {
                return y;
            }
        }
        return -1;
    }

    /**
     * Donde parar bajo un techo: el mob queda con los pies en {@code ceiling-2},
     * asi tiene un bloque de hueco sobre la cabeza y puede romper el techo.
     * Si esa posicion es solida, bajamos hasta encontrar hueco.
     */
    @Nullable
    public BlockPos standUnderCeiling(BlockPos column, int ceilingY) {
        BlockPos stand = column.atY(ceilingY - 2);
        for (int y = stand.getY(); y >= column.getY() - 6; y--) {
            BlockPos p = stand.atY(y);
            if (!BlockUtils.isSolid(this.level, p)
                    && !BlockUtils.isSolid(this.level, p.above())) {
                return p;
            }
        }
        return null;
    }

    /** Superficie segura mas alta, usada solo si el objetivo normal no es alcanzable. */
    @Nullable
    public BlockPos findHighestSafeSurface(BlockPos from) {
        // Antes empezaba en getMaxBuildHeight(): por eso se subía al máximo.
        int top = Math.min(from.getY() + MAX_LANDING_RISE, this.level.getMaxBuildHeight() - 2);
        for (int y = top; y >= from.getY(); y--) {
            for (int r = 0; r <= 3; r++) {
                for (int dx = -r; dx <= r; dx++) {
                    for (int dz = -r; dz <= r; dz++) {
                        if (Math.max(Math.abs(dx), Math.abs(dz)) != r) continue;
                        BlockPos pos = new BlockPos(from.getX() + dx, y, from.getZ() + dz);
                        if (isSafeSurface(pos)) {
                            return pos;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * El bloque actual es seguro y esta cerca del objetivo. No hace falta
     * clavarse en la coordenada exacta del nodo: basta una superficie segura
     * a 1-2 bloques en horizontal y como mucho 1 bloque arriba/abajo.
     */
    public boolean isNearSafeSurface(BlockPos target, BlockPos here) {
        if (!isSafeSurface(here)) {
            return false;
        }
        double dx = here.getX() - target.getX();
        double dz = here.getZ() - target.getZ();
        double dy = here.getY() - target.getY();
        // Antes exigía el bloque casi exacto; eso obligaba a repath todo el rato.
        return dx * dx + dz * dz <= 16.0D && Math.abs(dy) <= 2;
    }

    /** Suelo solido debajo + hueco para el phantom y el jinete. */
    public boolean isSafeSurface(BlockPos pos) {
        if (!BlockUtils.isSolid(this.level, pos.below())) {
            return false;
        }
        // 2 bloques de hueco contando el propio bloque (dy=0 y dy=1).
        for (int dy = 0; dy < RIDER_HEAD_CLEARANCE; dy++) {
            if (BlockUtils.isSolid(this.level, pos.above(dy))) {
                return false;
            }
        }
        return true;
    }
}
