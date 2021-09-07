package aputils.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BoatEntity.class)
public class BoatExploderMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    public void explodeBoat(CallbackInfo ci) {
        BoatEntity boat = (BoatEntity)(Object)this;
        Identifier dimID = boat.world.getRegistryKey().getValue();
        if (new Random().nextBoolean() &&
            (
                dimID.toString().equals("mmorpg:dungeon") ||
                dimID.toString().equals("mmorpg:rift")
            )
        ) {
            boat.damage(DamageSource.GENERIC, 1.0f);
            boat.world.addParticle(
                ParticleTypes.EXPLOSION,
                boat.getX() + getRandOffset(),
                boat.getY() + getRandOffset() + 1.5,
                boat.getZ() + getRandOffset(),
                0.0,
                0.0,
                0.0
            );
            PlayerEntity player = boat.world.getClosestPlayer(boat, 8.0);
            if (player != null) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 170, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 170, 1));
            }
        }
    }
    
    private double getRandOffset() {
        return new Random().nextDouble() * 4 - 2;
    }
}
