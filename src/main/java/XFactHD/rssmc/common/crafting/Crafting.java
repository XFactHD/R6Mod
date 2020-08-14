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

package XFactHD.rssmc.common.crafting;

import XFactHD.rssmc.common.crafting.recipes.RecipeArmorBagRefill;
import XFactHD.rssmc.common.crafting.recipes.RecipeBlockFloorPanel;
import XFactHD.rssmc.common.crafting.recipes.RecipeGunCrafting;
import XFactHD.rssmc.common.crafting.recipes.RecipeMagRefill;
import XFactHD.rssmc.common.data.EnumGun;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;

import java.util.HashMap;
import java.util.Map;

public class Crafting
{
    private static Map<EnumGun, RecipeGunCrafting> gunRecipes = new HashMap<>();

    public static void initItemStacks()
    {

    }

    public static void initVanillaCrafting()
    {
        RecipeSorter.register("recipeArmorRefill", RecipeArmorBagRefill.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        RecipeSorter.register("recipeFloorPanel", RecipeBlockFloorPanel.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        RecipeSorter.register("recipeMagRefill", RecipeMagRefill.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        GameRegistry.addRecipe(new RecipeArmorBagRefill());
        GameRegistry.addRecipe(new RecipeBlockFloorPanel());
        GameRegistry.addRecipe(new RecipeMagRefill());
    }

    public static void initGunCrafting()
    {
        //TODO: add recipes
        gunRecipes.put(EnumGun.C8_SFW, new RecipeGunCrafting());
    }

    public static void initAmmoCrafting()
    {

    }

    public static RecipeGunCrafting getGunRecipe(EnumGun gun)
    {
        return gunRecipes.getOrDefault(gun, RecipeGunCrafting.EMPTY_RECIPE);
    }
}