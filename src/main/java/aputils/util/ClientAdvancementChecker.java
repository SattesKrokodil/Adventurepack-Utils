package aputils.util;

import aputils.mixin.ClientAdvancementManagerAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ClientAdvancementChecker implements AdvancementChecker {

    @Override
    public Optional<Advancement> getAdvancement(PlayerEntity player, Identifier identifier) {
        if(player instanceof ServerPlayerEntity) {
            return Optional.ofNullable(player.getServer().getAdvancementLoader().get(identifier));
        } else
        if(player instanceof ClientPlayerEntity) {
            ClientAdvancementManager advancementManager = MinecraftClient.getInstance().getNetworkHandler().getAdvancementHandler();
            return Optional.ofNullable(advancementManager.getManager().get(identifier));
        }
        return Optional.empty();
    }

    @Override
    public boolean hasAdvancement(PlayerEntity player, Advancement advancement) {
        if(player instanceof ServerPlayerEntity) {
            return ((ServerPlayerEntity)player).getAdvancementTracker().getProgress(advancement).isDone();
        } else
        if(player instanceof ClientPlayerEntity) {
            ClientAdvancementManager advancementManager = MinecraftClient.getInstance().getNetworkHandler().getAdvancementHandler();
            if(advancement != null) {
                Map<Advancement, AdvancementProgress> progressMap = ((ClientAdvancementManagerAccessor)advancementManager).getAdvancementProgresses();
                if(progressMap.containsKey(advancement)) {
                    return progressMap.get(advancement).isDone();
                }
            }
        }
        return false;
    }
}
