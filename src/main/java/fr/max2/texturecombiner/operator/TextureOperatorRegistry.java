package fr.max2.texturecombiner.operator;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import fr.max2.texturecombiner.TextureCombinerMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = TextureCombinerMod.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class TextureOperatorRegistry
{
	private static final Map<ResourceLocation, ITextureOperator.Builder> REGISTRY = new HashMap<>();
	
	/**
	 * Registers a texture operator builder. It the operator will be available in a texture json using the given registry name
	 * @param registryName the name used to identify the type of operation
	 * @param builder
	 */
	public static void registerBuilder(ResourceLocation registryName, ITextureOperator.Builder builder)
	{
        if (REGISTRY.containsKey(registryName))
            throw new IllegalStateException("Duplicated texture operation builder: " + registryName);
		REGISTRY.put(registryName, builder);
	}
	
	/**
	 * Builds a texture operator from its parameters
	 * @param registryName the registry name of the operator
	 * @param inputCount the number of given inputs
	 * @param options the other options given in the json file
	 * @return the generated operator
	 */
	public static ITextureOperator buildOperator(ResourceLocation name, int inputCount, @Nullable JsonElement options)
	{
		ITextureOperator.Builder builder = REGISTRY.get(name);
		
		if (builder == null)
			throw new JsonSyntaxException("Unknown texture operation type: " + name.toString());
		
		try
		{
			return builder.build(inputCount, options);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Couldn't build operator of type " + name, e);
		}
	}
	
}
