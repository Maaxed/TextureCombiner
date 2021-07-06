package fr.max2.texturecombiner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.max2.texturecombiner.operator.TextureCombination;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.packs.DelegatableResourcePack;

public class CustomResourcePack extends DelegatableResourcePack
{
	public static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(TextureCombination.class, TextureCombination.Parser.INSTANCE)
		.create();
	
	
	private final ModFile modFile;
	private final IResourceManager resourceManager;

	protected CustomResourcePack(ModFile modFile)
	{
		super(new File("dummy"));
		this.modFile = modFile;
		this.resourceManager = Minecraft.getInstance().getResourceManager();
	}
	
	@Override
	public String getName()
	{
		return TextureCombinerMod.MOD_ID + ":" + modFile.getFileName();
	}

	@Override
	public InputStream getInputStream(String name) throws IOException
	{
		if (name.equals("pack.mcmeta"))
		{
			final Path path = modFile.getLocator().findPath(modFile, name);
			return Files.newInputStream(path, StandardOpenOption.READ);
		}
		final Path path = modFile.getLocator().findPath(modFile, name + ".json");
		try (InputStream jsonStream = Files.newInputStream(path, StandardOpenOption.READ))
		{
			try (Reader jsonReader = new InputStreamReader(jsonStream))
			{
				TextureCombination combination;
				try
				{
					combination = JSONUtils.fromJson(GSON, jsonReader, TextureCombination.class);
					if (combination == null)
						return null;
				}
				catch (RuntimeException e)
				{
					e.printStackTrace();
					throw e;
				}
				
				try (NativeImage img = combination.buildTexture(this::getImage))
				{
					return new ByteArrayInputStream(img.getBytes());
				}
			}
		}
	}
	
	private NativeImage getImage(ResourceLocation location) throws IOException
	{
		try (IResource res = resourceManager.getResource(getSpritePath(location)))
		{
			return NativeImage.read(res.getInputStream());
		}
	}

	private static ResourceLocation getSpritePath(ResourceLocation location)
	{
		if (location.getPath().startsWith("textures/"))
			return location;
		
		return new ResourceLocation(location.getNamespace(), "textures/" + location.getPath() + ".png");
	}

	@Override
	public boolean resourceExists(String name)
	{
		if (!name.equals("pack.mcmeta"))
		{
			if (!name.endsWith(".png"))
				return false;
			name += ".json";
		}
		return Files.exists(modFile.getLocator().findPath(modFile, name));
	}

	@Override
	public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String namespace, String pathIn, int maxDepth, Predicate<String> filter)
	{
		try
		{		
			Path root = modFile.getLocator().findPath(modFile, type.getDirectoryName()).toAbsolutePath();
			Path inputPath = root.getFileSystem().getPath(pathIn);
			
			return Files.walk(root)
				.map(path -> root.relativize(path.toAbsolutePath()))
				.filter(path -> path.getNameCount() > 1 && path.getNameCount() - 1 <= maxDepth) // Make sure the depth is within bounds, ignoring domain
				.filter(path -> path.toString().endsWith(".png.json")) // Filter .json files
				.filter(path -> path.subpath(1, path.getNameCount()).startsWith(inputPath)) // Make sure the target path is inside this one (again ignoring domain)
				.filter(path -> filter.test(path.getFileName().toString())) // Test the file name against the predicate
				// Finally we need to form the RL, so use the first name as the domain, and the rest as the path
				// It is VERY IMPORTANT that we do not rely on Path.toString as this is inconsistent between operating systems
				// Join the path names ourselves to force forward slashes
				.map(path -> new ResourceLocation(path.getName(0).toString(), Joiner.on('/').join(path.subpath(1,Math.min(maxDepth, path.getNameCount())))))
				.collect(Collectors.toList());
		}
		catch (IOException e)
		{
		    return Collections.emptyList();
		}
	}

	@Override
	public Set<String> getResourceNamespaces(ResourcePackType type)
	{
		try
		{
			Path root = modFile.getLocator().findPath(modFile, type.getDirectoryName()).toAbsolutePath();
			return Files.walk(root,1)
				.map(path -> root.relativize(path.toAbsolutePath()))
				.filter(path -> path.getNameCount() > 0) // skip the root entry
				.map(p->p.toString().replaceAll("/$","")) // remove the trailing slash, if present
				.filter(s -> !s.isEmpty()) //filter empty strings, otherwise empty strings default to minecraft in ResourceLocations
	.collect(Collectors.toSet());
		}
		catch (IOException e)
		{
			return Collections.emptySet();
}
	}

	@Override
	public void close() throws IOException
	{
		
	}
	
}
