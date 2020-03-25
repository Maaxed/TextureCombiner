package fr.max2.texturecombiner.defaultoperators;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import fr.max2.texturecombiner.ITextureOperator;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.JSONUtils;

public enum RotateOperation implements ITextureOperator
{
	CLOCKWISE_90,
	CLOCKWISE_180,
	COUNTERCLOCKWISE_90;
	
	private int getRotatedU(int u, int v, int w, int h)
	{
		switch (this)
		{
		case CLOCKWISE_180:
			return w-u;
		case CLOCKWISE_90:
			return v;
		case COUNTERCLOCKWISE_90:
			return h-v;
		default:
			return u;
		}
	}
	
	private int getRotatedV(int u, int v, int w, int h)
	{
		switch (this)
		{
		case CLOCKWISE_180:
			return h-v;
		case CLOCKWISE_90:
			return w-u;
		case COUNTERCLOCKWISE_90:
			return u;
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
		int outWidth = this == CLOCKWISE_180 ? width : height;
		int outHeight = this == CLOCKWISE_180 ? height : width;
		NativeImage out = new NativeImage(outWidth, outHeight, false);
		
		for (int u = 0; u < width; u++)
		{
			for (int v = 0; v < height; v++)
			{
				out.setPixelRGBA(getRotatedU(u, v, width, height), getRotatedV(u, v, width, height), in.getPixelRGBA(u, v));
			}
		}
		
		return out;
	}
	
	private static final ITextureOperator[] ROTATIONS = { IdentityOperation.INSTANCE, CLOCKWISE_90, CLOCKWISE_180, COUNTERCLOCKWISE_90 };
	
	public static ITextureOperator getRotation(int rot)
	{
		if (rot % 90 != 0)
			throw new IllegalArgumentException("The rotation should be a multiple of 90, bot was : " + rot);
		
		// Put rot to a [0, 360[ range
		rot = rot % 360;
		if (rot < 0)
			rot += 180;
		
		rot /= 90;
		
		return ROTATIONS[rot];
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
			return getRotation(options.getAsInt());
		}
		else if (options.isJsonObject() && options.getAsJsonObject().size() == 1)
		{
			return getRotation(JSONUtils.getInt(options, "rotation"));
		}
		
		throw new JsonSyntaxException("The invalid option value: " + JSONUtils.toString(options));
	}
}
