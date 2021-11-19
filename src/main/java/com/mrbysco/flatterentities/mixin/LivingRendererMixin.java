package com.mrbysco.flatterentities.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrbysco.flatterentities.Flattener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingRenderer.class)
public class LivingRendererMixin<T extends LivingEntity> {

	@Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
			locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/matrix/MatrixStack;translate(DDD)V",
			shift = Shift.AFTER,
			ordinal = 1))
	public void flatterRender(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, CallbackInfo cir) {
		float f = MathHelper.rotLerp(partialTicks, entityIn.yBodyRotO, entityIn.yBodyRot);

		final boolean shouldSit = entityIn.isPassenger() && (entityIn.getVehicle() != null && entityIn.getVehicle().shouldRiderSit());
		if (shouldSit && entityIn.getVehicle() instanceof LivingEntity) {
			float f1 = MathHelper.rotLerp(partialTicks, entityIn.yHeadRotO, entityIn.yHeadRot);
			float f2 = f1 - f;
			float f3 = MathHelper.wrapDegrees(f2);
			if (f3 < -85.0F) {
				f3 = -85.0F;
			}

			if (f3 >= 85.0F) {
				f3 = 85.0F;
			}

			f = f1 - f3;
			if (f3 * f3 > 2500.0F) {
				f += f3 * 0.2F;
			}
		}
		double x = entityIn.getX();
		double z = entityIn.getZ();

		final PlayerEntity player = Minecraft.getInstance().player;
		if(player != null) {
			x -= player.getX();
			z -= player.getZ();
		}

		Flattener.prepareFlatRendering(f, x, z, matrixStackIn, entityIn);
	}
}
