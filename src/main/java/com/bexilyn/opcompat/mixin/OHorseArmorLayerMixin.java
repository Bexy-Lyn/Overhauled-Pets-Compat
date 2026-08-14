package com.bexilyn.opcompat.mixin;

import com.bexilyn.opcompat.compat.TransmogHelper;
import com.dragn0007.dragnlivestock.entities.horse.OHorseArmorLayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = OHorseArmorLayer.class, remap = false)
public abstract class OHorseArmorLayerMixin {

    /**
     * Replaces the armor ItemStack local variable used by
     * OHorseArmorLayer#render with its Transmog appearance.
     * The actual equipped armor remains untouched.
     */
    @ModifyVariable(
            method = "render",
            at = @At("STORE"),
            ordinal = 0,
            remap = false
    )
    private ItemStack opcompat$useTransmogHorseArmor(ItemStack armorStack) {
        return TransmogHelper.getVisualStack(armorStack);
    }
}