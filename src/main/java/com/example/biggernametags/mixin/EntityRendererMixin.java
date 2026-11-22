package com.example.biggernametags.mixin;

import com.example.biggernametags.BiggerNametags;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Scales the name tag around its own anchor point, so it grows in place
 * instead of drifting upward. Only the size changes: render distance,
 * sneaking behaviour and wall visibility all stay vanilla, so it can't
 * be used as ESP.
 */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T, S extends EntityRenderState> {

	@Inject(method = "renderLabelIfPresent", at = @At("HEAD"))
	private void biggernametags$pushScale(S state, Text text, MatrixStack matrices,
			OrderedRenderCommandQueue queue, CameraRenderState camera, CallbackInfo ci) {
		matrices.push();
		if (state instanceof PlayerEntityRenderState && state.nameLabelPos != null) {
			Vec3d p = state.nameLabelPos;
			float s = BiggerNametags.scale();
			matrices.translate(p.x, p.y, p.z);
			matrices.scale(s, s, s);
			matrices.translate(-p.x, -p.y, -p.z);
		}
	}

	@Inject(method = "renderLabelIfPresent", at = @At("RETURN"))
	private void biggernametags$popScale(S state, Text text, MatrixStack matrices,
			OrderedRenderCommandQueue queue, CameraRenderState camera, CallbackInfo ci) {
		matrices.pop();
	}

	/** Recolors the name text. Size/color only: nothing about visibility changes. */
	@ModifyVariable(method = "renderLabelIfPresent", at = @At("HEAD"), argsOnly = true)
	private Text biggernametags$recolor(Text text) {
		int c = BiggerNametags.color();
		if (c < 0 || text == null) return text;
		return text.copy().setStyle(text.getStyle().withColor(c));
	}
}
