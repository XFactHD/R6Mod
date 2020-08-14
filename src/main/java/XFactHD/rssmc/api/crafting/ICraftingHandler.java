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

package XFactHD.rssmc.api.crafting;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public interface ICraftingHandler<T extends ICustomRecipe>
{
    void addRecipe(T recipe);

    void removeRecipe(T recipe);

    @Nullable
    ArrayList<T> getRecipeForInput(@Nonnull ItemStack input);

    @Nullable
    ArrayList<T> getRecipeForOutput(@Nonnull ItemStack output);

    ArrayList<T> getAllRecipes();
}