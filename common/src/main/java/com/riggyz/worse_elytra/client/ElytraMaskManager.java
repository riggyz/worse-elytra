package com.riggyz.worse_elytra.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

/**
 * Manages dynamic textures that combine a base elytra/cape texture with a mask.
 * The mask's alpha is used to cut out portions of the base texture.
 */
public class ElytraMaskManager {

    private static final Map<ResourceLocation, ResourceLocation> cache = new HashMap<>();

    /**
     * Gets or creates a masked texture by applying an alpha mask to a base texture.
     * 
     * The masking operation multiplies the base texture's alpha channel by the
     * mask's alpha channel, effectively cutting out portions of the base texture
     * where the mask is transparent.
     * 
     * Results are cached, so subsequent calls with the same base texture will
     * return the same masked texture without reprocessing.
     * 
     * 
     * @param baseTexture The original elytra texture to be masked
     * @param maskTexture The mask texture whose alpha channel determines visibility
     * 
     * @return A ResourceLocation pointing to the masked texture, or the original
     *         texture if masking fails
     */
    public static ResourceLocation getMaskedTexture(ResourceLocation baseTexture, ResourceLocation maskTexture) {
        if (cache.containsKey(baseTexture)) {
            return cache.get(baseTexture);
        }

        try {
            NativeImage base = loadTexture(baseTexture);
            NativeImage mask = loadTexture(maskTexture);

            if (base == null || mask == null) {
                return baseTexture;
            }

            applyMask(base, mask);
            mask.close();

            DynamicTexture texture = new DynamicTexture(base);
            ResourceLocation result = Minecraft.getInstance().getTextureManager()
                    .register("worse_elytra/masked_" + cache.size(), texture);

            cache.put(baseTexture, result);
            return result;

        } catch (Exception e) {
            return baseTexture;
        }
    }

    /**
     * Loads a texture from the resource manager.
     * 
     * @param location The resource location of the texture to load
     * 
     * @return A NativeImage containing the texture data, or null if the texture
     *         could not be loaded
     */
    private static NativeImage loadTexture(ResourceLocation location) {
        Minecraft mc = Minecraft.getInstance();

        try {
            Resource resource = mc.getResourceManager()
                    .getResource(location).orElse(null);
            if (resource != null) {
                try (InputStream stream = resource.open()) {
                    return NativeImage.read(stream);
                }
            }
        } catch (Exception e) {
            // Ignore
        }

        // Try loading from texture manager (for dynamic textures like capes)
        try {
            AbstractTexture texture = mc.getTextureManager().getTexture(location);
            if (texture != null) {
                int textureId = texture.getId();
                if (textureId != -1) {
                    return downloadTextureFromGL(textureId);
                }
            }
        } catch (Exception e) {
            // Failed
        }

        return null;
    }

    /**
     * Downloads texture data from an OpenGL texture ID.
     * 
     * @param textureId The OpenGL texture ID to download from
     * 
     * @return A NativeImage containing the texture data, or null if the download
     *         fails
     */
    private static NativeImage downloadTextureFromGL(int textureId) {
        // Must be on render thread
        if (!RenderSystem.isOnRenderThread()) {
            return null;
        }

        // Bind the texture
        RenderSystem.bindTexture(textureId);

        // Get texture dimensions
        int width = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int height = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        if (width <= 0 || height <= 0) {
            return null;
        }

        // Create NativeImage and download texture data from GPU
        NativeImage image = new NativeImage(width, height, false);
        image.downloadTexture(0, false);

        return image;
    }

    /**
     * Applies an alpha mask to a base image by combining their alpha channels.
     * 
     * @param base The base image to modify
     * @param mask The mask image whose alpha determines the cutout
     */
    private static void applyMask(NativeImage base, NativeImage mask) {
        int width = Math.min(base.getWidth(), mask.getWidth());
        int height = Math.min(base.getHeight(), mask.getHeight());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int basePixel = base.getPixelRGBA(x, y);
                int maskPixel = mask.getPixelRGBA(x, y);

                // Apply mask alpha to base
                int baseA = (basePixel >> 24) & 0xFF;
                int maskA = (maskPixel >> 24) & 0xFF;
                int newA = Math.min(baseA, maskA);

                // Update alpha only
                int newPixel = (basePixel & 0x00FFFFFF) | (newA << 24);
                base.setPixelRGBA(x, y, newPixel);
            }
        }
    }
}
