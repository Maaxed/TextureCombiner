package fr.max2.texturecombinerexample.data;

import fr.max2.texturecombinerexample.TextureCombinerExampleMod;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@EventBusSubscriber(modid = TextureCombinerExampleMod.MOD_ID, bus = Bus.MOD)
public class ModDataProviders
{

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        
        if (event.includeClient())
        {
        	ModBlockStateProvider blockStates = new ModBlockStateProvider(gen, event.getExistingFileHelper());
            gen.addProvider(blockStates);
            gen.addProvider(new ModItemModelProvider(gen, blockStates.models().existingFileHelper));
        }
    }
	
}
