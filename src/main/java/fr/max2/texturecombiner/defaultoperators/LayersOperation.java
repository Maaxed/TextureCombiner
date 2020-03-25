package fr.max2.texturecombiner.defaultoperators;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import fr.max2.texturecombiner.ITextureOperator;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.JSONUtils;

public enum LayersOperation implements ITextureOperator
{
	OVERWRITE
	{
		@Override
		public NativeImage apply(NativeImage[] inputs)
		{
			return ITextureOperator.applyPerPixel(inputs, pixels -> 
			{
				for (int i = pixels.length - 1; i >= 0; i++)
				{
					int alpha = pixels[i] & 0xff000000;
					if (alpha > 0)
						return pixels[i];
				}
				
				return 0;
			});
		}
	},
	NORMAL
	{
		@Override
		public NativeImage apply(NativeImage[] inputs)
		{
			return ITextureOperator.applyPerPixel(inputs, pixels -> 
			{
				int resColor = pixels[0];
				for (int i = 0; i < pixels.length; i++)
				{
					int pixA = (pixels[i] >> 24) & 255;
					if (pixA == 0)
						continue;

					int pixR = (pixels[i] >> 16) & 255;
					int pixG = (pixels[i] >> 8) & 255;
					int pixB = pixels[i] & 255;
					
					
					int oldA = (resColor >> 24) & 255;
					int oldR = (resColor >> 16) & 255;
					int oldG = (resColor >> 8) & 255;
					int oldB = resColor & 255;
					
					int newAlpha = pixA * 255 + oldA * (255 - pixA);
					int newR = (pixR * pixA * 255 + oldR * oldA * (255 - pixA)) / newAlpha;
					int newG = (pixG * pixA * 255 + oldG * oldA * (255 - pixA)) / newAlpha;
					int newB = (pixB * pixA * 255 + oldB * oldA * (255 - pixA)) / newAlpha;
					
					resColor = ((newAlpha / 255) << 24) | (newR << 16) | (newG << 8) | newB;
				}
				
				return resColor;
			});
		}
	};
	
	/**
	 * Finds a layers operator from the given options
	 * @param inputCount the number of given inputs
	 * @param options the options given in the json file
	 * @return the appropriate LayersOperation
	 */
	public static ITextureOperator build(int inputCount, @Nullable JsonElement options)
	{
		if (options != null)
		{
			if (options.isJsonPrimitive())
			{
				if (options.getAsString().equals("overwrite"))
				{
					return OVERWRITE;
				}
			}
			else if (options.isJsonObject())
			{
				JsonObject obj = options.getAsJsonObject();
				if (obj.has("overwrite"))
				{
					if (JSONUtils.getBoolean(obj, "overwrite"))
					{
						return OVERWRITE;
					}
				}
				if (obj.has("mode"))
				{
					switch (JSONUtils.getString(obj, "mode"))
					{
					case "overwrite":
						return OVERWRITE;
					}
				}
			}
			else
			{
				throw new JsonSyntaxException("The invalid option value: " + JSONUtils.toString(options));
			}
		}
		return NORMAL;
	}
}
