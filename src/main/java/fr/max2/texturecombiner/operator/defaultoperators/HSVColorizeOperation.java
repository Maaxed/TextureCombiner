package fr.max2.texturecombiner.operator.defaultoperators;

import java.awt.Color;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import fr.max2.texturecombiner.operator.DefaultFloatOperator;
import fr.max2.texturecombiner.operator.IFloatOperator;
import fr.max2.texturecombiner.operator.ITextureOperator;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.MathHelper;

public class HSVColorizeOperation implements ITextureOperator
{
	public static final HSVColorizeOperation DEFAULT = new HSVColorizeOperation(DefaultFloatOperator.SECOND, DefaultFloatOperator.SECOND, DefaultFloatOperator.PRODUCT, DefaultFloatOperator.FIRST);
	
	private IFloatOperator hueOperator, saturationOperator, valueOperator, alphaOperator;  
	
	public HSVColorizeOperation(IFloatOperator hueOp, IFloatOperator satOp, IFloatOperator valOp, IFloatOperator alphaOp)
	{
		this.hueOperator = hueOp;
		this.saturationOperator = satOp;
		this.valueOperator = valOp;
		this.alphaOperator = alphaOp;
	}

	@Override
	public NativeImage apply(NativeImage[] inputs)
	{
		return ITextureOperator.applyPerPixel(inputs, pixels ->
		{
			int a0 = (pixels[0] >> 24) & 0xFF;
			int b0 = (pixels[0] >> 16) & 0xFF;
			int g0 = (pixels[0] >> 8) & 0xFF;
			int r0 = pixels[0] & 0xFF;
			
			int a1 = (pixels[1] >> 24) & 0xFF;
			int b1 = (pixels[1] >> 16) & 0xFF;
			int g1 = (pixels[1] >> 8) & 0xFF;
			int r1 = pixels[1] & 0xFF;
			
			float[] hsb0 = Color.RGBtoHSB(r0, g0, b0, null);
			float[] hsb1 = Color.RGBtoHSB(r1, g1, b1, null);
			
			float h = hueOperator.apply(hsb0[0], hsb1[0]);
			float s = MathHelper.clamp(saturationOperator.apply(hsb0[1], hsb1[1]), 0f, 1f);
			float v = MathHelper.clamp(valueOperator.apply(hsb0[2], hsb1[2]), 0f, 1f);
			int a = MathHelper.clamp((int)(alphaOperator.apply(a0 / 255f, a1 / 255f) * 255), 0, 255);
			
			int color = Color.HSBtoRGB(h, s, v);
			
			int blue = (color & 0xFF) << 16;
			int green = color & 0x00_FF_00;
			int red = (color >> 16) & 0xFF;
			
			return (a << 24) | blue | green | red;
		});
	}
	
	public static ITextureOperator build(int inputCount, @Nullable JsonElement options)
	{
		if (inputCount != 2)
		{
			throw new JsonSyntaxException("Invalid input count: expected 2 but was " + inputCount);
		}
		if (options == null || (options.isJsonObject() && options.getAsJsonObject().entrySet().isEmpty()))
		{
			return DEFAULT;
		}
		
		if (options.isJsonObject() && options.getAsJsonObject().size() <= 4)
		{
			JsonObject obj = options.getAsJsonObject();
			
			IFloatOperator hueOp = DefaultFloatOperator.SECOND;
			IFloatOperator satOp = DefaultFloatOperator.SECOND;
			IFloatOperator valOp = DefaultFloatOperator.PRODUCT;
			IFloatOperator alphaOp = DefaultFloatOperator.FIRST;
			
			for (Entry<String, JsonElement> entry : obj.entrySet())
			{
				switch (entry.getKey())
				{
				case "hue":
					hueOp = DefaultFloatOperator.getOperator(JSONUtils.getString(entry.getValue(), entry.getKey()));
					break;
				case "saturation":
					satOp = DefaultFloatOperator.getOperator(JSONUtils.getString(entry.getValue(), entry.getKey()));
					break;
				case "value":
				case "brightness":
					valOp = DefaultFloatOperator.getOperator(JSONUtils.getString(entry.getValue(), entry.getKey()));
					break;
				case "alpha":
					alphaOp = DefaultFloatOperator.getOperator(JSONUtils.getString(entry.getValue(), entry.getKey()));
					break;
				default:
					throw new JsonSyntaxException("Invalid option key: " + entry.getKey());
				}
			}
			
			return new HSVColorizeOperation(hueOp, satOp, valOp, alphaOp);
		}
		
		throw new JsonSyntaxException("Invalid option value: " + JSONUtils.toString(options));
	}
	
}
