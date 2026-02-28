package com.mrbysco.flatterentities.mixin.gecko;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.flatterentities.Flattener;
import com.mrbysco.flatterentities.FlatterInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

@Pseudo
@Mixin(software.bernie.geckolib.renderer.GeoEntityRenderer.class)
public abstract class GeoEntityRendererMixin<T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> {

	@Inject(method = "applyRotations(Lsoftware/bernie/geckolib/renderer/base/RenderPassInfo;Lcom/mojang/blaze3d/vertex/PoseStack;F)V",
			remap = false,
			locals = LocalCapture.NO_CAPTURE, at = @At(
			value = "INVOKE",
			target = "Lsoftware/bernie/geckolib/renderer/base/RenderPassInfo;renderState()Lsoftware/bernie/geckolib/renderer/base/GeoRenderState;",
			shift = Shift.AFTER,
			ordinal = 0))
	public void flatterActuallyRender(RenderPassInfo<R> renderPassInfo, PoseStack poseStack, float nativeScale, CallbackInfo ci) {
		R renderState = renderPassInfo.renderState();
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
