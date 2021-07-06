package fr.max2.texturecombiner.data;

import com.google.common.base.Preconditions;

import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

public abstract class TextureFile
{
	protected ResourceLocation location;
	
	protected TextureFile(ResourceLocation location)
	{
		this.location = location;
	}
	
	protected abstract boolean exists();
	
	public ResourceLocation getLocation()
	{
		assertExistence();
		return location;
	}
	
	/**
	 * Assert that this texture exists.
	 * 
	 * @throws IllegalStateException
	 *             if this texture does not exist
	 */
	public void assertExistence()
	{
		Preconditions.checkState(exists(), "Texture at %s does not exist", location);
	}
	
	public ResourceLocation getUncheckedLocation()
	{
		return location;
	}
	
	public static class UncheckedTextureFile extends TextureFile
	{
		public UncheckedTextureFile(String location)
		{
			this(new ResourceLocation(location));
		}
		
		public UncheckedTextureFile(ResourceLocation location)
		{
			super(location);
		}
		
		@Override
		protected boolean exists()
		{
			return true;
		}
	}
	
	public static class ExistingTextureFile extends TextureFile
	{
		private final ExistingFileHelper existingHelper;
		
		public ExistingTextureFile(ResourceLocation location, ExistingFileHelper existingHelper)
		{
			super(location);
			this.existingHelper = existingHelper;
		}
		
		@Override
		protected boolean exists()
		{
			String suffix = "";
			if (!getUncheckedLocation().getPath().contains("."))
			{
				suffix = ".png.json";
			}
			else if (!getUncheckedLocation().getPath().endsWith(".json"))
			{
				suffix = ".json";
			}
			return existingHelper.exists(getUncheckedLocation(), ResourcePackType.CLIENT_RESOURCES, suffix, "textures");
		}
	}
}