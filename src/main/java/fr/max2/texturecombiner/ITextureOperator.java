package fr.max2.texturecombiner;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;

import net.minecraft.client.renderer.texture.NativeImage;

@FunctionalInterface
public interface ITextureOperator
{
	NativeImage apply(NativeImage[] inputs);
	
	/**
	 * Applies the given function to every pixels of the input images
	 * @param inputs the images to process
	 * @param fun the operator to apply the the pixels
	 * @return the generated image
	 */
	public static NativeImage applyPerPixel(NativeImage[] inputs, PixelOperator fun)
	{
		// Compute the right size of the new image
		int width = 0;
		int height = 0;
		for (NativeImage img : inputs)
		{
			width = Math.max(width, img.getWidth());
			height = Math.max(height, img.getHeight());
		}
		
		NativeImage res = new NativeImage(width, height, false);
		
		// For each pixel
		for (int u = 0; u < width; u++)
		{
			for (int v = 0; v < height; v++)
			{
				int[] pixels = new int[inputs.length];
				for (int i = 0; i < inputs.length; i++)
				{
					pixels[i] = inputs[i].getPixelRGBA(u * inputs[i].getWidth() / width, v * inputs[i].getHeight() / height);
				}
				// Apply the operator and put the result in the new image
				res.setPixelRGBA(u, v, fun.apply(pixels));
			}
		}
		
		return res;
	}

	@FunctionalInterface
	public static interface PixelOperator
	{
		int apply(int[] inputs);
	}
	
	@FunctionalInterface
	public static interface Builder
	{
		/**
		 * Builds a texture operator from its parameters
		 * @param inputCount the number of given inputs
		 * @param options the other options given in the json file
		 * @return the generated operator
		 */
		ITextureOperator build(int inputCount, @Nullable JsonElement options);
	}
}
