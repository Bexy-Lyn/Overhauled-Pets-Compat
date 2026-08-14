package com.bexilyn.opcompat.registry;

import com.bexilyn.opcompat.OPCompat;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(
                    Registries.CREATIVE_MODE_TAB,
                    OPCompat.MOD_ID
            );

    public static final RegistryObject<CreativeModeTab> INVISTAL_TAB =
            CREATIVE_TABS.register(
                    "opcompat",
                    () -> CreativeModeTab.builder()
                            .title(
                                    Component.translatable(
                                            "creativetab.opcompat"
                                    )
                            )
                            .icon(
                                    () -> new ItemStack(
                                            ModItems.DOG_BED_ITEM.get()
                                    )
                            )
                            .displayItems(
                                    (parameters, output) -> {
                                        output.accept(
                                                ModItems.HORSE_BED_ITEM.get()
                                        );
                                        output.accept(
                                                ModItems.DOG_BED_ITEM.get()
                                        );
                                        output.accept(
                                                ModItems.CAT_BED_ITEM.get()
                                        );
                                    }
                            )
                            .build()
            );

    private ModCreativeTabs() {
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}