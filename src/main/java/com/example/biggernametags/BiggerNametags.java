package com.example.biggernametags;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class BiggerNametags implements ClientModInitializer {
	/** Hard cap so it never gets silly. 1.0 = vanilla. */
	public static final float MIN_SCALE = 1.0f;
	public static final float MAX_SCALE = 2.0f;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("biggernametags.json");

	private static class Data {
		float scale = 1.5f;
		/** Index into COLORS. 0 = vanilla (no recolor). */
		int colorIndex = 0;
	}

	public static final String[] COLOR_NAMES = {
		"Default", "Red", "Orange", "Yellow", "Green", "Aqua", "Blue", "Purple", "Pink", "Black"
	};
	public static final int[] COLORS = {
		-1, 0xFF5555, 0xFFAA00, 0xFFFF55, 0x55FF55, 0x55FFFF, 0x5555FF, 0xAA55FF, 0xFF77CC, 0x000000
	};
	private static Data data = new Data();

	public static float scale() {
		return Math.max(MIN_SCALE, Math.min(data.scale, MAX_SCALE));
	}

	public static int colorIndex() {
		return Math.max(0, Math.min(data.colorIndex, COLORS.length - 1));
	}

	public static void setColorIndex(int i) {
		data.colorIndex = Math.floorMod(i, COLORS.length);
	}

	/** RGB color for name tags, or -1 for vanilla. */
	public static int color() {
		return COLORS[colorIndex()];
	}

	public static void setScale(float value) {
		data.scale = Math.max(MIN_SCALE, Math.min(value, MAX_SCALE));
	}

	public static void load() {
		try {
			if (Files.exists(FILE)) {
				try (Reader r = Files.newBufferedReader(FILE)) {
					Data d = GSON.fromJson(r, Data.class);
					if (d != null) data = d;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void save() {
		try (Writer w = Files.newBufferedWriter(FILE)) {
			GSON.toJson(data, w);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onInitializeClient() {
		load();
		// Type /nametags in chat to open the settings screen.
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
			dispatcher.register(ClientCommandManager.literal("nametags").executes(ctx -> {
				MinecraftClient mc = MinecraftClient.getInstance();
				mc.send(() -> mc.setScreen(new NametagScreen(null)));
				return 1;
			})));
	}
}
