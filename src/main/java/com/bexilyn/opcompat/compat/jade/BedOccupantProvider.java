package com.bexilyn.opcompat.compat.jade;

import com.bexilyn.opcompat.block.entity.PetBedBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum BedOccupantProvider
        implements
        IBlockComponentProvider,
        IServerDataProvider<BlockAccessor> {

    INSTANCE;

    private static final String TAG_OCCUPIED =
            "Occupied";

    private static final String TAG_PET_NAME =
            "PetName";

    /*
     * =============================================================
     * SERVER
     * =============================================================
     *
     * Jade calls this on the server.
     *
     * We send only the information that the client actually needs.
     */
    @Override
    public void appendServerData(
            CompoundTag data,
            BlockAccessor accessor
    ) {

        if (!(accessor.getBlockEntity()
                instanceof PetBedBlockEntity<?> petBed)) {
            return;
        }

        if (!petBed.hasLinkedPet()) {

            data.putBoolean(
                    TAG_OCCUPIED,
                    false
            );

            return;
        }

        data.putBoolean(
                TAG_OCCUPIED,
                true
        );

        String petName =
                petBed.getLinkedPetName();

        if (
                petName != null
                        && !petName.isBlank()
        ) {

            data.putString(
                    TAG_PET_NAME,
                    petName
            );
        }
    }

    /*
     * =============================================================
     * CLIENT TOOLTIP
     * =============================================================
     */
    @Override
    public void appendTooltip(
            ITooltip tooltip,
            BlockAccessor accessor,
            IPluginConfig config
    ) {

        CompoundTag serverData =
                accessor.getServerData();

        if (!serverData.getBoolean(
                TAG_OCCUPIED
        )) {

            return;
        }

        if (!serverData.contains(
                TAG_PET_NAME
        )) {

            return;
        }

        String petName =
                serverData.getString(
                        TAG_PET_NAME
                );

        tooltip.add(
                Component.translatable(
                        "tooltip.opcompat.occupied",
                        petName
                )
        );

        //TODO: Name not shown
    }

    /*
     * Jade uses this as the unique ID for this tooltip provider.
     *
     * It also generates Jade's own enable/disable config option
     * for the provider from this ID.
     */
    @Override
    public ResourceLocation getUid() {

        return JadePlugin
                .BED_OCCUPANT;
    }
}