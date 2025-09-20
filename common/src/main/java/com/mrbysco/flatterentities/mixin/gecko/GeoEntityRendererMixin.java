package com.mrbysco.flatterentities.mixin.gecko;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrbysco.flatterentities.Flattener;
import com.mrbysco.flatterentities.FlatterInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import software.bernie.geckolib.cache.object.BakedGeoModel;

@Pseudo
@Mixin(software.bernie.geckolib.renderer.GeoEntityRenderer.class)
public abstract class GeoEntityRendererMixin<R extends EntityRenderState> {

	@Inject(method = "actuallyRender(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;Lnet/minecraft/client/renderer/RenderType;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZIII)V",
			remap = false,
			locals = LocalCapture.NO_CAPTURE, at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",
			shift = Shift.AFTER,
			ordinal = 0))
	public void flatterActuallyRender(R renderState, PoseStack poseStack, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor, CallbackInfo ci) {
		if (renderState instanceof FlatterInfo flatterLerp) {
			final float yawLerp = flatterLerp.flatterentities$getYawLerp();
			double x = renderState.x;
			double z = renderState.y;

			final Player player = Minecraft.getInstance().player;
			if (player != null) {
				x -= player.getX();
				z -= player.getZ();
			}

			Flattener.prepareFlatRendering(yawLerp, x, z, poseStack, renderState);

		}
	}
}
