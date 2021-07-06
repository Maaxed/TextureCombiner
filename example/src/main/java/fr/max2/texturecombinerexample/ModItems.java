package fr.max2.texturecombinerexample;

import java.util.stream.Stream;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemTier;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(modid = TextureCombinerExampleMod.MOD_ID, bus = Bus.MOD)
@ObjectHolder(TextureCombinerExampleMod.MOD_ID)
public class ModItems
{
	public static final EquipmentSlotType[] ARMOR_SLOTS = new EquipmentSlotType[] { EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET };
	
	public static final Item REVERSE_SWORD = null;
	public static final Item EMERALD_HELMET = null, EMERALD_CHESTPLATE = null, EMERALD_LEGGINGS = null, EMERALD_BOOTS = null;
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(
			loc("reverse_sword", new SwordItem(ItemTier.DIAMOND, 3, -2.4F, (new Item.Properties()).group(ItemGroup.COMBAT)))
		);
		
		Stream.of(ARMOR_SLOTS).flatMap(slot ->
			Stream.of(ModArmorMaterials.values()).map(mat ->
				loc(mat.getName().substring(mat.getName().indexOf(":")) + "_" + armorPartName(slot),
					new ArmorItem(mat, slot, new Item.Properties().group(ItemGroup.COMBAT))
				)
			)
		).forEach(event.getRegistry()::register);
	}
	
	private static <T extends IForgeRegistryEntry<T>> T loc(String name, T obj)
	{
		obj.setRegistryName(new ResourceLocation(TextureCombinerExampleMod.MOD_ID, name));
		return obj;
	}
	
	public static String armorPartName(EquipmentSlotType slot)
	{
		switch (slot)
		{
		case HEAD: return "helmet";
		case CHEST: return "chestplate";
		case LEGS: return "leggings";
		case FEET: return "boots";
		default: return "error";
		}
	}
}
