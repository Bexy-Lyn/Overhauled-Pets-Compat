package com.bexilyn.opcompat.block.entity;

import com.bexilyn.opcompat.config.ModServerConfig;
import com.bexilyn.opcompat.data.PetBedSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class PetBedBlockEntity<T extends LivingEntity>
        extends BlockEntity {

    private static final int CLAIM_CHECK_INTERVAL = 40;

    private final Class<T> searchClass;

    private UUID linkedPetUuid;
    private String linkedPetName;

    private int claimTicker = 0;
    private long lastHandledDay = -1L;

    protected PetBedBlockEntity(
            BlockEntityType<?> blockEntityType,
            Class<T> searchClass,
            BlockPos pos,
            BlockState state
    ) {

        super(
                blockEntityType,
                pos,
                state
        );

        this.searchClass =
                searchClass;
    }

    /*
     * =============================================================
     * TICKING
     * =============================================================
     */

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            PetBedBlockEntity<?> bed
    ) {

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        bed.claimTicker++;

        if (bed.claimTicker >= CLAIM_CHECK_INTERVAL) {

            bed.claimTicker = 0;

            if (!bed.hasLinkedPet()) {

                bed.tryClaimNearbyPet(
                        serverLevel
                );

            } else {

                bed.refreshLinkedPetName(
                        serverLevel
                );
            }
        }

        long dayTime =
                serverLevel.getDayTime();

        long currentDay =
                dayTime / 24000L;

        long timeOfDay =
                dayTime % 24000L;

        boolean isDawn =
                timeOfDay >= 0L
                        && timeOfDay < 20L;

        if (
                isDawn
                        && bed.lastHandledDay != currentDay
        ) {

            bed.lastHandledDay =
                    currentDay;

            bed.handleDawn(
                    serverLevel
            );
        }
    }

    /*
     * =============================================================
     * CLAIMING
     * =============================================================
     */

    private void tryClaimNearbyPet(
            ServerLevel level
    ) {

        double claimRadius =
                getClaimRadius();

        AABB searchArea =
                new AABB(worldPosition)
                        .inflate(claimRadius);

        List<T> nearbyPets =
                level.getEntitiesOfClass(
                        searchClass,
                        searchArea,
                        this::canClaim
                );

        Optional<T> nearest =
                nearbyPets.stream()
                        .min(
                                Comparator.comparingDouble(
                                        pet ->
                                                pet.distanceToSqr(
                                                        Vec3.atCenterOf(
                                                                worldPosition
                                                        )
                                                )
                                )
                        );

        nearest.ifPresent(
                pet ->
                        claimPet(
                                level,
                                pet
                        )
        );
    }

    private boolean canClaim(
            T pet
    ) {

        if (!pet.isAlive()) {
            return false;
        }

        /*
         * Concrete beds decide whether this is:
         *
         * - the correct species/type
         * - tamed
         * - otherwise eligible
         */
        if (!isCorrectPetType(pet)) {
            return false;
        }

        if (!isTamedPet(pet)) {
            return false;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        PetBedSavedData data =
                PetBedSavedData.get(
                        serverLevel
                );

        /*
         * One pet may only own one bed.
         */
        return !data.isLinked(
                pet.getUUID()
        );
    }

    private void claimPet(
            ServerLevel level,
            T pet
    ) {

        linkedPetUuid =
                pet.getUUID();

        linkedPetName =
                pet.getDisplayName()
                        .getString();

        PetBedSavedData
                .get(level)
                .link(
                        linkedPetUuid,
                        level.dimension(),
                        worldPosition
                );

        setChanged();

        level.sendBlockUpdated(
                worldPosition,
                getBlockState(),
                getBlockState(),
                3
        );

        spawnClaimParticles(level);
    }

    /*
     * =============================================================
     * NAME
     * =============================================================
     */

    private void refreshLinkedPetName(
            ServerLevel level
    ) {

        if (linkedPetUuid == null) {
            return;
        }

        Entity entity =
                level.getEntity(
                        linkedPetUuid
                );

        if (
                entity == null
                        || !searchClass.isInstance(entity)
        ) {
            return;
        }

        T pet =
                searchClass.cast(entity);

        String currentName =
                pet.getDisplayName()
                        .getString();

        if (currentName.equals(linkedPetName)) {
            return;
        }

        linkedPetName =
                currentName;

        setChanged();

        level.sendBlockUpdated(
                worldPosition,
                getBlockState(),
                getBlockState(),
                3
        );
    }

    /*
     * =============================================================
     * DAWN RETURN
     * =============================================================
     */

    private void handleDawn(
            ServerLevel level
    ) {

        if (linkedPetUuid == null) {
            return;
        }

        Entity entity =
                level.getEntity(
                        linkedPetUuid
                );

        if (
                entity == null
                        || !searchClass.isInstance(entity)
        ) {
            return;
        }

        T pet =
                searchClass.cast(entity);

        /*
         * Each concrete bed decides whether the pet is currently
         * allowed to be returned.
         *
         * Horse:
         *     not being ridden.
         *
         * Dog/cat:
         *     later: wandering mode only, not stay/follow.
         */
        if (!canReturnPet(pet)) {
            return;
        }

        double returnDistance =
                getReturnDistance();

        double returnDistanceSqr =
                returnDistance
                        * returnDistance;

        double distanceSqr =
                pet.distanceToSqr(
                        Vec3.atCenterOf(
                                worldPosition
                        )
                );

        if (distanceSqr <= returnDistanceSqr) {
            return;
        }

        Optional<Vec3> safePosition =
                findSafeReturnPosition(
                        level,
                        pet
                );

        safePosition.ifPresent(
                position ->
                        teleportPet(
                                pet,
                                position
                        )
        );
    }

    /*
     * =============================================================
     * SAFE POSITION
     * =============================================================
     */

    private Optional<Vec3> findSafeReturnPosition(
            ServerLevel level,
            T pet
    ) {

        int safePositionRadius =
                getSafePositionRadius();

        for (
                int radius = 0;
                radius <= safePositionRadius;
                radius++
        ) {

            for (
                    int x = -radius;
                    x <= radius;
                    x++
            ) {

                for (
                        int z = -radius;
                        z <= radius;
                        z++
                ) {

                    if (
                            radius > 0
                                    && Math.abs(x) != radius
                                    && Math.abs(z) != radius
                    ) {
                        continue;
                    }

                    BlockPos candidate =
                            worldPosition.offset(
                                    x,
                                    1,
                                    z
                            );

                    Optional<Vec3> result =
                            checkCandidatePosition(
                                    level,
                                    pet,
                                    candidate
                            );

                    if (result.isPresent()) {
                        return result;
                    }
                }
            }
        }

        return Optional.empty();
    }

    private Optional<Vec3> checkCandidatePosition(
            ServerLevel level,
            T pet,
            BlockPos candidate
    ) {

        Vec3 target =
                Vec3.atBottomCenterOf(
                        candidate
                );

        AABB movedBox =
                pet.getBoundingBox()
                        .move(
                                target.x - pet.getX(),
                                target.y - pet.getY(),
                                target.z - pet.getZ()
                        );

        if (!level.noCollision(
                pet,
                movedBox
        )) {
            return Optional.empty();
        }

        BlockPos floor =
                candidate.below();

        if (
                level.getBlockState(floor)
                        .getCollisionShape(
                                level,
                                floor
                        )
                        .isEmpty()
        ) {
            return Optional.empty();
        }

        return Optional.of(
                target
        );
    }

    private void teleportPet(
            T pet,
            Vec3 position
    ) {

        /*
         * Usually canReturnPet() should already prevent this,
         * but this makes the teleport itself safe.
         */
        pet.stopRiding();

        pet.teleportTo(
                position.x,
                position.y,
                position.z
        );

        pet.setDeltaMovement(
                Vec3.ZERO
        );

        pet.fallDistance =
                0.0F;
    }

    /*
     * =============================================================
     * LINK MANAGEMENT
     * =============================================================
     */

    public boolean hasLinkedPet() {
        return linkedPetUuid != null;
    }

    public UUID getLinkedPetUuid() {
        return linkedPetUuid;
    }

    public String getLinkedPetName() {
        return linkedPetName;
    }

    public void breakLink() {

        if (linkedPetUuid == null) {
            return;
        }

        if (level instanceof ServerLevel serverLevel) {

            PetBedSavedData
                    .get(serverLevel)
                    .unlink(
                            linkedPetUuid
                    );
        }

        linkedPetUuid = null;
        linkedPetName = null;

        setChanged();

        if (level instanceof ServerLevel serverLevel) {

            serverLevel.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3
            );
        }
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

        super.saveAdditional(
                tag
        );

        if (linkedPetUuid != null) {

            tag.putUUID(
                    "LinkedPet",
                    linkedPetUuid
            );
        }

        if (linkedPetName != null) {

            tag.putString(
                    "LinkedPetName",
                    linkedPetName
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

        super.load(
                tag
        );

        linkedPetUuid =
                tag.hasUUID("LinkedPet")
                        ? tag.getUUID("LinkedPet")
                        : null;

        linkedPetName =
                tag.contains("LinkedPetName")
                        ? tag.getString("LinkedPetName")
                        : null;

        lastHandledDay =
                tag.getLong(
                        "LastHandledDay"
                );
    }

    /*
     * =============================================================
     * BED-SPECIFIC BEHAVIOUR
     * =============================================================
     */

    private void spawnClaimParticles(
            ServerLevel level
    ) {

        level.sendParticles(
                ParticleTypes.HAPPY_VILLAGER,

                worldPosition.getX() + 0.5D,
                worldPosition.getY() + 0.6D,
                worldPosition.getZ() + 0.5D,

                10,

                0.45D,
                0.20D,
                0.45D,

                0.05D
        );
    }

    /**
     * Checks whether this particular entity belongs to this bed type.
     * Example:
     * HorseBed -> vanilla horses and OHorse
     * DogBed   -> vanilla Wolf and ODog
     * CatBed   -> vanilla Cat and OCat
     */
    protected abstract boolean isCorrectPetType(T pet);

    /**
     * Checks whether the animal has actually been tamed.
     */
    protected abstract boolean isTamedPet(T pet);

    /**
     * Determines whether a linked pet may be teleported at dawn.
     */
    protected abstract boolean canReturnPet(T pet);

    protected double getClaimRadius() {

        return ModServerConfig
                .BED_CLAIM_RADIUS
                .get();
    }

    protected double getReturnDistance() {

        return ModServerConfig
                .BED_RETURN_DISTANCE
                .get();
    }

    protected int getSafePositionRadius() {

        return ModServerConfig
                .BED_SAFE_POSITION_RADIUS
                .get();
    }
}