package fr.max2.texturecombiner.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

public abstract class TextureProvider implements IDataProvider
{
	
	private class ExistingFileHelperIncludingGenerated extends ExistingFileHelper
	{
		private final ExistingFileHelper delegate;
		
		public ExistingFileHelperIncludingGenerated(ExistingFileHelper delegate)
		{
			super(Collections.emptyList(), true);
			this.delegate = delegate;
		}
		
		@Override
		public boolean exists(ResourceLocation loc, ResourcePackType type, String pathSuffix, String pathPrefix)
		{
			return generatedModels.containsKey(loc) || delegate.exists(loc, type, pathSuffix, pathPrefix);
		}
	}
	
	public static final String BLOCK_FOLDER = "block";
	public static final String ITEM_FOLDER = "item";
	
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	protected final DataGenerator generator;
	protected final String modid;
	protected final String folder;
	public final Map<ResourceLocation, TextureBuilder> generatedModels = new HashMap<>();
	public final ExistingFileHelper existingFileHelper;
	
	protected abstract void registerModels();
	
	public TextureProvider(DataGenerator generator, String modid, String folder, ExistingFileHelper existingFileHelper)
	{
		Preconditions.checkNotNull(generator);
		this.generator = generator;
		Preconditions.checkNotNull(modid);
		this.modid = modid;
		Preconditions.checkNotNull(folder);
		this.folder = folder;
		Preconditions.checkNotNull(existingFileHelper);
		this.existingFileHelper = new ExistingFileHelperIncludingGenerated(existingFileHelper);
	}
	
	public TextureBuilder getBuilder(String path)
	{
		Preconditions.checkNotNull(path, "Path must not be null");
		ResourceLocation outputLoc = extendWithFolder(path.contains(":") ? new ResourceLocation(path) : new ResourceLocation(modid, path));
		return generatedModels.computeIfAbsent(outputLoc, loc -> new TextureBuilder(loc, existingFileHelper));
	}
	
	private ResourceLocation extendWithFolder(ResourceLocation rl)
	{
		if (rl.getPath().contains("/"))
		{
			return rl;
		}
		return new ResourceLocation(rl.getNamespace(), folder + "/" + rl.getPath());
	}
	
	public ResourceLocation modLoc(String name)
	{
		return new ResourceLocation(modid, name);
	}
	
	public ResourceLocation mcLoc(String name)
	{
		return new ResourceLocation(name);
	}
	
	public TextureBuilder withOperator(String name, ResourceLocation operator) {
        return getBuilder(name).operator(operator);
    }
	
	public TextureFile getExistingFile(ResourceLocation path)
	{
		TextureFile ret = new TextureFile.ExistingTextureFile(extendWithFolder(path), existingFileHelper);
		ret.assertExistence();
		return ret;
	}
	
	protected void clear()
	{
		generatedModels.clear();
	}
	
	@Override
	public void act(DirectoryCache cache) throws IOException
	{
		clear();
		registerModels();
		generateAll(cache);
	}
	
	protected void generateAll(DirectoryCache cache)
	{
		for (TextureBuilder model : generatedModels.values())
		{
			Path target = getPath(model);
			try
			{
				IDataProvider.save(GSON, cache, model.toJson(), target);
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	private Path getPath(TextureBuilder texture)
	{
		ResourceLocation loc = texture.file().getLocation();
		return generator.getOutputFolder().resolve("assets/" + loc.getNamespace() + "/textures/" + loc.getPath() + ".png.json");
	}
	
}
