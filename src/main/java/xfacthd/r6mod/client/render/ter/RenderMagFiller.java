package xfacthd.r6mod.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.data.itemsubtypes.EnumMagazine;
import xfacthd.r6mod.common.items.gun.ItemMagazine;
import xfacthd.r6mod.common.tileentities.misc.TileEntityMagFiller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RenderMagFiller extends TileEntityRenderer<TileEntityMagFiller>
{
    private static final HashMap<EnumMagazine, IBakedModel> magModels = new HashMap<>();

    private final Random rand = new Random();
    private final IModelData data = new ModelDataMap.Builder().build();

    public RenderMagFiller(TileEntityRendererDispatcher dispatch)
    {
        super(dispatch);
    }

    @Override
    public void render(TileEntityMagFiller tile, float partialTicks, MatrixStack mstack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        LazyOptional<IItemHandler> itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        if (!itemHandler.isPresent()) { return; }
        //noinspection ConstantConditions
        IItemHandler handler = itemHandler.orElse(null); //Can't be null because of the isPresent() check earlier

        ItemStack magStack = handler.getStackInSlot(TileEntityMagFiller.MAG_SLOT);
        if (!magStack.isEmpty() && magStack.getItem() instanceof ItemMagazine)
        {
            EnumMagazine mag = ((ItemMagazine)magStack.getItem()).getMagazine();
            IBakedModel magModel = magModels.get(mag);
            IVertexBuilder builder = buffer.getBuffer(RenderType.getCutoutMipped());

            mstack.push();
            mstack.rotate(Vector3f.YP.rotationDegrees(-90 * (tile.getFacing().getHorizontalIndex() - 1)));

            if (mag.rotateInMagFiller())
            {
                double offX = (tile.getFacing() == Direction.SOUTH || tile.getFacing() == Direction.WEST) ? -1F : 0F;
                double offZ = (tile.getFacing() == Direction.NORTH || tile.getFacing() == Direction.WEST) ? 1F : 0F;

                mstack.rotate(Vector3f.YP.rotationDegrees(90.0F));
                mstack.translate(0.15 + offX, 0.025, -1.6 + offZ);
                mstack.scale(0.7F, 0.7F, 2.0F);
            }
            else
            {
                double offX = (tile.getFacing() == Direction.NORTH || tile.getFacing() == Direction.WEST) ? 1F : 0F;
                double offZ = (tile.getFacing().getHorizontalIndex() < 2) ? 1F : 0F;

                mstack.translate(-0.95 + offX, 0.025, -0.85 + offZ);
                mstack.scale(0.7F, 0.7F, 0.7F);
            }

            for (BakedQuad quad : magModel.getQuads(null, null, rand, data))
            {
                builder.addQuad(mstack.getLast(), quad, 1.0F, 1.0F, 1.0F, combinedLight, combinedOverlay);
            }
            mstack.pop();
        }
    }

    public static void loadModels(Map<ResourceLocation, IBakedModel> modelMap)
    {
        for (EnumMagazine mag : EnumMagazine.values())
        {
            ResourceLocation loc = new ModelResourceLocation(new ResourceLocation(R6Mod.MODID, mag.toItemName()), "inventory");
            IBakedModel model = modelMap.get(loc);
            magModels.put(mag, model);
        }
    }
}