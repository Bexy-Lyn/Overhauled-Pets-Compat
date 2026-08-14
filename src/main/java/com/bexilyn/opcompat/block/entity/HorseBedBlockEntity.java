package com.bexilyn.opcompat.block.entity;

import com.bexilyn.opcompat.data.HorseBedSavedData;
import com.bexilyn.opcompat.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class HorseBedBlockEntity extends BlockEntity {

    /**
     * Horses within this distance may automatically claim the bed.
     */
    private static final double CLAIM_RADIUS = 10.0D;

    /**
     * At dawn, horses farther away than this are returned.
     */
    private static final double RETURN_DISTANCE = 50.0D;

    private static final double RETURN_DISTANCE_SQR =
            RETURN_DISTANCE * RETURN_DISTANCE;

    /**
     * Claim checks only need to happen occasionally.
     * 40 ticks = roughly 2 seconds.
     */
    private static final int CLAIM_CHECK_INTERVAL = 40;

    /**
     * Search this many blocks horizontally around the bed
     * when looking for somewhere safe to place the horse.
     */
    private static final int SAFE_POSITION_RADIUS = 4;

    private UUID linkedHorseUuid;

    private int claimTicker = 0;

    /**
     * Prevents the dawn action from running multiple times
     * during the same dawn.
     */
    private long lastHandledDay = -1L;


    public HorseBedBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        super(
                ModBlockEntities.HORSE_BED.get(),
                pos,
                state
        );
    }

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            HorseBedBlockEntity bed
    ) {

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        /*
         * ---------------------------------------------------------
         * AUTO-CLAIM
         * ---------------------------------------------------------
         */

        bed.claimTicker++;

        if (bed.claimTicker >= CLAIM_CHECK_INTERVAL) {

            bed.claimTicker = 0;

            if (!bed.hasLinkedHorse()) {
                bed.tryClaimNearbyHorse(serverLevel);
            }
        }

        /*
         * ---------------------------------------------------------
         * DAWN CHECK
         * ---------------------------------------------------------
         */

        long dayTime =
                serverLevel.getDayTime();

        long currentDay =
                dayTime / 24000L;

        long timeOfDay =
                dayTime % 24000L;

        /*
         * Use a small window rather than requiring exactly tick 0.
         * This is more robust when time is advanced through sleep
         * or commands.
         */
        boolean isDawn =
                timeOfDay >= 0L
                        && timeOfDay < 20L;

        if (isDawn
                && bed.lastHandledDay != currentDay) {

            bed.lastHandledDay = currentDay;

            bed.handleDawn(serverLevel);
        }
    }

    /*
     * =============================================================
     * CLAIMING
     * =============================================================
     */

    private void tryClaimNearbyHorse(
            ServerLevel level
    ) {

        AABB searchArea =
                new AABB(worldPosition)
                        .inflate(CLAIM_RADIUS);

        List<AbstractHorse> nearbyHorses =
                level.getEntitiesOfClass(
                        AbstractHorse.class,
                        searchArea,
                        this::canClaim
                );

        Optional<AbstractHorse> nearest =
                nearbyHorses.stream()
                        .min(
                                Comparator.comparingDouble(
                                        horse ->
                                                horse.distanceToSqr(
                                                        Vec3.atCenterOf(
                                                                worldPosition
                                                        )
                                                )
                                )
                        );

        nearest.ifPresent(
                horse ->
                        claimHorse(
                                level,
                                horse
                        )
        );
    }

    private boolean canClaim(
            AbstractHorse horse
    ) {

        if (!horse.isAlive()) {
            return false;
        }

        /*
         * Prevent wild horses from stealing beds.
         */
        if (!horse.isTamed()) {
            return false;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        HorseBedSavedData data =
                HorseBedSavedData.get(serverLevel);

        /*
         * A horse can belong to only one bed.
         */
        return !data.isLinked(
                horse.getUUID()
        );
    }

    private void claimHorse(
            ServerLevel level,
            AbstractHorse horse
    ) {

        linkedHorseUuid =
                horse.getUUID();

        HorseBedSavedData.get(level)
                .link(
                        linkedHorseUuid,
                        level.dimension(),
                        worldPosition
                );

        setChanged();
    }

    /*
     * =============================================================
     * DAWN RETURN
     * =============================================================
     */

    private void handleDawn(
            ServerLevel level
    ) {

        if (linkedHorseUuid == null) {
            return;
        }

        Entity entity =
                level.getEntity(
                        linkedHorseUuid
                );

        /*
         * IMPORTANT:
         *
         * getEntity(UUID) only gives us a currently loaded entity.
         *
         * We'll extend this later with last-known chunk tracking
         * so unloaded horses can be retrieved as well.
         */
        if (!(entity instanceof AbstractHorse horse)) {
            return;
        }

        /*
         * Do not teleport a horse while somebody is riding it.
         *
         * An entity is a "vehicle" when it currently has passengers.
         */
        if (horse.isVehicle()) {
            return;
        }

        double distanceSqr =
                horse.distanceToSqr(
                        Vec3.atCenterOf(
                                worldPosition
                        )
                );

        if (distanceSqr <= RETURN_DISTANCE_SQR) {
            return;
        }

        Optional<Vec3> safePosition =
                findSafeReturnPosition(
                        level,
                        horse
                );

        safePosition.ifPresent(
                position ->
                        teleportHorse(
                                horse,
                                position
                        )
        );
    }

    private Optional<Vec3> findSafeReturnPosition(
            ServerLevel level,
            AbstractHorse horse
    ) {

        /*
         * Try the bed itself first.
         *
         * Then search outward in rings.
         */
        for (int radius = 0;
             radius <= SAFE_POSITION_RADIUS;
             radius++) {

            for (int x = -radius;
                 x <= radius;
                 x++) {

                for (int z = -radius;
                     z <= radius;
                     z++) {

                    /*
                     * For radii > 0, only inspect the outside
                     * of each ring.
                     */
                    if (radius > 0
                            && Math.abs(x) != radius
                            && Math.abs(z) != radius) {

                        continue;
                    }

                    BlockPos candidate =
                            worldPosition.offset(
                                    x,
                                    1,
                                    z
                            );

                    Optional<Vec3> position =
                            checkCandidatePosition(
                                    level,
                                    horse,
                                    candidate
                            );

                    if (position.isPresent()) {
                        return position;
                    }
                }
            }
        }

        return Optional.empty();
    }

    private Optional<Vec3> checkCandidatePosition(
            ServerLevel level,
            AbstractHorse horse,
            BlockPos candidate
    ) {

        /*
         * Put the horse at the bottom-center of this block.
         */
        Vec3 target =
                Vec3.atBottomCenterOf(
                        candidate
                );

        /*
         * Move the horse's current bounding box to the proposed
         * destination and ask Minecraft whether it collides.
         */
        AABB movedBox =
                horse.getBoundingBox()
                        .move(
                                target.x - horse.getX(),
                                target.y - horse.getY(),
                                target.z - horse.getZ()
                        );

        if (!level.noCollision(
                horse,
                movedBox
        )) {
            return Optional.empty();
        }

        /*
         * We don't want to place the horse over a pit.
         */
        BlockPos floor =
                candidate.below();

        if (level.getBlockState(floor)
                .getCollisionShape(
                        level,
                        floor
                )
                .isEmpty()) {

            return Optional.empty();
        }

        return Optional.of(target);
    }

    private void teleportHorse(
            AbstractHorse horse,
            Vec3 position
    ) {

        horse.stopRiding();

        horse.teleportTo(
                position.x,
                position.y,
                position.z
        );

        horse.setDeltaMovement(
                Vec3.ZERO
        );

        horse.fallDistance = 0.0F;
    }

    /*
     * =============================================================
     * LINK MANAGEMENT
     * =============================================================
     */

    public boolean hasLinkedHorse() {
        return linkedHorseUuid != null;
    }

    public UUID getLinkedHorseUuid() {
        return linkedHorseUuid;
    }

    public void breakLink() {

        if (linkedHorseUuid == null) {
            return;
        }

        if (level instanceof ServerLevel serverLevel) {

            HorseBedSavedData.get(serverLevel)
                    .unlink(
                            linkedHorseUuid
                    );
        }

        linkedHorseUuid = null;

        setChanged();
    }

    /*
     * =============================================================
     * NBT
     * =============================================================
     */

    @Override
    protected void saveAdditional(
            CompoundTag tag
    ) {

        super.saveAdditional(tag);

        if (linkedHorseUuid != null) {
            tag.putUUID(
                    "LinkedHorse",
                    linkedHorseUuid
            );
        }

        tag.putLong(
                "LastHandledDay",
                lastHandledDay
        );
    }

    @Override
    public void load(
            CompoundTag tag
    ) {

        super.load(tag);

        if (tag.hasUUID("LinkedHorse")) {

            linkedHorseUuid =
                    tag.getUUID(
                            "LinkedHorse"
                    );

        } else {

            linkedHorseUuid = null;
        }

        lastHandledDay =
                tag.getLong(
                        "LastHandledDay"
                );
    }

}