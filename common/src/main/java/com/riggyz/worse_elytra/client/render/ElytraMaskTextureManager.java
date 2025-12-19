package com.riggyz.worse_elytra.client.render;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

/**
 * Manages dynamic textures that combine a base elytra/cape texture with a mask.
 * The mask's alpha is used to cut out portions of the base texture.
 */
public class ElytraMaskTextureManager {
    
    private static final Map<String, ResourceLocation> cachedTextures = new HashMap<>();
    private static final Map<String, DynamicTexture> dynamicTextures = new HashMap<>();
    
    /**
     * Gets or creates a masked texture by combining the base texture with the mask.
     * Where the mask has low alpha, the result will also have low alpha (transparent).
     * 
     * @param baseTexture The base elytra or cape texture
     * @param maskTexture The mask texture (alpha determines visibility)
     * @return A ResourceLocation for the combined texture
     */
    public static ResourceLocation getMaskedTexture(ResourceLocation baseTexture, ResourceLocation maskTexture) {
        String cacheKey = baseTexture.toString() + ":" + maskTexture.toString();
        
        // Return cached texture if available
        if (cachedTextures.containsKey(cacheKey)) {
            return cachedTextures.get(cacheKey);
        }
        
        try {
            // Load both textures
            NativeImage baseImage = loadTexture(baseTexture);
            NativeImage maskImage = loadTexture(maskTexture);
            
            if (baseImage == null || maskImage == null) {
                // Fallback to base texture if we can't load
                if (baseImage != null) baseImage.close();
                if (maskImage != null) maskImage.close();
                return baseTexture;
            }
            
            // Create the combined image
            NativeImage combinedImage = combineWithMask(baseImage, maskImage);
            
            // Clean up source images
            baseImage.close();
            maskImage.close();
            
            // Create dynamic texture and register it
            DynamicTexture dynamicTexture = new DynamicTexture(combinedImage);
            ResourceLocation textureLocation = Minecraft.getInstance().getTextureManager()
                .register("worse_elytra_masked_elytra_" + cachedTextures.size(), dynamicTexture);
            
            // Cache for future use
            cachedTextures.put(cacheKey, textureLocation);
            dynamicTextures.put(cacheKey, dynamicTexture);
            
            return textureLocation;
            
        } catch (Exception e) {
            // Log and fallback to base texture
            System.err.println("Failed to create masked elytra texture: " + e.getMessage());
            e.printStackTrace();
            return baseTexture;
        }
    }
    
    /**
     * Loads a texture as a NativeImage.
     * Handles both resource pack textures and dynamic textures (like player capes).
     */
    private static NativeImage loadTexture(ResourceLocation location) {
        Minecraft mc = Minecraft.getInstance();
        
        // First try loading from resource manager (for resource pack textures)
        try {
            Resource resource = mc.getResourceManager().getResource(location).orElse(null);
            if (resource != null) {
                try (InputStream stream = resource.open()) {
                    return NativeImage.read(stream);
                }
            }
        } catch (IOException e) {
            // Fall through to try loading from texture manager
        }
        
        // Try loading from texture manager (for dynamic textures like player capes)
        try {
            AbstractTexture texture = mc.getTextureManager().getTexture(location);
            if (texture != null) {
                // Bind the texture and read pixels from OpenGL
                int textureId = texture.getId();
                if (textureId != -1) {
                    return downloadTexture(textureId);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load texture from texture manager: " + location + " - " + e.getMessage());
        }
        
        System.err.println("Failed to load texture: " + location);
        return null;
    }
    
    /**
     * Downloads texture data from an OpenGL texture ID.
     */
    private static NativeImage downloadTexture(int textureId) {
        // Bind the texture
        com.mojang.blaze3d.systems.RenderSystem.bindTexture(textureId);
        
        // Get texture dimensions
        int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
        
        if (width <= 0 || height <= 0) {
            return null;
        }
        
        // Create NativeImage and download texture data
        NativeImage image = new NativeImage(width, height, false);
        image.downloadTexture(0, false);
        
        return image;
    }

    /**
     * Combines the base image with the mask.
     * The output pixel's alpha = min(baseAlpha, maskAlpha)
     * This effectively "cuts out" areas where the mask is transparent.
     */
    private static NativeImage combineWithMask(NativeImage base, NativeImage mask) {
        int width = base.getWidth();
        int height = base.getHeight();
        
        // Scale mask to match base if needed
        int maskWidth = mask.getWidth();
        int maskHeight = mask.getHeight();
        
        NativeImage result = new NativeImage(width, height, false);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int basePixel = base.getPixelRGBA(x, y);
                
                // Sample mask (scale coordinates if sizes differ)
                int maskX = (x * maskWidth) / width;
                int maskY = (y * maskHeight) / height;
                int maskPixel = mask.getPixelRGBA(maskX, maskY);
                
                // Extract components (RGBA format: ABGR in memory)
                int baseR = (basePixel >> 0) & 0xFF;
                int baseG = (basePixel >> 8) & 0xFF;
                int baseB = (basePixel >> 16) & 0xFF;
                int baseA = (basePixel >> 24) & 0xFF;
                
                int maskA = (maskPixel >> 24) & 0xFF;
                
                // Apply mask: result alpha = min(base alpha, mask alpha)
                int resultA = Math.min(baseA, maskA);
                
                // Reconstruct pixel
                int resultPixel = (resultA << 24) | (baseB << 16) | (baseG << 8) | baseR;
                result.setPixelRGBA(x, y, resultPixel);
            }
        }
        
        return result;
    }
    
    /**
     * Clears the texture cache. Call this when resources are reloaded.
     */
    public static void clearCache() {
        for (DynamicTexture texture : dynamicTextures.values()) {
            texture.close();
        }
        dynamicTextures.clear();
        cachedTextures.clear();
    }
}
