package fr.max2.texturecombiner.operator;

import java.io.IOException;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;

@FunctionalInterface
public interface ITextureBuilder
{
	NativeImage buildTexture(ITextureFinder finder) throws IOException;
	
	@FunctionalInterface
	public static interface ITextureFinder
	{
		/**
		 * Finds an image from its resource location
		 * @param loc the location of the desired image
		 * @return the corresponding image
		 * @throws IOException
		 */
		NativeImage find(ResourceLocation loc) throws IOException;
	}
}
