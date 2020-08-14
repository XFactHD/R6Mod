/*  Copyright (C) <2017>  <XFactHD>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses. */

package XFactHD.rssmc;

import XFactHD.rssmc.common.CommonProxy;
import XFactHD.rssmc.common.Content;
import XFactHD.rssmc.common.utils.helper.LogHelper;
import XFactHD.rssmc.common.utils.Reference;
import XFactHD.rssmc.common.utils.utilClasses.CreativeTabRSSMC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = Reference.DEPENDENCIES)
public class RainbowSixSiegeMC
{
    public static final boolean DEOBF_ENV = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    public static final boolean DEBUG = false;

    @Mod.Instance
    public static RainbowSixSiegeMC INSTANCE;

    @SidedProxy(serverSide = Reference.SERVER_PROXY, clientSide = Reference.CLIENT_PROXY)
    public static CommonProxy proxy;

    static { MinecraftForge.EVENT_BUS.register(Content.class); }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LogHelper.info("Hello Minecraft!");
        LogHelper.info("Starting PreInit!");
        if (DEOBF_ENV) { System.getProperties().setProperty("forge.verboseMissingModelLoggingCount", "20"); }
        proxy.preInit(event);
        LogHelper.info("Finished PreInit!");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        LogHelper.info("Starting Init!");
        CT.prepareTabSorters();
        proxy.init(event);
        LogHelper.info("Finished Init!");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        LogHelper.info("Starting PostInit!");
        proxy.postInit(event);
        LogHelper.info("Finished PostInit!");
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) { }

    public static final class CT
    {
        public static CreativeTabRSSMC gunTab = new CreativeTabRSSMC(Reference.MOD_ID + ":guns.name")
        {
            @Override
            public String getBackgroundImageName()
            {
                return "item_search.png";
            }

            @Override
            public Item getTabIconItem()
            {
                return Content.itemGun;
            }

            @Override
            public boolean hasSearchBar()
            {
                return true;
            }

            @Override
            public<F extends ItemStack> void prepareTabSorter()
            {

            }
        };

        public static CreativeTabRSSMC ammoTab = new CreativeTabRSSMC(Reference.MOD_ID + ":ammo.name")
        {
            @Override
            public String getBackgroundImageName()
            {
                return "item_search.png";
            }

            @Override
            public Item getTabIconItem()
            {
                return null;
            }

            @Override
            public ItemStack getIconItemStack()
            {
                return new ItemStack(Content.itemAmmo, 1, 29);
            }

            @Override
            public boolean hasSearchBar()
            {
                return true;
            }

            @Override
            public<F extends ItemStack> void prepareTabSorter()
            {

            }
        };

        public static CreativeTabRSSMC armorTab = new CreativeTabRSSMC(Reference.MOD_ID + ":armor.name")
        {
            @Override
            public String getBackgroundImageName()
            {
                return "item_search.png";
            }

            @Override
            public Item getTabIconItem()
            {
                return Content.itemRookUpgrade;
            }

            @Override
            public boolean hasSearchBar()
            {
                return true;
            }

            @Override
            public<F extends ItemStack> void prepareTabSorter() { }
        };

        public static CreativeTabRSSMC gadgetTab = new CreativeTabRSSMC(Reference.MOD_ID + ":gadget.name")
        {
            @Override
            public Item getTabIconItem()
            {
                return Content.itemNitroCell;
            }

            @Override
            public<F extends ItemStack> void prepareTabSorter()
            {

            }
        };

        public static CreativeTabRSSMC attachmentTab = new CreativeTabRSSMC(Reference.MOD_ID + ":attachments.name")
        {
            @Override
            public Item getTabIconItem()
            {
                return Content.itemAttachment;
            }

            @Override
            public<F extends ItemStack> void prepareTabSorter()
            {

            }
        };

        public static CreativeTabRSSMC buildingTab = new CreativeTabRSSMC(Reference.MOD_ID + ":building.name")
        {
            @Override
            public Item getTabIconItem()
            {
                return Item.getItemFromBlock(Content.blockBarricade);
            }

            @Override
            public<F extends ItemStack> void prepareTabSorter()
            {

            }
        };

        public static CreativeTabRSSMC miscTab = new CreativeTabRSSMC(Reference.MOD_ID + ":misc.name")
        {
            @Override
            public Item getTabIconItem()
            {
                return Content.itemCrowbar;
            }

            @Override
            public<F extends ItemStack> void prepareTabSorter()
            {

            }
        };

        public static void prepareTabSorters()
        {
            gunTab.prepareTabSorter();
            ammoTab.prepareTabSorter();
            armorTab.prepareTabSorter();
            gadgetTab.prepareTabSorter();
            attachmentTab.prepareTabSorter();
            miscTab.prepareTabSorter();
        }
    }

    public static final class NET
    {
        public static SimpleNetworkWrapper RSSMC_NET_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

        public static <REQ extends IMessage, REPLY extends IMessage> void registerMessageForServerSide(Class<REQ> message, Class<? extends IMessageHandler<REQ, REPLY>> handler, int id)
        {
            RSSMC_NET_WRAPPER.registerMessage(handler, message, id, Side.SERVER);
        }

        public static <REQ extends IMessage, REPLY extends IMessage> void registerMessageForClientSide(Class<REQ> message, Class<? extends IMessageHandler<REQ, REPLY>> handler, int id)
        {
            RSSMC_NET_WRAPPER.registerMessage(handler, message, id, Side.CLIENT);
        }

        public static void sendMessageToServer(IMessage message)
        {
            RSSMC_NET_WRAPPER.sendToServer(message);
        }

        public static void sendMessageToAllClients(IMessage message)
        {
            RSSMC_NET_WRAPPER.sendToAll(message);
        }

        public static void sendMessageToClient(IMessage message, EntityPlayer player)
        {
            RSSMC_NET_WRAPPER.sendTo(message, (EntityPlayerMP) player);
        }

        public static void sendMessageToArea(IMessage message, TargetPoint target)
        {
            RSSMC_NET_WRAPPER.sendToAllAround(message, target);
        }
    }
}