package com.mrbysco.flatterentities.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrbysco.flatterentities.Flattener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EnderCrystalRenderer;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EnderCrystalRenderer.class)
public class EnderCrystalRendererMixin<T extends EnderCrystalEntity> {

	@Inject(method = "Lnet/minecraft/client/renderer/entity/EnderCrystalRenderer;render(Lnet/minecraft/entity/item/EnderCrystalEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
			locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/matrix/MatrixStack;translate(DDD)V",
			shift = Shift.AFTER))
	public void flatterRender(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, CallbackInfo ci) {
		final float f = MathHelper.lerp(partialTicks, entityIn.prevRotationYaw, entityIn.rotationYaw);
		double x = entityIn.getPosX();
		double z = entityIn.getPosZ();

		final PlayerEntity player = Minecraft.getInstance().player;
		if(player != null) {
			x -= player.getPosX();
			z -= player.getPosZ();
		}

		Flattener.prepareFlatRendering(f, x, z, matrixStackIn, entityIn);
	}
}
