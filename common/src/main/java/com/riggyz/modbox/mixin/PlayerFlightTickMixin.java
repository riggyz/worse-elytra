package com.riggyz.modbox.mixin;

import com.riggyz.modbox.elytra.FlightDistanceTracker;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerFlightTickMixin {

    /**
     * Hooks into the player tick to track flight distance.
     * Runs at TAIL so vanilla logic completes first.
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void modbox$onPlayerTick(CallbackInfo ci) {
        Player self = (Player) (Object) this;

        // Only run on server side to prevent desync
        if (!self.level().isClientSide()) {
            FlightDistanceTracker.tick(self);
        }
    }
}