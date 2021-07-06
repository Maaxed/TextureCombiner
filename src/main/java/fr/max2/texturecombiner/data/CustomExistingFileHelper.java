package fr.max2.texturecombiner.data;

import java.lang.reflect.Field;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.fml.ModLoader;

public class CustomExistingFileHelper extends ExistingFileHelper
{
	private static final Logger LOGGER = LogManager.getLogger();

	private final ExistingFileHelper delegate;

	public CustomExistingFileHelper(ExistingFileHelper delegate)
	{
		super(Collections.emptyList(), true);
		this.delegate = delegate;
	}
	
	@Override
	public boolean exists(ResourceLocation loc, ResourcePackType type, String pathSuffix, String pathPrefix)
	{
		if (delegate.exists(loc, type, pathSuffix, pathPrefix))
			return true;
		
		if (pathSuffix.endsWith(".png"))
			return delegate.exists(loc, type, pathSuffix + ".json", pathPrefix);
		
		return false;
	}
	
	public static final void fixExistingFileHelper()
	{
		ModLoader ml = ModLoader.get();
		try
		{
			Field fileHelperField = ModLoader.class.getDeclaredField("existingFileHelper");
			fileHelperField.setAccessible(true);
			
			ExistingFileHelper oldFileHelper = (ExistingFileHelper) fileHelperField.get(ml);
			
			ExistingFileHelper newFileHelper = new CustomExistingFileHelper(oldFileHelper);
			
			fileHelperField.set(ml, newFileHelper);
		}
		catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
		{
			LOGGER.warn("Error during ExistingFileHelper fixing phase : {}" + e);
			e.printStackTrace();
		}
	}
	
}
