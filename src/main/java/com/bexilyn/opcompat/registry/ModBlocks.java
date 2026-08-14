package com.bexilyn.opcompat.registry;

import com.bexilyn.opcompat.OPCompat;
import com.bexilyn.opcompat.block.CatBedBlock;
import com.bexilyn.opcompat.block.DogBedBlock;
import com.bexilyn.opcompat.block.HorseBedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(
                    ForgeRegistries.BLOCKS,
                    OPCompat.MOD_ID
            );

    public static final RegistryObject<Block> HORSE_BED =
            BLOCKS.register(
                    "horse_bed",
                    () -> new HorseBedBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(0.6F)
                                    .sound(SoundType.WOOL)
                                    .noOcclusion()
                    )
            );


    public static final RegistryObject<Block> DOG_BED =
            BLOCKS.register(
                    "dog_bed",
                    () -> new DogBedBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(0.6F)
                                    .sound(SoundType.WOOL)
                                    .noOcclusion()
                    )
            );


    public static final RegistryObject<Block>
            CAT_BED =
            BLOCKS.register(
                    "cat_bed",
                    () -> new CatBedBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(0.6F)
                                    .sound(SoundType.WOOL)
                                    .noOcclusion()
                    )
            );

    private ModBlocks() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}