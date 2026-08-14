package com.bexilyn.opcompat.block.entity;

import com.bexilyn.opcompat.block.ColoredPetBedBlock;
import com.bexilyn.opcompat.registry.ModBlockEntities;
import com.dragn0007.dragnpets.entities.dog.ODog;
import com.dragn0007.dragnpets.entities.wolf.OWolf;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.DyeColor;
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

    private DyeColor getCollarColor(
            TamableAnimal pet
    ) {

        if (pet instanceof Wolf wolf) {
            return wolf.getCollarColor();
        }

        if (pet instanceof ODog dog) {
            return dog.getCollarColor();
        }

        if (pet instanceof OWolf wolf) {
            return wolf.getCollarColor();
        }

        return DyeColor.WHITE;
    }

    @Override
    protected boolean isCorrectPetType(
            TamableAnimal pet
    ) {

        return pet instanceof Wolf
                || pet instanceof ODog
                || pet instanceof OWolf;
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
        return !pet.isOrderedToSit()
                && !pet.isVehicle();
    }

    @Override
    protected void onPetClaimed(
            ServerLevel level,
            TamableAnimal pet
    ) {

        setBedColor(
                level,
                getCollarColor(pet)
        );
    }

    @Override
    protected void refreshPetAppearance(
            ServerLevel level,
            TamableAnimal pet
    ) {

        setBedColor(
                level,
                getCollarColor(pet)
        );
    }

}