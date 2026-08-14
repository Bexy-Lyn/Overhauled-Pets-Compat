package com.bexilyn.opcompat.block.entity;

import com.dragn0007.dragnlivestock.entities.horse.OHorse;
import com.bexilyn.opcompat.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.level.block.state.BlockState;

public class HorseBedBlockEntity
        extends PetBedBlockEntity<AbstractHorse> {

    public HorseBedBlockEntity(
            BlockPos pos,
            BlockState state
    ) {

        super(
                ModBlockEntities.HORSE_BED.get(),
                AbstractHorse.class,
                pos,
                state
        );
    }

    @Override
    protected boolean isCorrectPetType(
            AbstractHorse horse
    ) {

        /*
         * Explicitly restrict this bed to:
         *
         * vanilla Horse
         * Livestock Overhaul OHorse
         *
         * This excludes:
         * donkey
         * mule
         * skeleton horse
         * zombie horse
         * etc.
         */
        return horse instanceof Horse
                || horse instanceof OHorse;
    }

    @Override
    protected boolean isTamedPet(
            AbstractHorse horse
    ) {

        return horse.isTamed();
    }

    @Override
    protected boolean canReturnPet(
            AbstractHorse horse
    ) {

        /*
         * A horse may return unless somebody is currently riding it.
         */
        return !horse.isVehicle();
    }
}