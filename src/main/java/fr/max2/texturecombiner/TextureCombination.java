package fr.max2.texturecombiner;

import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class TextureCombination implements ITextureBuilder
{
	/** the registry name of the operator (for debugging purpose only) */
	private final ResourceLocation operatorName;
	/** the operator that this combination applies to the inputs */
	private final ITextureOperator operator;
	/** the inputs that will be processed */
	private final ITextureBuilder[] inputs;
	
	public TextureCombination(ResourceLocation operatorName, ITextureOperator operator, ITextureBuilder[] inputs)
	{
		this.operatorName = operatorName;
		this.operator = operator;
		this.inputs = inputs;
	}

	@Override
	public NativeImage buildTexture(ITextureFinder finder)
	{
		// Generate all the input textures
		NativeImage[] images = new NativeImage[inputs.length];
		for (int i = 0; i < inputs.length; i++)
		{
			try
			{
				images[i] = inputs[i].buildTexture(finder);
			}
			catch (IOException e)
			{
				throw new TextureBuildingException("Could not build texture input " + i + " for operator " + operatorName.toString() + " : " + e.getMessage(), e);
			}
		}
		// Apply the operator on the inputs
		NativeImage result = operator.apply(images);
		
		for (int i = 0; i < inputs.length; i++)
		{
			images[i].close();
		}
		
		return result;
	}
	
	public static class TextureBuildingException extends RuntimeException
	{
		public TextureBuildingException(String message, Throwable cause)
		{
	        super(message, cause);
	    }
	}
	
	public static enum Parser implements JsonDeserializer<TextureCombination>
	{
		INSTANCE;

		@Override
		public TextureCombination deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			JsonObject obj = json.getAsJsonObject();
			
			ITextureBuilder[] inputs;
			JsonElement inputsJson = obj.get("inputs");
			
			if (inputsJson == null)
				inputsJson = obj.get("input");
			
			if (inputsJson == null)
			{
				inputs = new ITextureBuilder[0];
			}
			else if (inputsJson.isJsonArray())
			{
				JsonArray inputArray = inputsJson.getAsJsonArray();
				inputs = new ITextureBuilder[inputArray.size()];
				for (int i = 0; i < inputArray.size(); i++)
				{
					inputs[i] = parseTextureBuilder(inputArray.get(i), context);
				}
			}
			else
			{
				inputs = new ITextureBuilder[] { parseTextureBuilder(inputsJson, context) };
			}
			
			ResourceLocation operatorName = new ResourceLocation(JSONUtils.getString(obj, "operator"));
			
			ITextureOperator operator = TextureOperatorRegistry.buildOperator(operatorName, inputs.length, obj.get("options"));
			
			return new TextureCombination(operatorName, operator, inputs);
		}
		
		private static ITextureBuilder parseTextureBuilder(JsonElement json, JsonDeserializationContext context)
		{
			if (json.isJsonPrimitive())
			{
				return finder -> finder.find(new ResourceLocation(json.getAsString()));
			}
			else
			{
				return context.deserialize(json, TextureCombination.class);
			}
		}
		
	}
}
