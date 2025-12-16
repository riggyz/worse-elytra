package com.riggyz.modbox.mixin;

import com.riggyz.modbox.elytra.FlightDistanceTracker;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerFlightTickMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void modbox$onPlayerTick(CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (!self.level().isClientSide()) {
            FlightDistanceTracker.tick(self);
        }
    }
}