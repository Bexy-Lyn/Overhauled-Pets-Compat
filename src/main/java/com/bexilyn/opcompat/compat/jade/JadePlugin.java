package com.bexilyn.opcompat.compat.jade;

import com.bexilyn.opcompat.OPCompat;
import com.bexilyn.opcompat.block.PetBedBlock;
import com.bexilyn.opcompat.block.entity.HorseBedBlockEntity;
import com.bexilyn.opcompat.block.entity.PetBedBlockEntity;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    public static final ResourceLocation BED_OCCUPANT =
            new ResourceLocation(
                    OPCompat.MOD_ID,
                    "bed_occupant"
            );

    @Override
    public void register(
            IWailaCommonRegistration registration
    ) {

        registration.registerBlockDataProvider(
                BedOccupantProvider.INSTANCE,
                HorseBedBlockEntity.class
        );
    }

    @Override
    public void registerClient(
            IWailaClientRegistration registration
    ) {

        registration.registerBlockComponent(
                BedOccupantProvider.INSTANCE,
                PetBedBlock.class
        );
    }
}