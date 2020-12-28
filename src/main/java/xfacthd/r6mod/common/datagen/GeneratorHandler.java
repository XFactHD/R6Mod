package xfacthd.r6mod.common.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.datagen.providers.internal.*;
import xfacthd.r6mod.common.datagen.providers.model.block.*;
import xfacthd.r6mod.common.datagen.providers.model.item.*;
import xfacthd.r6mod.common.datagen.providers.sound.SoundProvider;

@Mod.EventBusSubscriber(modid = R6Mod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GeneratorHandler
{
    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent event)
    {
        final DataGenerator gen = event.getGenerator();
        final ExistingFileHelper fileHelper = event.getExistingFileHelper();

        //Block
        gen.addProvider(new BarricadeStateProvider(gen, fileHelper));
        gen.addProvider(new WallStateProvider(gen, fileHelper));
        gen.addProvider(new BlackMirrorStateProvider(gen, fileHelper));
        gen.addProvider(new ReinforcementStateProvider(gen, fileHelper));
        gen.addProvider(new FloorPanelStateProvider(gen, fileHelper));

        //Item
        gen.addProvider(new EmptyGunModelProvider(gen, fileHelper));

        //Sound
        gen.addProvider(new SoundProvider(gen));

        //Internal
        //gen.addProvider(new GunDefinitionProvider(gen)); //DO NOT USE
    }
}