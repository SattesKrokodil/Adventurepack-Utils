package aputils.util;

import net.minecraft.advancement.Advancement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ServerAdvancementChecker implements AdvancementChecker {

    @Override
    public Optional<Advancement> getAdvancement(PlayerEntity player, Identifier identifier) {
        return Optional.ofNullable(player.getServer().getAdvancementLoader().get(identifier));
    }

    @Override
    public boolean hasAdvancement(PlayerEntity player, Advancement advancement) {
        return ((ServerPlayerEntity)player).getAdvancementTracker().getProgress(advancement).isDone();
    }
}
