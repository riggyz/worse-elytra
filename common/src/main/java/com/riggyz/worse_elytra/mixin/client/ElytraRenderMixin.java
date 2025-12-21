package com.riggyz.worse_elytra.mixin.client;

import com.riggyz.worse_elytra.Constants;
import com.riggyz.worse_elytra.client.ElytraMaskManager;
import com.riggyz.worse_elytra.elytra.StateHandler;
import com.riggyz.worse_elytra.elytra.StateHandler.ElytraState;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Mixin that handles rendering the damaged elytra. This could be expanded later
 * on but for now only has a broken stage.
 */
@Mixin(ElytraLayer.class)
public abstract class ElytraRenderMixin<T extends LivingEntity, M extends EntityModel<T>>
        extends RenderLayer<T, M> {

    private static final ResourceLocation CUSTOM_ELYTRA_MASK = new ResourceLocation(Constants.MOD_ID,
            "textures/entity/elytra_mask.png");

    /**
     * Mandatory function so that we can overwrite the elytra rendering code with
     * this mixin.
     * 
     * @param parent no idea what this is for
     */
    public ElytraRenderMixin(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    /**
     * Injected method that takes care of adding our masking texture overlay. It is
     * done so that when the elytra is broken we get a texture that looks like the
     * elytra is damaged.
     * 
     * @param originalTexture
     * @param poseStack
     * @param buffer
     * @param packedLight
     * @param entity
     * @param limbSwing
     * @param limbSwingAmount
     * @param partialTick
     * @param ageInTicks
     * @param netHeadYaw
     * @param headPitch
     */
    @Redirect(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;armorCutoutNoCull(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"))
    private RenderType worse_elytra$modifyElytraRenderType(ResourceLocation originalTexture,
            com.mojang.blaze3d.vertex.PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            LivingEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch) {

        ItemStack chestStack = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (chestStack.getItem() instanceof ElytraItem) {
            ElytraState state = StateHandler.getStateFromStack(chestStack);

            // Apply mask when broken
            if (state == ElytraState.BROKEN) {
                ResourceLocation maskedTexture = ElytraMaskManager.getMaskedTexture(originalTexture,
                        CUSTOM_ELYTRA_MASK);
                return RenderType.armorCutoutNoCull(maskedTexture);
            }
        }

        return RenderType.armorCutoutNoCull(originalTexture);
    }
}