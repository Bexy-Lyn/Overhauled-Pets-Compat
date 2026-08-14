package com.bexilyn.opcompat.registry;

import com.bexilyn.opcompat.OPCompat;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(
                    ForgeRegistries.ITEMS,
                    OPCompat.MOD_ID
            );

    public static final RegistryObject<Item> HORSE_BED_ITEM =
            ITEMS.register("horse_bed", () ->
                    new BlockItem(
                            ModBlocks.HORSE_BED.get(),
                            new Item.Properties()
                    )
            );

    public static final RegistryObject<Item> DOG_BED_ITEM =
            ITEMS.register("dog_bed", () ->
                    new BlockItem(
                            ModBlocks.DOG_BED.get(),
                            new Item.Properties()
                    )
            );

    public static final RegistryObject<Item> CAT_BED_ITEM =
            ITEMS.register("cat_bed", () ->
                    new BlockItem(
                            ModBlocks.CAT_BED.get(),
                            new Item.Properties()
                    )
            );

    private ModItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}