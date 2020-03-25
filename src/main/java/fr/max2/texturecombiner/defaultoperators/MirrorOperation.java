package fr.max2.texturecombiner.defaultoperators;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import fr.max2.texturecombiner.ITextureOperator;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.JSONUtils;

public enum MirrorOperation implements ITextureOperator
{
	HORIZONTAL,
	VERTICAL;
	
	private int getMirroredU(int u, int w)
	{
		switch (this)
		{
		case HORIZONTAL:
			return w-u;
		default:
			return u;
		}
	}
	
	private int getMirroredV(int v, int h)
	{
		switch (this)
		{
		case VERTICAL:
			return h-v;
		default:
			return v;
		}
	}
	
	@Override
	public NativeImage apply(NativeImage[] inputs)
	{
		NativeImage in = inputs[0];
		int width = in.getWidth();
		int height = in.getHeight();
		NativeImage out = new NativeImage(width, height, false);
		
		for (int u = 0; u < width; u++)
		{
			for (int v = 0; v < height; v++)
			{
				out.setPixelRGBA(getMirroredU(u, width), getMirroredV(v, height), in.getPixelRGBA(u, v));
			}
		}
		
		return out;
	}
	
	public static ITextureOperator getMirror(String mirrorName)
	{
		switch (mirrorName.toLowerCase())
		{
		case "h":
		case "horizontal":
			return HORIZONTAL;
		case "v":
		case "vertical":
			return VERTICAL;
		default:
			throw new IllegalArgumentException("Invalid mirror type  : " + mirrorName);
		}
	}
	
	public static ITextureOperator build(int inputCount, @Nullable JsonElement options)
	{
		if (inputCount != 1)
		{
			throw new JsonSyntaxException("The invalid input count : expected 1 but was " + inputCount);
		}
		if (options == null || (options.isJsonObject() && options.getAsJsonObject().entrySet().isEmpty()))
		{
			throw new JsonSyntaxException("No option provided to rotation operator");
		}
		
		if (options.isJsonPrimitive())
		{
			return getMirror(options.getAsString());
		}
		else if (options.isJsonObject() && options.getAsJsonObject().size() == 1)
		{
			return getMirror(JSONUtils.getString(options, "mirror"));
		}
		
		throw new JsonSyntaxException("The invalid option value: " + JSONUtils.toString(options));
	}
}
