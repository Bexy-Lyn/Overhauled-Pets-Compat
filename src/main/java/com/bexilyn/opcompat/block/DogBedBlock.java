package com.bexilyn.opcompat.block;

import com.bexilyn.opcompat.block.entity.DogBedBlockEntity;
import com.bexilyn.opcompat.block.entity.PetBedBlockEntity;
import com.bexilyn.opcompat.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DogBedBlock extends ColoredPetBedBlock {

    public DogBedBlock(
            Properties properties
    ) {

        super(properties, 8.0D);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(
            BlockPos pos,
            BlockState state
    ) {

        return new DogBedBlockEntity(
                pos,
                state
        );
    }

    @Nullable
    @Override
    public <T extends BlockEntity>
    BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {

        if (level.isClientSide) {
            return null;
        }

        return createTickerHelper(
                type,
                ModBlockEntities.DOG_BED.get(),
                PetBedBlockEntity::serverTick
        );
    }
}