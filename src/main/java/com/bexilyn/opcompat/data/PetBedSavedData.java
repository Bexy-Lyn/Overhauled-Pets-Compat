package com.bexilyn.opcompat.data;

import com.bexilyn.opcompat.OPCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PetBedSavedData extends SavedData {

    private static final String DATA_NAME =
            OPCompat.MOD_ID + "_pet_beds";

    private final Map<UUID, PetBedLink> links = new HashMap<>();

    public static PetBedSavedData get(ServerLevel level) {

        ServerLevel overworld =
                level.getServer().getLevel(Level.OVERWORLD);

        if (overworld == null) {
            throw new IllegalStateException("Overworld is not available");
        }

        return overworld.getDataStorage().computeIfAbsent(
                PetBedSavedData::load,
                PetBedSavedData::new,
                DATA_NAME
        );
    }

    public boolean isLinked(UUID petUuid) {
        return links.containsKey(petUuid);
    }

    public PetBedLink getLink(UUID petUuid) {
        return links.get(petUuid);
    }

    public void link(
            UUID petUuid,
            ResourceKey<Level> dimension,
            BlockPos bedPos
    ) {

        links.put(
                petUuid,
                new PetBedLink(
                        dimension,
                        bedPos.immutable()
                )
        );

        setDirty();
    }

    public void unlink(UUID petUuid) {

        if (links.remove(petUuid) != null) {
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {

        ListTag list = new ListTag();

        for (Map.Entry<UUID, PetBedLink> entry : links.entrySet()) {

            CompoundTag linkTag = new CompoundTag();

            linkTag.putUUID(
                    "PetUUID",
                    entry.getKey()
            );

            linkTag.putString(
                    "Dimension",
                    entry.getValue()
                            .dimension()
                            .location()
                            .toString()
            );

            linkTag.putLong(
                    "BedPos",
                    entry.getValue()
                            .bedPos()
                            .asLong()
            );

            list.add(linkTag);
        }

        tag.put(
                "Links",
                list
        );

        return tag;
    }

    public static PetBedSavedData load(CompoundTag tag) {

        PetBedSavedData data =
                new PetBedSavedData();

        ListTag list =
                tag.getList(
                        "Links",
                        Tag.TAG_COMPOUND
                );

        for (int i = 0; i < list.size(); i++) {

            CompoundTag linkTag =
                    list.getCompound(i);

            if (!linkTag.hasUUID("PetUUID")) {
                continue;
            }

            UUID petUuid =
                    linkTag.getUUID("PetUUID");

            ResourceLocation dimensionId =
                    new ResourceLocation(
                            linkTag.getString("Dimension")
                    );

            ResourceKey<Level> dimension =
                    ResourceKey.create(
                            Registries.DIMENSION,
                            dimensionId
                    );

            BlockPos bedPos =
                    BlockPos.of(
                            linkTag.getLong("BedPos")
                    );

            data.links.put(
                    petUuid,
                    new PetBedLink(
                            dimension,
                            bedPos
                    )
            );
        }

        return data;
    }

    public record PetBedLink(
            ResourceKey<Level> dimension,
            BlockPos bedPos
    ) {
    }
}