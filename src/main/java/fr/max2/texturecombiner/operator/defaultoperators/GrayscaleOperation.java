package fr.max2.texturecombiner.operator.defaultoperators;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import fr.max2.texturecombiner.operator.ITextureOperator;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.JSONUtils;

public enum GrayscaleOperation implements ITextureOperator
{
	NORMAL
	{
		@Override
		public NativeImage apply(NativeImage[] inputs)
		{
			return ITextureOperator.applyPerPixel(inputs, pixels -> 
			{
				int a = pixels[0] & 0xff000000;
				int b = (pixels[0] >> 16) & 0xff;
				int g = (pixels[0] >> 8) & 0xff;
				int r = pixels[0] & 0xff;
				
				int gray = (b + g + r) / 3;
				
				return a | (gray << 16) | (gray << 8) | gray;
			});
		}
	};
	
	public static ITextureOperator build(int inputCount, @Nullable JsonElement options)
	{
		if (inputCount != 1)
		{
			throw new JsonSyntaxException("Invalid input count: expected 1 but was " + inputCount);
		}
		if (options != null && (!options.isJsonObject() || !options.getAsJsonObject().entrySet().isEmpty()))
		{
			throw new JsonSyntaxException("Invalid option value: " + JSONUtils.toString(options));
		}
		return NORMAL;
	}
}
