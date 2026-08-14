package com.bexilyn.opcompat.block.entity;

import com.bexilyn.opcompat.block.ColoredPetBedBlock;
import com.bexilyn.opcompat.registry.ModBlockEntities;
import com.dragn0007.dragnpets.entities.cat.OCat;
import com.dragn0007.dragnpets.entities.ocelot.OOcelot;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.item.DyeColor;
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

    private DyeColor getCollarColor(
            TamableAnimal pet
    ) {

        if (pet instanceof Cat cat) {
            return cat.getCollarColor();
        }

        if (pet instanceof OCat cat) {
            return cat.getCollarColor();
        }

        if (pet instanceof OOcelot ocelot) {
            return ocelot.getCollarColor();
        }

        return DyeColor.WHITE;
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