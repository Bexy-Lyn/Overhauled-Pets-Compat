package com.bexilyn.opcompat.mixin;

import com.dragn0007.dragnpets.entities.dog.ODog;
import com.dragn0007.dragnpets.entities.dog.ODogDecorLayer;
import com.bexilyn.opcompat.compat.TransmogHelper;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ODogDecorLayer.class, remap = false)
public abstract class ODogDecorLayerMixin {

    /**
     * Redirects calls to ODog#getArmor() made specifically from
     * ODogDecorLayer#render().
     * Gameplay code still receives the real armor ItemStack.
     * Only this rendering layer receives the Transmog appearance stack.
     */
    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/dragn0007/dragnpets/entities/dog/ODog;getArmor()Lnet/minecraft/world/item/ItemStack;",
                    remap = false
            ),
            remap = false
    )
    private ItemStack optransmog$useTransmogAppearance(ODog dog) {
        return TransmogHelper.getVisualStack(dog.getArmor());
    }
}