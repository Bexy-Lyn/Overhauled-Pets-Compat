package com.bexilyn.opcompat.client;

import com.bexilyn.opcompat.OPCompat;
import com.bexilyn.opcompat.block.entity.PetBedBlockEntity;
import com.bexilyn.opcompat.registry.ModBlocks;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = OPCompat.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public final class ModBlockColors {

    private ModBlockColors() {
    }

    @SubscribeEvent
    public static void registerBlockColors(
            RegisterColorHandlersEvent.Block event
    ) {

        event.register(
                (state, level, pos, tintIndex) -> {

                    if (tintIndex != 0) {
                        return 0xFFFFFF;
                    }

                    /*
                     * Item model / inventory rendering.
                     */
                    if (level == null || pos == null) {
                        return 0xFFFFFF;
                    }

                    BlockEntity blockEntity =
                            level.getBlockEntity(pos);

                    if (!(blockEntity
                            instanceof PetBedBlockEntity<?> petBed)) {

                        return 0xFFFFFF;
                    }

                    return dyeColorToRgb(
                            petBed.getBedColor()
                    );
                },

                ModBlocks.DOG_BED.get(),
                ModBlocks.CAT_BED.get()
        );
    }

    private static int dyeColorToRgb(
            DyeColor color
    ) {

        float[] rgb =
                color.getTextureDiffuseColors();

        int red =
                (int) (rgb[0] * 255.0F);

        int green =
                (int) (rgb[1] * 255.0F);

        int blue =
                (int) (rgb[2] * 255.0F);

        return (red << 16)
                | (green << 8)
                | blue;
    }
}