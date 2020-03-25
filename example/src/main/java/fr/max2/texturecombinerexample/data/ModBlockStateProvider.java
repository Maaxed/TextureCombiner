package fr.max2.texturecombinerexample.data;

import javax.annotation.Nonnull;

import fr.max2.texturecombinerexample.ModBlocks;
import fr.max2.texturecombinerexample.TextureCombinerExampleMod;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider
{

	public ModBlockStateProvider(DataGenerator generator, ExistingFileHelper existingFileHelper)
	{
		super(generator, TextureCombinerExampleMod.MOD_ID, existingFileHelper);
	}

	@Override
	@Nonnull
	public String getName()
	{
		return "TexComb Example BlockStates";
	}

	@Override
	protected void registerStatesAndModels()
	{
		simpleBlock(ModBlocks.APPLE_ORE);
		simpleBlock(ModBlocks.RED_STONE_GLASS);
	}
	
}
