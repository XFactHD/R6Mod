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

package XFactHD.rssmc.common.utils.helper;

import XFactHD.rssmc.common.utils.Reference;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Config(modid = Reference.MOD_ID)
public class ConfigHandler
{
    @Config.RangeInt(min = 16, max = 512)
    @Config.Comment("Sets the max range guns check for a hit. Decided by the server.")
    @Config.LangKey("config.rssmc:max_shoot_range.name")
    public static int maxShootRange = 256; //Default 256

    @Config.RangeInt(min = -1, max = 100)
    @Config.Comment("Sets the max range for placing a yellow marker. Set to -1 to disable. Decided by the server.")
    @Config.LangKey("config.rssmc:max_mark_range.name")
    public static int maxMarkRange = 100; //Default 100

    @Config.Comment("If set to true, you need to hold the aim button to aim down sights, else you need to click to go into and out of the sights.")
    @Config.LangKey("config.rssmc:hold_to_aim.name")
    public static boolean holdToAim = false; //Default true

    @Config.Comment("If set to true, certain blocks, ui elements and behaviours for playing Siege gamemodes are enabled. Decided by the server.")
    @Config.LangKey("config.rssmc:battle_mode.name")
    public static boolean battleMode = false; //Default false

    @Config.Comment("Enables a debug renderer to display secure and defuse range for biohazard container and bomb. Only works in creative mode.")
    @Config.LangKey("config.rssmc:debug_render_obj.name")
    public static boolean debugRenderObjective = false; //Default false

    @Config.Comment("Enables a debug renderer for gun shots. Only works in creative mode.")
    @Config.LangKey("config.rssmc:debug_render_bullet.name")
    public static boolean debugRenderBullet = false; //Default false

    @Config.Comment("Enables a debug renderer for grenade paths. Only works in creative mode.")
    @Config.LangKey("config.rssmc:debug_render_grenade_path.name")
    public static boolean debugRenderGrenadePath = true; //Default false

    @Config.Comment("Enables a custom health bar that more accurately shows your current health.")
    @Config.LangKey("config.rssmc:custom_health_bar.name")
    public static boolean customHealthBar = true; //Default true

    @Config.Comment("Adds a compass to your hud. Decided by the server.")
    @Config.LangKey("config.rssmc:show_compass.name")
    public static boolean showCompass = true; //Default true

    @Config.Comment("Enables information on the points you get. Only available in battle mode.")
    @Config.LangKey("config.rssmc:show_point_info.name")
    public static boolean showPointInfo = true; //Default true

    @Config.Comment("Enables a debug renderer for kapkan traps. Only works in creative mode.")
    @Config.LangKey("config.rssmc:debug_render_kapkan.name")
    public static boolean debugRenderKapkan = false; //Default false

    @Config.Comment("If set to true damage and health values will be displayed based on MC's system of max 20hp, else they are based on a system of max 100 hp.")
    @Config.LangKey("config.rssmc:disp_hp_base_twenty.name")
    public static boolean displayHealthBaseTwenty = true; //Default true

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(Reference.MOD_ID))
        {
            ConfigManager.load(Reference.MOD_ID, Config.Type.INSTANCE);
        }
    }
}