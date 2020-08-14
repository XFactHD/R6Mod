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

package XFactHD.rssmc.client.keybind;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public final class KeyBindings
{
    public static final KeyBinding activateGadget = new KeyBinding("keybind.rssmc:activate_gadget.name", Keyboard.KEY_G, "key.categories.rssmc");
    public static final KeyBinding setMarker = new KeyBinding("keybind.rssmc:set_marker.name", Keyboard.KEY_Y, "key.categories.rssmc");
    public static final KeyBinding reload = new KeyBinding("keybind.rssmc:reload_gun.name", Keyboard.KEY_R, "key.categories.rssmc");
    public static final KeyBinding sneakUpright = new KeyBinding("keybind.rssmc:sneak_upright.name", Keyboard.KEY_LMENU, "key.categories.rssmc");
    public static final KeyBinding changeFiremode = new KeyBinding("keybind.rssmc:change_firemode.name", Keyboard.KEY_B, "key.categories.rssmc");

    public static void register()
    {
        ClientRegistry.registerKeyBinding(activateGadget);
        ClientRegistry.registerKeyBinding(setMarker);
        ClientRegistry.registerKeyBinding(reload);
        ClientRegistry.registerKeyBinding(sneakUpright);
        ClientRegistry.registerKeyBinding(changeFiremode);
    }
}