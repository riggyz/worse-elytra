package com.riggyz.modbox.mixin;

import com.riggyz.modbox.elytra.ElytraStateHandler;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class ElytraActivationMixin {

    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    private void modbox$preventActivationOnCooldown(CallbackInfoReturnable<Boolean> cir) {
        Player self = (Player) (Object) this;
        ItemStack elytra = self.getItemBySlot(EquipmentSlot.CHEST);

        // Only check our CustomElytraItem
        if (!ElytraStateHandler.isCustomElytra(elytra)) {
            return;
        }

        // Prevent activation if on cooldown
        if (ElytraStateHandler.isOnCooldown(self, elytra)) {
            // Play a "denied" sound so player knows why it didn't work
            self.level().playSound(
                    null,
                    self.getX(), self.getY(), self.getZ(),
                    SoundEvents.ITEM_BREAK,
                    SoundSource.PLAYERS,
                    0.5f,
                    1.5f // Higher pitch = sounds like a "nope"
            );

            cir.setReturnValue(false);
        }
    }
}
