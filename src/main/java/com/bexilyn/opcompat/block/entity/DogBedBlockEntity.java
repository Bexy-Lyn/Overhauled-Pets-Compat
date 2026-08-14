package com.bexilyn.opcompat.block.entity;

import com.dragn0007.dragnpets.entities.dog.ODog;
import com.bexilyn.opcompat.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.block.state.BlockState;

public class DogBedBlockEntity
        extends PetBedBlockEntity<TamableAnimal> {

    public DogBedBlockEntity(
            BlockPos pos,
            BlockState state
    ) {

        super(
                ModBlockEntities.DOG_BED.get(),
                TamableAnimal.class,
                pos,
                state
        );
    }

    @Override
    protected boolean isCorrectPetType(
            TamableAnimal pet
    ) {

        return pet instanceof Wolf
                || pet instanceof ODog;
    }

    @Override
    protected boolean isTamedPet(
            TamableAnimal pet
    ) {

        return pet.isTame();
    }

    @Override
    protected boolean canReturnPet(
            TamableAnimal pet
    ) {

        /*
         * TODO: Placeholder rule.
         *
         * We'll refine this once we implement the exact
         * stay/follow/wander behaviour.
         */
        return !pet.isOrderedToSit()
                && !pet.isVehicle();
    }
}