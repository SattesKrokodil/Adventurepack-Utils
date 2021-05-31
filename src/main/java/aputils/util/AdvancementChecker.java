package aputils.util;

import net.minecraft.advancement.Advancement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Optional;

public interface AdvancementChecker {

    Optional<Advancement> getAdvancement(PlayerEntity player, Identifier identifier);
    boolean hasAdvancement(PlayerEntity player, Advancement advancement);
}
