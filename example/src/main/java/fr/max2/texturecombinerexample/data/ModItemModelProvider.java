package fr.max2.texturecombinerexample.data;

import java.util.stream.Stream;

import fr.max2.texturecombinerexample.ModArmorMaterials;
import fr.max2.texturecombinerexample.ModBlocks;
import fr.max2.texturecombinerexample.ModItems;
import fr.max2.texturecombinerexample.TextureCombinerExampleMod;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.registries.IForgeRegistryEntry;

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
		
		toolItem(ModItems.REVERSE_SWORD);
		
		Stream.of(ModItems.ARMOR_SLOTS).flatMap(slot ->
			Stream.of(ModArmorMaterials.values()).map(mat ->
				new ResourceLocation(TextureCombinerExampleMod.MOD_ID, mat.getName().substring(mat.getName().indexOf(":")) + "_" + ModItems.armorPartName(slot))
			)
		).forEach(this::simpleItem);
	}
	
	protected void simpleItem(IForgeRegistryEntry<?> entry)
	{
		singleTexture(name(entry), mcLoc("item/generated"), "layer0", itemTexture(entry));
	}
	
	protected void simpleItem(ResourceLocation name)
	{
		singleTexture(name.getPath(), mcLoc("item/generated"), "layer0", new ResourceLocation(name.getNamespace(), ITEM_FOLDER + "/" + name.getPath()));
	}
	
	protected void toolItem(IForgeRegistryEntry<?> entry)
	{
		singleTexture(name(entry), mcLoc("item/handheld"), "layer0", itemTexture(entry));
	}
	
	protected void simpleBlock(Block block)
	{
		withExistingParent(block.getRegistryName().getPath(), blockModel(block));
	}

    protected ResourceLocation itemTexture(IForgeRegistryEntry<?> entry)
    {
        ResourceLocation name = entry.getRegistryName();
        return new ResourceLocation(name.getNamespace(), (entry instanceof Block ? BLOCK_FOLDER : ITEM_FOLDER) + "/" + name.getPath());
    }

    protected ResourceLocation blockModel(Block block)
    {
        ResourceLocation name = block.getRegistryName();
        return new ResourceLocation(name.getNamespace(), BLOCK_FOLDER + "/" + name.getPath());
    }

    protected String name(IForgeRegistryEntry<?> entry)
    {
        return entry.getRegistryName().getPath();
    }
}
