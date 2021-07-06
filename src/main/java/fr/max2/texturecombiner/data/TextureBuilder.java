package fr.max2.texturecombiner.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

public class TextureBuilder
{
	protected TextureBuilder parent;
	protected TextureFile file;
	protected ResourceLocation operator;
	private List<Supplier<JsonElement>> inputs = new ArrayList<>();
    protected final ExistingFileHelper existingFileHelper;

    protected TextureBuilder(ResourceLocation location, ExistingFileHelper existingFileHelper)
    {
        this.existingFileHelper = existingFileHelper;
		this.file = new BuildedTextureFile(location);
    }

    protected TextureBuilder(TextureBuilder parent)
    {
        this.existingFileHelper = parent.existingFileHelper;
		this.parent = parent;
    }
    
	public TextureBuilder operator(ResourceLocation operator)
	{
		Preconditions.checkNotNull(operator, "Operator must not be null");
		this.operator = operator;
		return this;
	}
	
	public TextureFile file()
	{
		return this.file;
	}
	
	public TextureBuilder parent()
	{
		return this.parent;
	}
	
	public TextureBuilder input(TextureFile input)
	{
		Preconditions.checkNotNull(input, "Input must not be null");
		this.inputs.add(() -> new JsonPrimitive(input.getLocation().toString()));
		return this;
	}
	
	public TextureBuilder input(ResourceLocation texture)
	{
        Preconditions.checkNotNull(texture, "Texture must not be null");
        Preconditions.checkArgument(existingFileHelper.exists(texture, ResourcePackType.CLIENT_RESOURCES, ".png", "textures"),
            "Texture %s does not exist in any known resource pack", texture);
		this.inputs.add(() -> new JsonPrimitive(texture.toString()));
		return this;
	}
	
	public TextureBuilder operationInput()
	{
		TextureBuilder builder = new TextureBuilder(this);
		this.inputs.add(() -> builder.toJson());
		return builder;
	}
	
	public JsonObject toJson()
	{
		JsonObject obj = new JsonObject();
		obj.addProperty("operator", operator.toString());
		
		JsonArray in = new JsonArray();
		
		this.inputs.forEach(provider -> in.add(provider.get()));
		
		obj.add("inputs", in);
		return obj;
	}
	
	private static class BuildedTextureFile extends TextureFile
	{
		protected BuildedTextureFile(ResourceLocation location)
		{
			super(location);
		}

		@Override
		protected boolean exists()
		{
			return true;
		}
	}
}
