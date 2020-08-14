/*  Copyright (C) <2016>  <XFactHD, DrakoAlcarus>

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

package XFactHD.rssmc.client.data;

import net.minecraft.util.text.TextComponentTranslation;

import java.util.Locale;
import java.util.Random;

public enum EnumCallout
{
    MUTE_REINFORCE(0),
    MUTE_BOARD_DOOR(0),
    MUTE_PLACE_JAMMER(0),
    MUTE_PLACE_NITRO(0),
    MUTE_ACTIVATE_NITRO(0),
    MUTE_PLACE_SHIELD(0),
    MUTE_RELOAD(0),
    MUTE_REVIVE(0),
    SMOKE_REINFORCE(0),
    SMOKE_BOARD_DOOR(0),
    SMOKE_THROW_GAS(0),
    SMOKE_ACTIVATE_GAS(0),
    SMOKE_PLACE_BARBED_WIRE(0),
    SMOKE_RELOAGIND(0),
    SMOKE_REVIVE(0),
    PULSE_REINFORCE(0),
    PULSE_BOARD_DOOR(0),
    PULSE_ACTIVATE_SENSOR(0),
    PULSE_SENSOR_INACTIVE(0),
    PULSE_PLACE_NITRO(0),
    PULSE_ACTIVATE_NITRO(0),
    PULSE_RELOAD(0),
    PULSE_REVIVE(0),
    CASTLE_REINFORCE(0),
    CASTLE_BOARD_DOOR(0),
    CASTLE_PLACE_SHIELD(0),
    CASTLE_RELOAD(0),
    CASTLE_REVIVE(0),
    ROOK_REINFORCE(0),
    ROOK_BOARD_DOOR(0),
    ROOK_PLACE_ARMOR(0),
    ROOK_PLACE_SHIELD(0),
    ROOK_RELOAD(0),
    ROOK_REVIVE(0),
    DOC_REINFORCE(0),
    DOC_BOARD_DOOR(0),
    DOC_PLACE_SHIELD(0),
    DOC_PLACE_BARBED_WIRE(0),
    DOC_RELOAD(0),
    DOC_REVIVE(0),
    TACHANKA_REINFORCE(0),
    TACHANKA_BOARD_DOOR(0),
    TACHANKA_PLACE_LMG(0),
    TACHANKA_REMOVE_LMG(0),
    TACHANKA_PLACE_SHIELD(0),
    TACHANKA_PLACE_BARBED_WIRE(0),
    TACHANKA_RELOAD(0),
    TACHANKA_REVIVE(0),
    KAPKAN_REINFORCE(0),
    KAPKAN_BOARD_DOOR(0),
    KAPKAN_PLACE_TRAP(0),
    KAPKAN_PLACE_NITRO(0),
    KAPKAN_ACTIVATE_NITRO(0),
    KAPKAN_PLACE_SHIELD(0),
    KAPKAN_PLACE_BARBED_WIRE(0),
    KAPKAN_RELOAD(0),
    KAPKAN_REVIVE(0),
    JÄGER_REINFORCE(0),
    JÄGER_BOARD_DOOR(0),
    JÄGER_PLACE_ADS(0),
    JÄGER_PLACE_SHIELD(0),
    JÄGER_PLACE_BARBED_WIRE(0),
    JÄGER_RELOAD(0),
    JÄGER_REVIVE(0),
    BANDIT_REINFORCE(0),
    BANDIT_BOARD_DOOR(0),
    BANDIT_PLACE_BATTERY(0),
    BANDIT_PLACE_NITRO(0),
    BANDIT_ACTIVATE_NITRO(0),
    BANDIT_PLACE_BARBED_WIRE(0),
    FROST_REINFORCE(0),
    FROST_BOARD_DOOR(0),
    FROST_PLACE_TRAP(0),
    FROST_PLACE_SHIELD(0),
    FROST_PLACE_BARBED_WIRE(0),
    FROST_RELOAD(0),
    FROST_REVIVE(0),
    VALKYRIE_REINFORCE(0),
    VALKYRIE_BOARD_DOOR(0),
    VALKYRIE_PLACE_CAMERA(0),
    VALKYRIE_PLACE_NITRO(0),
    VALKYRIE_ACTIVATE_NITRO(0),
    VALKYRIE_PLACE_SHIELD(0),
    VALKYRIE_RELOAD(0),
    VALKYRIE_REVIVE(0),
    CAVEIRA_REINFORCE(0),
    CAVEIRA_BOARD_DOOR(0),
    CAVEIRA_INTEROGATE_START(0),
    CAVEIRA_INTEROGATE_FINISH(0),
    CAVEIRA_PLACE_BARBED_WIRE(0),
    CAVEIRA_RELOAD(0),
    CAVEIRA_REVIVE(0),
    ECHO_REINFORCE(0),
    ECHO_BOARD_DOOR(0),
    ECHO_USE_DRONE(0),
    ECHO_PLACE_SHIELD(0),
    ECHO_PLACE_BARBED_WIRE(0),
    ECHO_RELOAD(0),
    ECHO_REVIVE(0),
    MIRA_REINFORCE(0),
    MIRA_BOARD_DOOR(0),
    MIRA_PLACE_MIRROR(0),
    MIRA_PLACE_NITRO(0),
    MIRA_ACTIVATE_NITRO(0),
    MIRA_PLACE_SHIELD(0),
    MIRA_RELOAD(0),
    MIRA_REVIVE(0),

    SLEDGE(0),
    THATCHER(0),
    THERMITE(0),
    ASH(0),
    MONTAGNE(0),
    TWITCH(0),
    FUZE(0),
    GLAZ(0),
    BLITZ(0),
    IQ(0),
    BUCK(0),
    BLACKBEARD(0),
    CAPITAO(0),
    HIBANA(0),
    JACKAL(0);

    private static final Random rand = new Random();

    private int count;

    EnumCallout(int count)
    {
        this.count = count;
    }

    public TextComponentTranslation getRandomCallout()
    {
        int index = rand.nextInt(count) + 1;
        return new TextComponentTranslation("callout.rssmc:" + toString().toLowerCase(Locale.ENGLISH) + "_" + index + ".name");
    }
}