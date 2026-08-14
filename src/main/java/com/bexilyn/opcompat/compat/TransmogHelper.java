package com.bexilyn.opcompat.compat;

import com.hidoni.transmog.TransmogUtils;
import net.minecraft.world.item.ItemStack;

public final class TransmogHelper {

    private TransmogHelper() {
    }

    /**
     * Returns the ItemStack that should be used purely for rendering.
     * The original stack is never modified.
     * If the item has no transmog:
     *     returns the original ItemStack.
     * If the item is transmogged:
     *     returns its appearance ItemStack.
     * If the appearance is Transmog's hidden/Void Fragment appearance:
     *     returns ItemStack.EMPTY.
     */
    public static ItemStack getVisualStack(ItemStack originalStack) {

        if (originalStack == null || originalStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (!TransmogUtils.isItemStackTransmogged(originalStack)) {
            return originalStack;
        }

        return TransmogUtils.getAppearanceItemStack(originalStack, false);
    }
}