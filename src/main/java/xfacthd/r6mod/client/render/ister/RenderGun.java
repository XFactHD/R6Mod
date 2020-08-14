package xfacthd.r6mod.client.render.ister;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.api.capability.ICapabilityGun;
import xfacthd.r6mod.client.model.baked.BakedModelISTER;
import xfacthd.r6mod.common.capability.CapabilityGun;
import xfacthd.r6mod.common.data.gun_data.ReloadState;
import xfacthd.r6mod.common.data.itemsubtypes.*;
import xfacthd.r6mod.common.items.gun.ItemGun;

import java.util.*;

public class RenderGun extends ItemStackTileEntityRenderer
{
    private static final Transform DEFAULT_TRANSFORM = new Transform(Quaternion.ONE, new Vector3f(1, 1, 1), new Vector3d(0, 0, 0));
    private static final Vector3f SCALE_ONE = new Vector3f(1.0F, 1.0F, 1.0F);
    private static final Vector3d TRANSLATE_ZERO = new Vector3d(0.0, 0.0, 0.0);

    private static final Map<EnumGun, BakedModelISTER> gunModels = new EnumMap<>(EnumGun.class);
    private static final Map<EnumMagazine, IBakedModel> magModels = new EnumMap<>(EnumMagazine.class);
    private static final Map<EnumAttachment, IBakedModel> attachModels = new EnumMap<>(EnumAttachment.class);

    private static final Map<EnumGun, Transform> magazineTransforms = new EnumMap<>(EnumGun.class);
    private static final Map<EnumGun, Map<EnumAttachment, Transform>> attachTransforms = new EnumMap<>(EnumGun.class);

    private EnumGun gun;
    private final Random rand = new Random();
    private final IModelData data = new ModelDataMap.Builder().build();

    public RenderGun() { }

    @Override
    public void render(ItemStack stack, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        if (gun == null) { gun = ((ItemGun)stack.getItem()).getGun(); }
        BakedModelISTER gunModel = gunModels.get(gun);

        IVertexBuilder builder = buffer.getBuffer(Atlases.getCutoutBlockType());

        ICapabilityGun cap = CapabilityGun.getFrom(stack);
        boolean loaded = cap.isLoaded();
        ReloadState reloadState = cap.getReloadState();
        List<EnumAttachment> attachments = cap.getAttachments();

        if (gunModel != null)
        {
            ItemCameraTransforms.TransformType type = gunModel.getTransformType();

            //noinspection ConstantConditions
            long gameTime = Minecraft.getInstance().world.getGameTime();
            float partialTicks = Minecraft.getInstance().getRenderPartialTicks();

            //Interpolate aiming
            float aimState = cap.getAimState(gameTime, partialTicks);
            if (type == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND)
            {
                double xOff = MathHelper.lerp(aimState, 0D, .8357);
                double yOff = MathHelper.lerp(aimState, 0D, .23155);
                double zOff = MathHelper.lerp(aimState, 0D, -.8);
                matrix.translate(xOff, yOff, zOff);
            }
            else if (type == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
            {
                //TODO: change arm yaw and pitch (wait for RenderLivingEvent.RenderModel) and correct rotation
                matrix.rotate(Vector3f.YP.rotationDegrees(0));
            }

            //Add gun model quads
            for (BakedQuad quad : gunModel.getQuads(null, null, rand, data))
            {
                builder.addQuad(matrix.getLast(), quad, 1.0F, 1.0F, 1.0F, combinedLight, combinedOverlay);
            }
        }

        if (gun.hasMag() && (loaded || reloadState != ReloadState.NONE))
        {
            IBakedModel model = magModels.get(gun.getMagazine());
            Transform transform = magazineTransforms.getOrDefault(gun, DEFAULT_TRANSFORM);

            //noinspection ConstantConditions
            long gameTime = Minecraft.getInstance().world.getGameTime();
            float progress = cap.getReloadStateProgress(gameTime);

            double offY = 0;
            if (reloadState == ReloadState.MAG_OUT)
            {
                offY = -.75D * progress;
            }
            else if (reloadState == ReloadState.MAG_IN)
            {
                offY = -.75D * (1F - progress);
            }

            matrix.push();
            transform.applyToMatrix(matrix);
            matrix.translate(0, offY, 0);
            for (BakedQuad quad : model.getQuads(null, null, rand, data))
            {
                builder.addQuad(matrix.getLast(), quad, 1.0F, 1.0F, 1.0F, combinedLight, combinedOverlay);
            }
            matrix.pop();
        }
        else if (!gun.hasMag())
        {
            //TODO: implement with special casing for bosg and revolvers
        }

        Map<EnumAttachment, Transform> transforms = attachTransforms.get(gun);
        for (EnumAttachment attach : attachments)
        {
            IBakedModel model = attachModels.get(attach);
            Transform trans = (transforms == null) ? DEFAULT_TRANSFORM : transforms.getOrDefault(attach, DEFAULT_TRANSFORM);

            matrix.push();
            trans.applyToMatrix(matrix);
            if (trans.equals(DEFAULT_TRANSFORM))
            {
                //matrix.rotate(Vector3f.YP.rotationDegrees(0));
                //matrix.scale(1.0F, 1.0F, 1.0F);
                //matrix.translate(0.0, 0.0, 0.0);
            }
            for (BakedQuad quad : model.getQuads(null, null, rand, data))
            {
                builder.addQuad(matrix.getLast(), quad, 1.0F, 1.0F, 1.0F, combinedLight, combinedOverlay);
            }
            matrix.pop();
        }
    }

    public static void loadModels(Map<ResourceLocation, IBakedModel> modelMap)
    {
        gunModels.clear();
        magModels.clear();
        attachModels.clear();

        for (EnumGun gun : EnumGun.values())
        {
            ResourceLocation loc = new ModelResourceLocation(new ResourceLocation(R6Mod.MODID, gun.toItemName()), "inventory");
            IBakedModel original = modelMap.get(loc);
            BakedModelISTER model = new BakedModelISTER(original);
            gunModels.put(gun, model);
            modelMap.put(loc, model);
        }

        for (EnumMagazine mag : EnumMagazine.values())
        {
            ResourceLocation loc = new ModelResourceLocation(new ResourceLocation(R6Mod.MODID, mag.toItemName()), "inventory");
            IBakedModel model = modelMap.get(loc);
            magModels.put(mag, model);
        }

        for (EnumAttachment attach : EnumAttachment.values())
        {
            ResourceLocation loc = new ModelResourceLocation(new ResourceLocation(R6Mod.MODID, attach.toItemName()), "inventory");
            IBakedModel model = modelMap.get(loc);
            attachModels.put(attach, model);
        }
    }

    public static void parseAttachmentTransform(EnumGun gun, EnumAttachment attachment, JsonObject json)
    {
        Quaternion rotate = Quaternion.ONE;
        Vector3f scale = SCALE_ONE;
        Vector3d translate = TRANSLATE_ZERO;

        if (json.has("rotate"))
        {
            JsonArray rotArr = json.getAsJsonArray("rotate");
            float x = rotArr.get(0).getAsFloat();
            float y = rotArr.get(1).getAsFloat();
            float z = rotArr.get(2).getAsFloat();
            rotate = new Quaternion(x, y, z, true);
        }
        if (json.has("scale"))
        {
            JsonArray scaleArr = json.getAsJsonArray("scale");
            float x = scaleArr.get(0).getAsFloat();
            float y = scaleArr.get(1).getAsFloat();
            float z = scaleArr.get(2).getAsFloat();
            scale = new Vector3f(x, y, z);
        }
        if (json.has("translate"))
        {
            JsonArray transArr = json.getAsJsonArray("translate");
            double x = transArr.get(0).getAsDouble();
            double y = transArr.get(1).getAsDouble();
            double z = transArr.get(2).getAsDouble();
            translate = new Vector3d(x, y, z);
        }
        Transform transform = new Transform(rotate, scale, translate);

        if (!attachTransforms.containsKey(gun)) { attachTransforms.put(gun, new EnumMap<>(EnumAttachment.class)); }
        attachTransforms.get(gun).put(attachment, transform);
    }

    public static void parseMagazineTransform(EnumGun gun, JsonObject json)
    {
        Quaternion rotate = Quaternion.ONE;
        Vector3f scale = SCALE_ONE;
        Vector3d translate = TRANSLATE_ZERO;

        if (json.has("rotate"))
        {
            JsonArray rotArr = json.getAsJsonArray("rotate");
            float x = rotArr.get(0).getAsFloat();
            float y = rotArr.get(1).getAsFloat();
            float z = rotArr.get(2).getAsFloat();
            rotate = new Quaternion(x, y, z, true);
        }
        if (json.has("scale"))
        {
            JsonArray scaleArr = json.getAsJsonArray("scale");
            float x = scaleArr.get(0).getAsFloat();
            float y = scaleArr.get(1).getAsFloat();
            float z = scaleArr.get(2).getAsFloat();
            scale = new Vector3f(x, y, z);
        }
        if (json.has("translate"))
        {
            JsonArray transArr = json.getAsJsonArray("translate");
            double x = transArr.get(0).getAsDouble();
            double y = transArr.get(1).getAsDouble();
            double z = transArr.get(2).getAsDouble();
            translate = new Vector3d(x, y, z);
        }
        Transform transform = new Transform(rotate, scale, translate);
        magazineTransforms.put(gun, transform);
    }

    private static final class Transform
    {
        private final Quaternion rotate;
        private final Vector3f scale;
        private final Vector3d translate;

        public Transform(Quaternion rotate, Vector3f scale, Vector3d translate)
        {
            this.rotate = rotate;
            this.scale = scale;
            this.translate = translate;
        }

        public void applyToMatrix(MatrixStack stack)
        {
            stack.rotate(rotate);
            stack.scale(scale.getX(), scale.getY(), scale.getZ());
            stack.translate(translate.x, translate.y, translate.z);
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }
            Transform transform = (Transform) o;
            return rotate.equals(transform.rotate) &&
                    scale.equals(transform.scale) &&
                    translate.x == transform.translate.x && //Vector3d doesn't override equals()
                    translate.y == transform.translate.y &&
                    translate.z == transform.translate.z;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(rotate, scale, translate);
        }
    }
}