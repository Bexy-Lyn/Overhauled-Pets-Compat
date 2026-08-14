package com.bexilyn.opcompat.block;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public abstract class ColoredPetBedBlock extends PetBedBlock {

    public static final EnumProperty<DyeColor> COLOR =
            EnumProperty.create(
                    "color",
                    DyeColor.class
            );

    protected ColoredPetBedBlock(
            Properties properties,
            double height
    ) {

        super(
                properties,
                height
        );

        registerDefaultState(
                defaultBlockState()
                        .setValue(
                                COLOR,
                                DyeColor.BROWN
                        )
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<
                    net.minecraft.world.level.block.Block,
                    BlockState
                    > builder
    ) {

        /*
         * PetBedBlock already defines FACING,
         * so make sure its implementation calls super as described below.
         */
        super.createBlockStateDefinition(
                builder
        );

        builder.add(
                COLOR
        );
    }
}