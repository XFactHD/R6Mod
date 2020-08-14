package XFactHD.rssmc.api.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IShockable
{
    boolean canBeShocked(IBlockState state);

    boolean shock(World world, BlockPos pos, IBlockState state, EntityPlayer player, float hitX, float hitY, float hitZ);
}
