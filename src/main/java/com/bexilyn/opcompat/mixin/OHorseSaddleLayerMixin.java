package com.bexilyn.opcompat.mixin;

import com.dragn0007.dragnlivestock.entities.horse.OHorse;
import com.dragn0007.dragnlivestock.entities.horse.OHorseSaddleLayer;
import com.bexilyn.opcompat.compat.TransmogHelper;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = OHorseSaddleLayer.class, remap = false)
public abstract class OHorseSaddleLayerMixin {

    /**
     * Replaces the saddle ItemStack used by the horse saddle renderer
     * with its Transmog appearance.
     * The real saddle equipped on the horse remains unchanged.
     */
    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/dragn0007/dragnlivestock/entities/horse/OHorse;getSaddleItem()Lnet/minecraft/world/item/ItemStack;",
                    remap = false
            ),
            remap = false
    )
    private ItemStack optransmog$useTransmogSaddle(OHorse horse) {
        return TransmogHelper.getVisualStack(horse.getSaddleItem());
    }
}