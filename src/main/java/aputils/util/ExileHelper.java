package aputils.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.NbtPredicate;

public final class ExileHelper {

    public static int getPlayerLevel(PlayerEntity player) {
        CompoundTag playerNbt = NbtPredicate.entityToTag(player);
        if(playerNbt.contains("cardinal_components")) {
            CompoundTag ccaData = playerNbt.getCompound("cardinal_components");
            if(ccaData.contains("mmorpg:unit_data")) {
                CompoundTag unitData = ccaData.getCompound("mmorpg:unit_data");
                if(unitData.contains("level")) {
                    return unitData.getInt("level");
                }
            }
        }
        return 0;
    }
}
