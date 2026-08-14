package com.bexilyn.opcompat.block;

import com.bexilyn.opcompat.block.entity.HorseBedBlockEntity;
import com.bexilyn.opcompat.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HorseBedBlock extends BaseEntityBlock {

    /**
     * A fairly low bed shape:
     * 16 x 2 x 16 pixels.
     */
    private static final VoxelShape SHAPE =
            box(
                    0.0D,
                    0.0D,
                    0.0D,
                    16.0D,
                    2.0D,
                    16.0D
            );

    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty NORTH_EAST = BooleanProperty.create("northeast");
    public static final BooleanProperty NORTH_WEST = BooleanProperty.create("northwest");
    public static final BooleanProperty SOUTH_EAST = BooleanProperty.create("southeast");
    public static final BooleanProperty SOUTH_WEST = BooleanProperty.create("southwest");

    public HorseBedBlock(BlockBehaviour.Properties properties) {
        super(properties);

        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(NORTH, false)
                        .setValue(EAST, false)
                        .setValue(SOUTH, false)
                        .setValue(WEST, false)
                        .setValue(NORTH_EAST, false)
                        .setValue(NORTH_WEST, false)
                        .setValue(SOUTH_EAST, false)
                        .setValue(SOUTH_WEST, false)
        );
    }
    private BlockState updateConnections(
            BlockState state,
            LevelAccessor level,
            BlockPos pos
    ) {
        return state
                .setValue(NORTH, level.getBlockState(pos.north()).is(this))
                .setValue(EAST, level.getBlockState(pos.east()).is(this))
                .setValue(SOUTH, level.getBlockState(pos.south()).is(this))
                .setValue(WEST, level.getBlockState(pos.west()).is(this))

                .setValue(NORTH_EAST,
                        level.getBlockState(pos.north().east()).is(this))
                .setValue(NORTH_WEST,
                        level.getBlockState(pos.north().west()).is(this))
                .setValue(SOUTH_EAST,
                        level.getBlockState(pos.south().east()).is(this))
                .setValue(SOUTH_WEST,
                        level.getBlockState(pos.south().west()).is(this));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return updateConnections(
                this.defaultBlockState(),
                context.getLevel(),
                context.getClickedPos()
        );
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        return updateConnections(state, level, pos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(
                NORTH,
                EAST,
                SOUTH,
                WEST,
                NORTH_EAST,
                NORTH_WEST,
                SOUTH_EAST,
                SOUTH_WEST
        );
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull VoxelShape getShape(
            BlockState state,
            net.minecraft.world.level.BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        return new HorseBedBlockEntity(
                pos,
                state
        );
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {

        if (level.isClientSide) {
            return null;
        }

        return createTickerHelper(
                type,
                ModBlockEntities.HORSE_BED.get(),
                HorseBedBlockEntity::serverTick
        );
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
                    level.getBlockEntity(pos);

            if (blockEntity instanceof HorseBedBlockEntity horseBed) {
                horseBed.breakLink();
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