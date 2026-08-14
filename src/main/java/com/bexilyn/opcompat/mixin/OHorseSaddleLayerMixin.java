package com.bexilyn.opcompat.mixin;

import com.bexilyn.opcompat.compat.TransmogHelper;
import com.dragn0007.dragnlivestock.entities.horse.OHorseSaddleLayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = OHorseSaddleLayer.class, remap = false)
public abstract class OHorseSaddleLayerMixin {

    /**
     * Replaces the saddle ItemStack local variable used by
     * OHorseSaddleLayer#render with its Transmog appearance.
     *
     * The actual equipped saddle remains untouched.
     */
    @ModifyVariable(
            method = "render",
            at = @At("STORE"),
            ordinal = 0,
            remap = false
    )
    private ItemStack opcompat$useTransmogSaddle(ItemStack saddleStack) {
        return TransmogHelper.getVisualStack(saddleStack);
    }
}