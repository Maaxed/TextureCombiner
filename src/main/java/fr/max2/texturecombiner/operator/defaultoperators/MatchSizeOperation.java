package fr.max2.texturecombiner.operator.defaultoperators;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import fr.max2.texturecombiner.operator.ITextureOperator;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.JSONUtils;

public enum MatchSizeOperation implements ITextureOperator
{
	NEAREST,
	FLOOR,
	CEIL;
	
	private static final double log2 = Math.log(2);
	
	private double evaluate(int targetWidth, int targetHeight, int w, int h)
	{
		double wexp = Math.log(w) / log2;
		double hexp = Math.log(h) / log2;
		double twexp = Math.log(targetWidth) / log2;
		double thexp = Math.log(targetHeight) / log2;
		double dist = (wexp - twexp) * (wexp - twexp) + (hexp - thexp) * (hexp - thexp);
		
		if ((this == FLOOR && (w > targetWidth || h > targetHeight)) ||
			(this == CEIL  && (w < targetWidth || h < targetHeight)))
		{
			dist -= 1000;
		}
		
		return dist;
	}
	
	@Override
	public NativeImage apply(NativeImage[] inputs)
	{
		NativeImage target = inputs[0];
		
		int targetWidth = target.getWidth();
		int targetHeight = target.getHeight();
		
		double bestValue = Float.POSITIVE_INFINITY;
		int bestIndex = -1;
		
		for (int i = 1; i < inputs.length; i++)
		{
			NativeImage img = inputs[1];
			double value = evaluate(targetWidth, targetHeight, img.getWidth(), img.getHeight());
			if (value > bestValue)
			{
				bestValue = value;
				bestIndex = i;
			}
		}
		
		return inputs[bestIndex];
	}
	
	private static ITextureOperator getSizeMatcher(String choiseMode)
	{
		switch (choiseMode.toLowerCase())
		{
		case "f":
		case "floor":
			return FLOOR;
		case "c":
		case "ceil":
			return CEIL;
		case "n":
		case "nearest":
			return NEAREST;
		default:
			throw new IllegalArgumentException("Invalid choice mode: " + choiseMode);
		}
	}
	
	public static ITextureOperator build(int inputCount, @Nullable JsonElement options)
	{
		if (inputCount < 3)
		{
			throw new JsonSyntaxException("Invalid input count: expected at least 3 but was " + inputCount);
		}
		
		if (options == null || (options.isJsonObject() && options.getAsJsonObject().entrySet().isEmpty()))
		{
			return NEAREST;
		}
		
		if (options.isJsonPrimitive())
		{
			return getSizeMatcher(options.getAsString());
		}
		else if (options.isJsonObject() && options.getAsJsonObject().size() == 1)
		{
			return getSizeMatcher(JSONUtils.getString(options.getAsJsonObject(), "choice_mode"));
		}
		
		throw new JsonSyntaxException("Invalid option value: " + JSONUtils.toString(options));
	}
	
}
