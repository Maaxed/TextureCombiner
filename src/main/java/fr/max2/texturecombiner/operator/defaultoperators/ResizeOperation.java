package fr.max2.texturecombiner.operator.defaultoperators;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import fr.max2.texturecombiner.operator.IInterpolation;
import fr.max2.texturecombiner.operator.ITextureOperator;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.JSONUtils;

public class ResizeOperation implements ITextureOperator
{
	private final int newWidth, newHeight;
	private final IInterpolation interpolation;
	
	public ResizeOperation(int newWidth, int newHeight, IInterpolation interpolation)
	{
		this.newWidth = newWidth;
		this.newHeight = newHeight;
		this.interpolation = interpolation;
	}

	@Override
	public NativeImage apply(NativeImage[] inputs)
	{
		NativeImage in = inputs[0];
		NativeImage out = new NativeImage(newWidth, newHeight, false);
		
		for (int u = 0; u < newWidth; u++)
		{
			for (int v = 0; v < newHeight; v++)
			{
				out.setPixelRGBA(u, v, interpolation.getPixel(in, u, v, newWidth, newHeight));
			}
		}
		
		return out;
	}

	public static enum DefaultInterpolation implements IInterpolation
	{
		NEAREST_PIXEL
		{
			@Override
			public int getPixel(NativeImage img, int u, int v, int w, int h)
			{
				return img.getPixelRGBA(u * img.getWidth() / w, v * img.getHeight() / h);
			}
		},
		REPEAT
		{
			@Override
			public int getPixel(NativeImage img, int u, int v, int w, int h)
			{
				return img.getPixelRGBA(u % img.getWidth(), v % img.getHeight());
			}
		}
	}
	
	public static IInterpolation getInterpolation(String name)
	{
		switch (name.toLowerCase())
		{
		case "nearest_pixel":
		case "nearest pixel":
			return DefaultInterpolation.NEAREST_PIXEL;
		case "repeat":
			return DefaultInterpolation.REPEAT;
		default:
			throw new IllegalArgumentException("Invalid interpolation type: " + name);
		}
	}
	
	public static ITextureOperator build(int inputCount, @Nullable JsonElement options)
	{
		if (inputCount != 1)
		{
			throw new JsonSyntaxException("Invalid input count: expected 1 but was " + inputCount);
		}
		if (options == null || (options.isJsonObject() && options.getAsJsonObject().entrySet().isEmpty()))
		{
			throw new JsonSyntaxException("No option provided to resize operator");
		}
		
		if (options.isJsonObject() && options.getAsJsonObject().size() <= 3)
		{
			JsonObject obj = options.getAsJsonObject();
			
			IInterpolation interp = getInterpolation(JSONUtils.getString(obj, "interpolation"));
			if (obj.size() <= 2)
			{
				int size = JSONUtils.getInt(obj, "size");
				return new ResizeOperation(size, size, interp);
			}
			else
			{
				int width = JSONUtils.getInt(obj, "width");
				int height = JSONUtils.getInt(obj, "height");
				return new ResizeOperation(width, height, interp);
			}
		}
		
		throw new JsonSyntaxException("Invalid option value: " + JSONUtils.toString(options));
	}
}
