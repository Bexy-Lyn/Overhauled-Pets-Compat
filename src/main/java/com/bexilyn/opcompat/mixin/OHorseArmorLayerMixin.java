package com.bexilyn.opcompat.mixin;

import com.bexilyn.opcompat.compat.TransmogHelper;
import com.dragn0007.dragnlivestock.entities.horse.OHorseArmorLayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = OHorseArmorLayer.class, remap = false)
public abstract class OHorseArmorLayerMixin {

    /**
     * OHorseArmorLayer obtains the equipped horse armor with:
     * List<ItemStack> armorSlots = (List) animatable.m_6168_();
     * ItemStack armorItemStack = (ItemStack) armorSlots.get(2);
     * We intercept that List#get call and return the Transmog appearance
     * instead of the real ItemStack.
     * The actual horse equipment is never modified.
     */
    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;get(I)Ljava/lang/Object;",
                    ordinal = 0,
                    remap = false
            ),
            remap = false
    )
    private Object optransmog$useTransmogHorseArmor(List<?> armorSlots, int slot) {

        Object original = armorSlots.get(slot);

        if (slot == 2 && original instanceof ItemStack armorStack) {
            return TransmogHelper.getVisualStack(armorStack);
        }

        return original;
    }
}