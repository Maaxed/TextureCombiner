package fr.max2.texturecombiner.operator.defaultoperators;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

import fr.max2.texturecombiner.operator.ITextureOperator;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.JSONUtils;

public class ColorOperation implements ITextureOperator
{
	private int color;
	private int width, height;
	
	public ColorOperation(int color, int width, int height)
	{
		this.color = color;
		this.width = width;
		this.height = height;
	}

	@Override
	public NativeImage apply(NativeImage[] inputs)
	{
		NativeImage out = new NativeImage(width, height, false);
		out.fillAreaRGBA(0, 0, width, height, color);
		return out;
	}
	
	private static int toIntComponent(Number n)
	{
		if (n instanceof Float || n instanceof Double)
		{
			return (int)(n.floatValue() * 255);
		}
		else if (n instanceof Byte || n instanceof Short || n instanceof Integer || n instanceof Long)
		{
			return n.intValue();
		}
		
		throw new IllegalArgumentException("Invalid color component: " + n);
	}
	
	private static int exchangeARGBtoABGR(int argb)
	{
		int r = (argb >> 16) & 0xFF;
		int b = (argb & 0xFF) << 16;
		
		return (argb & 0xFF_00_FF_00) | r | b;
	}
	
	public static int getColor(JsonElement color)
	{
		if (color.isJsonPrimitive())
		{
			JsonPrimitive prim = color.getAsJsonPrimitive();
			if (prim.isString())
			{
				String str = prim.getAsString();
				if (str.startsWith("#"))
					str = str.substring(1);
				else if (str.startsWith("0x"))
					str = str.substring(2);
				
				int c = Integer.parseUnsignedInt(str, 16);
				
				if (str.length() <= 2)
				{
					c = (c << 16) | (c << 8) | c;
				}
				
				if (str.length() <= 6)
				{
					c = c | 0xFF_00_00_00;
				}
				
				return exchangeARGBtoABGR(c);
			}
			else if (prim.isNumber())
			{
				return exchangeARGBtoABGR(prim.getAsInt());
			}
		}
		else if (color.isJsonArray())
		{
			JsonArray array = color.getAsJsonArray();
			Number alpha = 0xFF;
			int startIndex = 0;
			if (array.size() == 4)
			{
				alpha = array.get(0).getAsNumber();
				startIndex++;
			}
			if (array.size() >= 3 && array.size() <= 4)
			{
				Number r = array.get(startIndex + 0).getAsNumber();
				Number g = array.get(startIndex + 1).getAsNumber();
				Number b = array.get(startIndex + 2).getAsNumber();
				
				return NativeImage.getCombined(toIntComponent(alpha), toIntComponent(b), toIntComponent(g), toIntComponent(r));
			}
		}

		throw new IllegalArgumentException("Invalid color value: " + color);
	}
	
	public static ITextureOperator build(int inputCount, @Nullable JsonElement options)
	{
		if (inputCount != 0)
		{
			throw new JsonSyntaxException("Invalid input count: expected 0 but was " + inputCount);
		}
		if (options == null || (options.isJsonObject() && options.getAsJsonObject().entrySet().isEmpty()))
		{
			throw new JsonSyntaxException("No option provided to color operator");
		}
		
		if (options.isJsonObject() && options.getAsJsonObject().size() <= 3)
		{
			JsonObject obj = options.getAsJsonObject();
			
			int interp = getColor(obj.get("color"));
			if (obj.size() <= 1)
			{
				return new ColorOperation(interp, 1, 1);
			}
			else if (obj.size() <= 2)
			{
				int size = JSONUtils.getInt(obj, "size");
				return new ColorOperation(interp, size, size);
			}
			else
			{
				int width = JSONUtils.getInt(obj, "width");
				int height = JSONUtils.getInt(obj, "height");
				return new ColorOperation(interp, width, height);
			}
		}
		
		throw new JsonSyntaxException("Invalid option value: " + JSONUtils.toString(options));
	}
}
