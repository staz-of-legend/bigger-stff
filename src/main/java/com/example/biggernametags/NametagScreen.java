package com.example.biggernametags;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class NametagScreen extends Screen {
	private final Screen parent;
	private ScaleSlider slider;

	public NametagScreen(Screen parent) {
		super(Text.literal("Bigger Nametags"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		int w = 200, x = width / 2 - w / 2, y = height / 4 + 10;

		slider = addDrawableChild(new ScaleSlider(x, y, w, 20));

		addDrawableChild(ButtonWidget.builder(colorLabel(), b -> {
			BiggerNametags.setColorIndex(BiggerNametags.colorIndex() + 1);
			BiggerNametags.save();
			b.setMessage(colorLabel());
		}).dimensions(x, y + 28, w, 20).build());

		addDrawableChild(ButtonWidget.builder(Text.literal("Reset to default (1.5x, no color)"), b -> {
			BiggerNametags.setScale(1.5f);
			BiggerNametags.setColorIndex(0);
			BiggerNametags.save();
			clearAndInit();
		}).dimensions(x, y + 56, w, 20).build());

		addDrawableChild(ButtonWidget.builder(Text.literal("Vanilla size (1.0x)"), b -> {
			BiggerNametags.setScale(1.0f);
			BiggerNametags.save();
			clearAndInit();
		}).dimensions(x, y + 82, w, 20).build());

		addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
				.dimensions(x, y + 118, w, 20).build());
	}

	private Text colorLabel() {
		int i = BiggerNametags.colorIndex();
		Text name = Text.literal(BiggerNametags.COLOR_NAMES[i]);
		if (BiggerNametags.COLORS[i] >= 0) {
			final int rgb = BiggerNametags.COLORS[i];
			name = Text.literal(BiggerNametags.COLOR_NAMES[i]).styled(st -> st.withColor(rgb));
		}
		return Text.literal("Name color (click to change): ").append(name);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 4 - 14, 0xFFFFFFFF);
	}

	@Override
	public void close() {
		BiggerNametags.save();
		client.setScreen(parent);
	}

	/** Slider from 1.0x to the hard cap, in 0.05 steps. */
	private static class ScaleSlider extends SliderWidget {
		ScaleSlider(int x, int y, int w, int h) {
			super(x, y, w, h, Text.empty(),
					(BiggerNametags.scale() - BiggerNametags.MIN_SCALE) / (BiggerNametags.MAX_SCALE - BiggerNametags.MIN_SCALE));
			updateMessage();
		}

		private float current() {
			float raw = BiggerNametags.MIN_SCALE + (float) value * (BiggerNametags.MAX_SCALE - BiggerNametags.MIN_SCALE);
			return Math.round(raw * 20f) / 20f;
		}

		@Override
		protected void updateMessage() {
			setMessage(Text.literal("Nametag size: " + String.format("%.2f", current()) + "x"));
		}

		@Override
		protected void applyValue() {
			BiggerNametags.setScale(current());
			BiggerNametags.save();
		}
	}
}
