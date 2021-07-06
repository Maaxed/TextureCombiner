package fr.max2.texturecombinerexample;

import java.util.function.Supplier;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.block.Blocks;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum ModArmorMaterials implements IArmorMaterial
{
	EMERALD("emerald", 33, new int[]{3, 6, 8, 3}, 10, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F, () -> Items.EMERALD),
	OBIDIAN("obsidian", 33, new int[]{3, 6, 8, 3}, 10, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F, () -> Blocks.OBSIDIAN);
	
	private static final int[] DURABILITY_ARRAY = new int[]{13, 15, 16, 11};
	private final String name;
	private final int durabilityFactor;
	private final int[] damageReductionAmountArray;
	private final int enchantability;
	private final SoundEvent soundEvent;
	private final float toughness;
	private final LazyValue<Ingredient> repairMaterial;
	
	private ModArmorMaterials(String name, int durabilitFactor, int[] damageReduction, int enchantability, SoundEvent equipSound, float toughness, Supplier<IItemProvider> repairMaterial)
	{
		this.name = TextureCombinerExampleMod.MOD_ID + ":" + name;
		this.durabilityFactor = durabilitFactor;
		this.damageReductionAmountArray = damageReduction;
		this.enchantability = enchantability;
		this.soundEvent = equipSound;
		this.toughness = toughness;
		this.repairMaterial = new LazyValue<>(() -> Ingredient.fromItems(repairMaterial.get()));
	}
	
	@Override
	public int getDurability(EquipmentSlotType slotIn)
	{
		return DURABILITY_ARRAY[slotIn.getIndex()] * this.durabilityFactor;
	}
	
	@Override
	public int getDamageReductionAmount(EquipmentSlotType slotIn)
	{
		return this.damageReductionAmountArray[slotIn.getIndex()];
	}
	
	@Override
	public int getEnchantability()
	{
		return this.enchantability;
	}
	
	@Override
	public SoundEvent getSoundEvent()
	{
		return this.soundEvent;
	}
	
	@Override
	public Ingredient getRepairMaterial()
	{
		return this.repairMaterial.getValue();
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public String getName()
	{
		return this.name;
	}
	
	@Override
	public float getToughness()
	{
		return this.toughness;
	}
}
