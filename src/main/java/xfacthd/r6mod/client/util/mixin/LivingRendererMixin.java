package xfacthd.r6mod.client.util.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.r6mod.client.event.GunEventHandler;

@Mixin(LivingRenderer.class)
public abstract class LivingRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements IEntityRenderer<T, M>
{
    @Shadow
    protected M entityModel;

    protected LivingRendererMixin(EntityRendererManager renderManager) { super(renderManager); }

    //LivingRenderer Line 112
    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingRenderer;isVisible(Lnet/minecraft/entity/LivingEntity;)Z"), remap = false)
    private void postSetRotation(T entity, float entityYaw, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int packedLightIn, CallbackInfo info)
    {
        if (entity instanceof PlayerEntity && entityModel instanceof PlayerModel)
        {
            PlayerEntity player = (PlayerEntity)entity;
            //noinspection unchecked
            PlayerModel<PlayerEntity> playerModel = (PlayerModel<PlayerEntity>)entityModel;
            GunEventHandler.handleModelRotations(player, playerModel);
        }
    }
}