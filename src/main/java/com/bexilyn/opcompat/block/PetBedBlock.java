package com.bexilyn.opcompat.block;

import com.bexilyn.opcompat.block.entity.PetBedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class PetBedBlock extends BaseEntityBlock {

    private VoxelShape shape;


    protected PetBedBlock(
            Properties properties, double height
    ) {

        super(properties);

        this.shape = box(
                0.0D,
                0.0D,
                0.0D,
                16.0D,
                height,
                16.0D
        );
    }

    @Override
    public RenderShape getRenderShape(
            BlockState state
    ) {

        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            net.minecraft.world.level.BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {

        return shape;
    }

    @Override
    public void onRemove(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean isMoving
    ) {

        if (!state.is(newState.getBlock())) {

            BlockEntity blockEntity =
                    level.getBlockEntity(
                            pos
                    );

            if (
                    blockEntity
                            instanceof PetBedBlockEntity<?> petBed
            ) {

                petBed.breakLink();
            }
        }

        super.onRemove(
                state,
                level,
                pos,
                newState,
                isMoving
        );
    }
}