package xfacthd.r6mod.client.render.ister;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.client.model.baked.BakedModelISTER;
import xfacthd.r6mod.common.items.gadgets.ItemCandela;

import java.util.*;

public class RenderCandela extends ItemStackTileEntityRenderer
{
    private static final int BLINK_INTERVAL = 10;

    private static BakedModelISTER model_0;
    private static IBakedModel model_1;
    private static IBakedModel model_2;
    private static IBakedModel model_3;

    private final Random rand = new Random();
    private final IModelData data = new ModelDataMap.Builder().build();

    @Override
    public void render(ItemStack stack, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        int time = ((ItemCandela)stack.getItem()).getCurrentTime(stack, Minecraft.getInstance().world);
        ItemCandela.State state = ItemCandela.State.fromTime(time);
        IBakedModel model = getModel(state, time);

        matrix.push();

        ItemCameraTransforms.TransformType transform = model_0.getTransformType();
        if (transform != ItemCameraTransforms.TransformType.GROUND && transform != ItemCameraTransforms.TransformType.GUI && transform != ItemCameraTransforms.TransformType.FIXED)
        {
            matrix.translate(.5, .5, .5);
            matrix.rotate(Vector3f.YP.rotationDegrees(-25));
            matrix.rotate(Vector3f.XP.rotationDegrees(-35));
            matrix.translate(-.5, -.5, -.5);

            if (transform == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND ||
                transform == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
            {
                matrix.translate(0, -.1, .1);
            }
        }

        IVertexBuilder builder = buffer.getBuffer(Atlases.getCutoutBlockType());
        for (BakedQuad quad : model.getQuads(null, null, rand, data))
        {
            builder.addQuad(matrix.getLast(), quad, 1.0F, 1.0F, 1.0F, combinedLight, combinedOverlay);
        }

        matrix.pop();
    }

    private static IBakedModel getModel(ItemCandela.State state, int time)
    {
        switch (state)
        {
            case ONE_BLINK:   return blinkOn(time) ? model_1 : model_0;
            case ONE_SOLID:   return model_1;
            case TWO_BLINK:   return blinkOn(time) ? model_2 : model_1;
            case TWO_SOLID:   return model_2;
            case THREE_BLINK: return blinkOn(time) ? model_3 : model_2;
            case THREE_SOLID: return model_3;
            default:          return model_0;
        }
    }

    private static boolean blinkOn(int time) { return  (time % BLINK_INTERVAL) < (BLINK_INTERVAL / 2); }

    public static void registerModels()
    {
        ModelLoader.addSpecialModel(new ResourceLocation(R6Mod.MODID, "item/item_candela_1"));
        ModelLoader.addSpecialModel(new ResourceLocation(R6Mod.MODID, "item/item_candela_2"));
        ModelLoader.addSpecialModel(new ResourceLocation(R6Mod.MODID, "item/item_candela_3"));
    }

    public static void loadModels(Map<ResourceLocation, IBakedModel> registry)
    {
        ResourceLocation baseLoc = new ModelResourceLocation(new ResourceLocation(R6Mod.MODID, "item_candela"), "inventory");
        IBakedModel model0 = registry.get(baseLoc);
        model_0 = new BakedModelISTER(model0);
        registry.put(baseLoc, model_0);

        model_1 = registry.get(new ResourceLocation(R6Mod.MODID, "item/item_candela_1"));
        model_2 = registry.get(new ResourceLocation(R6Mod.MODID, "item/item_candela_2"));
        model_3 = registry.get(new ResourceLocation(R6Mod.MODID, "item/item_candela_3"));
    }
}