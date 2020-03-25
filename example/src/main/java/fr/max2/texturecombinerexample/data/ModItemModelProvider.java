package fr.max2.texturecombinerexample.data;

import fr.max2.texturecombinerexample.ModBlocks;
import fr.max2.texturecombinerexample.TextureCombinerExampleMod;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelProvider;

public class ModItemModelProvider extends ItemModelProvider
{

	public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper)
	{
		super(generator, TextureCombinerExampleMod.MOD_ID, existingFileHelper);
	}

	@Override
	public String getName()
	{
		return "TexComb Example Item Models";
	}

	@Override
	protected void registerModels()
	{
		simpleBlock(ModBlocks.APPLE_ORE);
		simpleBlock(ModBlocks.RED_STONE_GLASS);
	}
	
	protected void simpleBlock(Block block)
	{
		withExistingParent(block.getRegistryName().getPath(), blockModel(block));
	}

    protected ResourceLocation blockModel(Block block)
    {
        ResourceLocation name = block.getRegistryName();
        return new ResourceLocation(name.getNamespace(), BLOCK_FOLDER + "/" + name.getPath());
    }
}
