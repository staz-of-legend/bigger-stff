package com.example.biggernametags;

import net.fabricmc.api.ClientModInitializer;

public class BiggerNametags implements ClientModInitializer {
	/** Name tag size multiplier. 1.0 = vanilla. Hard-capped so it never gets silly. */
	public static final float SCALE = 1.5f;
	public static final float MAX_SCALE = 2.0f;

	public static float scale() {
		return Math.max(1.0f, Math.min(SCALE, MAX_SCALE));
	}

	@Override
	public void onInitializeClient() {}
}
