package com.bexilyn.opcompat.registry;

import com.bexilyn.opcompat.OPCompat;
import com.bexilyn.opcompat.block.entity.CatBedBlockEntity;
import com.bexilyn.opcompat.block.entity.DogBedBlockEntity;
import com.bexilyn.opcompat.block.entity.HorseBedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(
                    ForgeRegistries.BLOCK_ENTITY_TYPES,
                    OPCompat.MOD_ID
            );

    public static final RegistryObject<BlockEntityType<HorseBedBlockEntity>> HORSE_BED =
            BLOCK_ENTITIES.register(
                    "horse_bed",
                    () -> BlockEntityType.Builder.of(
                            HorseBedBlockEntity::new,
                            ModBlocks.HORSE_BED.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<DogBedBlockEntity>> DOG_BED =
            BLOCK_ENTITIES.register(
                    "dog_bed",
                    () -> BlockEntityType.Builder.of(
                            DogBedBlockEntity::new,
                            ModBlocks.DOG_BED.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<CatBedBlockEntity>> CAT_BED =
            BLOCK_ENTITIES.register(
                    "cat_bed",
                    () -> BlockEntityType.Builder.of(
                            CatBedBlockEntity::new,
                            ModBlocks.CAT_BED.get()
                    ).build(null)
            );

    private ModBlockEntities() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}