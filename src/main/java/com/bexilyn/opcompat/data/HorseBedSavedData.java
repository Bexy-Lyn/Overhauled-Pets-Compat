package com.bexilyn.opcompat.data;

import com.bexilyn.opcompat.OPCompat;
import net.minecraft.core.BlockPos;
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

public class HorseBedSavedData extends SavedData {

    private static final String DATA_NAME =
            OPCompat.MOD_ID + "_horse_beds";

    private final Map<UUID, HorseBedLink> links = new HashMap<>();

    public static HorseBedSavedData get(ServerLevel level) {

        // Store all links in the Overworld's data storage,
        // even if we later support beds in other dimensions.
        ServerLevel overworld =
                level.getServer().getLevel(Level.OVERWORLD);

        if (overworld == null) {
            throw new IllegalStateException("Overworld is not available");
        }

        return overworld.getDataStorage().computeIfAbsent(
                HorseBedSavedData::load,
                HorseBedSavedData::new,
                DATA_NAME
        );
    }

    public boolean isLinked(UUID horseUuid) {
        return links.containsKey(horseUuid);
    }

    public HorseBedLink getLink(UUID horseUuid) {
        return links.get(horseUuid);
    }

    public void link(
            UUID horseUuid,
            ResourceKey<Level> dimension,
            BlockPos bedPos
    ) {
        links.put(
                horseUuid,
                new HorseBedLink(
                        dimension,
                        bedPos.immutable()
                )
        );

        setDirty();
    }

    public void unlink(UUID horseUuid) {

        if (links.remove(horseUuid) != null) {
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {

        ListTag list = new ListTag();

        for (Map.Entry<UUID, HorseBedLink> entry : links.entrySet()) {

            CompoundTag linkTag = new CompoundTag();

            linkTag.putUUID(
                    "HorseUUID",
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

        tag.put("Links", list);

        return tag;
    }

    public static HorseBedSavedData load(CompoundTag tag) {

        HorseBedSavedData data =
                new HorseBedSavedData();

        ListTag list =
                tag.getList(
                        "Links",
                        Tag.TAG_COMPOUND
                );

        for (int i = 0; i < list.size(); i++) {

            CompoundTag linkTag =
                    list.getCompound(i);

            if (!linkTag.hasUUID("HorseUUID")) {
                continue;
            }

            UUID horseUuid =
                    linkTag.getUUID("HorseUUID");

            ResourceLocation dimensionId =
                    new ResourceLocation(
                            linkTag.getString("Dimension")
                    );

            ResourceKey<Level> dimension =
                    ResourceKey.create(
                            net.minecraft.core.registries.Registries.DIMENSION,
                            dimensionId
                    );

            BlockPos bedPos =
                    BlockPos.of(
                            linkTag.getLong("BedPos")
                    );

            data.links.put(
                    horseUuid,
                    new HorseBedLink(
                            dimension,
                            bedPos
                    )
            );
        }

        return data;
    }

    public record HorseBedLink(
            ResourceKey<Level> dimension,
            BlockPos bedPos
    ) {
    }
}