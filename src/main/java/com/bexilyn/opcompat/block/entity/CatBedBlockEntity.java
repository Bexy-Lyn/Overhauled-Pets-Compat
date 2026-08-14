package com.bexilyn.opcompat.block.entity;

import com.bexilyn.opcompat.registry.ModBlockEntities;
import com.dragn0007.dragnpets.entities.cat.OCat;
import com.dragn0007.dragnpets.entities.ocelot.OOcelot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.block.state.BlockState;

public class CatBedBlockEntity
        extends PetBedBlockEntity<TamableAnimal> {

    public CatBedBlockEntity(
            BlockPos pos,
            BlockState state
    ) {

        super(
                ModBlockEntities.CAT_BED.get(),
                TamableAnimal.class,
                pos,
                state
        );
    }

    @Override
    protected boolean isCorrectPetType(
            TamableAnimal pet
    ) {

        return pet instanceof Cat
                || pet instanceof OCat
                || pet instanceof OOcelot;
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