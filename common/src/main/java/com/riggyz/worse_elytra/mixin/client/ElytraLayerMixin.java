package com.riggyz.worse_elytra.mixin.client;

import com.riggyz.worse_elytra.Constants;
import com.riggyz.worse_elytra.client.render.ElytraMaskTextureManager;
import com.riggyz.worse_elytra.elytra.ElytraStateHandler;
import com.riggyz.worse_elytra.elytra.ElytraStateHandler.ElytraState;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ElytraLayer.class)
public abstract class ElytraLayerMixin<T extends LivingEntity, M extends EntityModel<T>>
        extends RenderLayer<T, M> {

    private static final ResourceLocation VANILLA_ELYTRA_TEXTURE = new ResourceLocation("minecraft",
            "textures/entity/elytra.png");

    private static final ResourceLocation CUSTOM_ELYTRA_MASK = new ResourceLocation(Constants.MOD_ID,
            "textures/entity/elytra_mask.png");

    @Shadow
    @Final
    private ElytraModel<T> elytraModel;

    public ElytraLayerMixin(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    // TODO: do we need this to render the elytra?
    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    private void worse_elytra$renderCustomElytra(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            T entity,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch,
            CallbackInfo ci) {
        ItemStack chestStack = entity.getItemBySlot(EquipmentSlot.CHEST);

        if (!(chestStack.getItem() instanceof ElytraItem)) {
            return;
        }

        // Cancel vanilla rendering, we'll do it ourselves
        ci.cancel();

        ResourceLocation baseTexture = VANILLA_ELYTRA_TEXTURE;
        if (entity instanceof AbstractClientPlayer player) {
            if (player.isElytraLoaded() && player.getElytraTextureLocation() != null) {
                // Special elytra texture from Mojang profile
                baseTexture = player.getElytraTextureLocation();
            } else if (player.isCapeLoaded()
                    && player.getCloakTextureLocation() != null
                    && player.isModelPartShown(PlayerModelPart.CAPE)) {
                baseTexture = player.getCloakTextureLocation();
            }
        }

        // Only apply the mask when the elytra is BROKEN
        ElytraState state = ElytraStateHandler.getStateFromStack(chestStack);
        ResourceLocation finalTexture;
        if (state == ElytraState.BROKEN) {
            finalTexture = ElytraMaskTextureManager.getMaskedTexture(baseTexture, CUSTOM_ELYTRA_MASK);
        } else {
            finalTexture = baseTexture;
        }

        // Setup pose
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 0.125D);

        // Copy rotations from parent model
        this.getParentModel().copyPropertiesTo(this.elytraModel);

        // Setup elytra model
        this.elytraModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        // Render the combined/masked texture using vanilla-style rendering
        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(
                buffer,
                RenderType.armorCutoutNoCull(finalTexture),
                false,
                chestStack.hasFoil());

        this.elytraModel.renderToBuffer(
                poseStack,
                vertexConsumer,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }
}