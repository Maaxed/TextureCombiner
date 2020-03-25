package fr.max2.texturecombiner;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.packs.DelegatableResourcePack;
import net.minecraftforge.fml.packs.DelegatingResourcePack;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.max2.texturecombiner.defaultoperators.GrayscaleOperation;
import fr.max2.texturecombiner.defaultoperators.LayersOperation;
import fr.max2.texturecombiner.defaultoperators.MirrorOperation;
import fr.max2.texturecombiner.defaultoperators.RotateOperation;

@Mod(TextureCombinerMod.MOD_ID)
public class TextureCombinerMod
{
	public static final String MOD_ID = "texturecombiner";
	private static final Logger LOGGER = LogManager.getLogger();
	
	public TextureCombinerMod()
	{
		
		if (FMLLoader.launcherHandlerName().toLowerCase().contains("data"))
		{
			CustomExistingFileHelper.fixExistingFileHelper();
		}
		else
		{
			DistExecutor.runWhenOn(Dist.CLIENT, () -> TextureCombinerMod::setupClientPackFinder);
		}
	}

	/**
	 * Initializes the client pack finder
	 */
	@OnlyIn(Dist.CLIENT)
	private static void setupClientPackFinder()
	{
		Minecraft.getInstance().getResourcePackList().addPackFinder(TextureCombinerMod::findPacks);
		// Initialize default texture operators
		TextureOperatorRegistry.registerBuilder(new ResourceLocation(MOD_ID, "layers"), LayersOperation::build);
		TextureOperatorRegistry.registerBuilder(new ResourceLocation(MOD_ID, "grayscale"), GrayscaleOperation::build);
		TextureOperatorRegistry.registerBuilder(new ResourceLocation(MOD_ID, "rotate"), RotateOperation::build);
		TextureOperatorRegistry.registerBuilder(new ResourceLocation(MOD_ID, "mirror"), MirrorOperation::build);
	}

	/**
	 * Creates a custom resource pack containing all the generated textures and adds it to the given map
	 * @param <T> the type of information to crate
	 * @param packList 
	 * @param factory
	 */
	@OnlyIn(Dist.CLIENT)
	private static <T extends ResourcePackInfo> void findPacks(Map<String, T> packList, ResourcePackInfo.IFactory<T> factory)
	{
		// Create custom resource packs
		List<DelegatableResourcePack> packs = new ArrayList<>();
		ModList.get().getModFiles().stream()
			.filter(mf -> !Objects.equals(mf.getModLoader(), "minecraft"))
			.forEach(file ->
			{
				IModInfo mod = file.getFile().getModInfos().get(0);
				
				if (Objects.equals(mod.getModId(), "minecraft"))
					return;
				
				LOGGER.debug("Adding custom resource pack for " + mod.getModId());
				packs.add(new CustomResourcePack(file.getFile()));
			});
		
		// Create a composite texture pack containing all the generated packs 
		String groupName = MOD_ID + ":generated_mod_resources";
		T packGroup = ResourcePackInfo.createResourcePack(groupName, true, () ->
			new DelegatingResourcePack(groupName, "Generated Mod Resources",
			new PackMetadataSection(new TranslationTextComponent(MOD_ID + ".resources.modresources", packs.size()), 5),
			packs), factory, ResourcePackInfo.Priority.TOP);
		
		// Register the pack in the map
		packList.put(groupName, packGroup);
	}
}
