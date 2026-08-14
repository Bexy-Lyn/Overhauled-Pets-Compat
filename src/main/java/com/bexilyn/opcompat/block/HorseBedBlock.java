package com.bexilyn.opcompat.block;

import com.bexilyn.opcompat.block.entity.HorseBedBlockEntity;
import com.bexilyn.opcompat.block.entity.PetBedBlockEntity;
import com.bexilyn.opcompat.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

public class HorseBedBlock extends PetBedBlock {

    public static final BooleanProperty NORTH =
            BooleanProperty.create("north");

    public static final BooleanProperty EAST =
            BooleanProperty.create("east");

    public static final BooleanProperty SOUTH =
            BooleanProperty.create("south");

    public static final BooleanProperty WEST =
            BooleanProperty.create("west");

    public static final BooleanProperty NORTHEAST =
            BooleanProperty.create("northeast");

    public static final BooleanProperty NORTHWEST =
            BooleanProperty.create("northwest");

    public static final BooleanProperty SOUTHEAST =
            BooleanProperty.create("southeast");

    public static final BooleanProperty SOUTHWEST =
            BooleanProperty.create("southwest");

    public HorseBedBlock(
            Properties properties
    ) {
        super(properties, 2.0D);

        this.registerDefaultState(
                this.stateDefinition
                        .any()
                        .setValue(NORTH, false)
                        .setValue(EAST, false)
                        .setValue(SOUTH, false)
                        .setValue(WEST, false)
                        .setValue(NORTHEAST, false)
                        .setValue(NORTHWEST, false)
                        .setValue(SOUTHEAST, false)
                        .setValue(SOUTHWEST, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        super.createBlockStateDefinition(builder);

        builder.add(
                NORTH,
                EAST,
                SOUTH,
                WEST,
                NORTHEAST,
                NORTHWEST,
                SOUTHEAST,
                SOUTHWEST
        );
    }

    private BlockState updateConnections(
            BlockState state,
            LevelAccessor level,
            BlockPos pos
    ) {
        return state
                .setValue(
                        NORTH,
                        level.getBlockState(pos.north()).is(this)
                )
                .setValue(
                        EAST,
                        level.getBlockState(pos.east()).is(this)
                )
                .setValue(
                        SOUTH,
                        level.getBlockState(pos.south()).is(this)
                )
                .setValue(
                        WEST,
                        level.getBlockState(pos.west()).is(this)
                )
                .setValue(
                        NORTHEAST,
                        level.getBlockState(pos.north().east()).is(this)
                )
                .setValue(
                        NORTHWEST,
                        level.getBlockState(pos.north().west()).is(this)
                )
                .setValue(
                        SOUTHEAST,
                        level.getBlockState(pos.south().east()).is(this)
                )
                .setValue(
                        SOUTHWEST,
                        level.getBlockState(pos.south().west()).is(this)
                );
    }

    @Override
    public BlockState getStateForPlacement(
            BlockPlaceContext context
    ) {
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
        return updateConnections(
                state,
                level,
                pos
        );
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
                ModBlockEntities.HORSE_BED.get(),
                PetBedBlockEntity::serverTick
        );
    }
}