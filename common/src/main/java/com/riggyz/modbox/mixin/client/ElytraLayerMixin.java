package com.riggyz.modbox.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.riggyz.modbox.Constants;
import com.riggyz.modbox.elytra.ElytraStateHandler;
import com.riggyz.modbox.elytra.ElytraStateHandler.ElytraState;
import com.riggyz.modbox.item.CustomElytraItem;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
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
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ElytraLayer.class)
public abstract class ElytraLayerMixin<T extends LivingEntity, M extends EntityModel<T>>
        extends RenderLayer<T, M> {

    private static final ResourceLocation CUSTOM_ELYTRA_TEXTURE = new ResourceLocation("minecraft",
            "textures/entity/elytra.png");

    @Shadow
    @Final
    private ElytraModel<T> elytraModel;

    // Vanilla elytra texture for reference
    @Unique
    private static final ResourceLocation VANILLA_ELYTRA = new ResourceLocation("textures/entity/elytra.png");

    // Required constructor for extending RenderLayer
    public ElytraLayerMixin(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    private void modbox$renderCustomElytra(
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

        if (!(chestStack.getItem() instanceof CustomElytraItem)) {
            return; // Let vanilla handle it
        }

        // Cancel vanilla rendering, we'll do it ourselves
        ci.cancel();

        // Get the texture for current state
        ElytraState state = ElytraStateHandler.getStateFromStack(chestStack);
        ResourceLocation texture = modbox$getTextureForState(state);

        // Setup pose
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 0.125D);

        // Copy rotations from parent model
        this.getParentModel().copyPropertiesTo(this.elytraModel);

        // Setup elytra model
        this.elytraModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        // Render
        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(
                buffer,
                RenderType.armorCutoutNoCull(texture),
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

    @Unique
    private ResourceLocation modbox$getTextureForState(ElytraState state) {
        return switch (state) {
            case NORMAL -> CUSTOM_ELYTRA_TEXTURE;
            case RUFFLED -> CUSTOM_ELYTRA_TEXTURE;
            case WITHERED -> CUSTOM_ELYTRA_TEXTURE;
            case BROKEN -> CUSTOM_ELYTRA_TEXTURE;
        };
    }
}