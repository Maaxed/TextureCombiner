package fr.max2.texturecombinerexample;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.block.OreBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(modid = TextureCombinerExampleMod.MOD_ID, bus = Bus.MOD)
@ObjectHolder(TextureCombinerExampleMod.MOD_ID)
public class ModBlocks
{
	public static final Block APPLE_ORE = null;
	public static final Block RED_STONE_GLASS = null;
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().registerAll(
			loc("apple_ore", new OreBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.0F, 3.0F))),
			loc("red_stone_glass", new Block(Block.Properties.create(Material.ROCK, MaterialColor.STONE).hardnessAndResistance(1.5F, 6.0F)))
		);
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(items(
			APPLE_ORE,
			RED_STONE_GLASS
		));
	}
	
	private static <T extends IForgeRegistryEntry<T>> T loc(String name, T obj)
	{
		obj.setRegistryName(new ResourceLocation(TextureCombinerExampleMod.MOD_ID, name));
		return obj;
	}
	
	private static <T extends IForgeRegistryEntry<?>> T loc(T obj, IForgeRegistryEntry<?> src)
	{
		obj.setRegistryName(src.getRegistryName());
		return obj;
	}
	
	private static BlockItem item(Block block)
	{
		return loc(new BlockItem(block, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)), block);
	}
	
	private static BlockItem[] items(Block... blocks)
	{
		BlockItem[] items = new BlockItem[blocks.length];
		
		for (int i = 0; i < blocks.length; i++)
		{
			items[i] = item(blocks[i]);
		}
		
		return items;
	}
}
