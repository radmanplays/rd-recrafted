package com.mojang.rubydung.settings;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.rubydung.RubyDung;
import com.mojang.rubydung.Textures;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.Keyboard;
import net.lax1dude.eaglercraft.KeyboardConstants;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.opengl.ImageData;

public final class GameSettings {
	public GameSettings(RubyDung minecraft) {
		bindings = new KeyBinding[] { forwardKey, leftKey, backKey, rightKey, jumpKey, buildKey, chatKey, toggleFogKey,
				saveLocationKey, loadLocationKey, gameModeKey };

		settingCount = 6;

		this.minecraft = minecraft;

		settingsFile = new VFile2("options.txt");

		load();
	}

	private static final String[] autosaves = new String[] { "10 sec", "15 sec", "30 sec", "3 min", "OFF" };
	private static final String[] shadowFogModes = new String[] { "OFF", "LOW", "MEDIUM", "HIGH" };
	public int autosave = 0;
	public int shadowfog = 3;
	public boolean showFrameRate = true;
	public boolean showCords = false;
	public boolean invertMouse = false;
	public boolean limitFramerate = true;
	public KeyBinding forwardKey = new KeyBinding("Forward", 17);
	public KeyBinding leftKey = new KeyBinding("Left", 30);
	public KeyBinding backKey = new KeyBinding("Back", 31);
	public KeyBinding rightKey = new KeyBinding("Right", 32);
	public KeyBinding jumpKey = new KeyBinding("Jump", 57);
	public KeyBinding buildKey = new KeyBinding("Build", 48);
	public KeyBinding chatKey = new KeyBinding("Chat", 20);
	public KeyBinding toggleFogKey = new KeyBinding("Toggle fog", 33);
	public KeyBinding saveLocationKey = new KeyBinding("Save location", 28);
	public KeyBinding loadLocationKey = new KeyBinding("Load location", 19);
	public KeyBinding gameModeKey = new KeyBinding("Switch gamemode", KeyboardConstants.KEY_M);
	public KeyBinding[] bindings;
	private RubyDung minecraft;
	private VFile2 settingsFile;
	public int settingCount;

	private Logger logger = LogManager.getLogger();

	public String getBinding(int key) {
		return bindings[key].name + ": " + Keyboard.getKeyName(bindings[key].key);
	}

	public void setBinding(int key, int keyID) {
		bindings[key].key = keyID;

		save();
	}

	public void toggleSetting(int setting, int fogValue) {
		if (setting == 0) {
			autosave = (autosave + 1) % autosaves.length;
		}
		
		if (setting == 1) {
			shadowfog = (shadowfog + 1) % shadowFogModes.length;
		}

		if (setting == 2) {
			invertMouse = !invertMouse;
		}

		if (setting == 3) {
			showFrameRate = !showFrameRate;
		}

		if (setting == 4) {
			showCords = !showCords;
		}

		if (setting == 5) {
			limitFramerate = !limitFramerate;
		}

		save();
	}

	public String getSetting(int id) {
		return id == 0 ? "AutoSave: " + autosaves[autosave]
				: (id == 1 ? "ShadowFog: " + shadowFogModes[shadowfog]
						: (id == 2 ? "Invert mouse: " + (invertMouse ? "ON" : "OFF")
								: (id == 3 ? "Show FPS: " + (showFrameRate ? "ON" : "OFF")
										: (id == 4 ? "Show Cords: " + (showCords ? "ON" : "OFF")
											: (id == 5 ? "Limit framerate: " + (limitFramerate ? "ON" : "OFF") : "")))));
	}

	private void load() {
		try {
			if (settingsFile.exists()) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(settingsFile.getInputStream()));

				String line = null;

				while ((line = reader.readLine()) != null) {
					String[] setting = line.split(":");

					if (setting[0].equals("autosave")) {
						autosave = Integer.parseInt(setting[1]);
					}

					if (setting[0].equals("shadowfog")) {
						shadowfog = Integer.parseInt(setting[1]);
					}

					if (setting[0].equals("invertYMouse")) {
						invertMouse = setting[1].equals("true");
					}

					if (setting[0].equals("showFrameRate")) {
						showFrameRate = setting[1].equals("true");
					}

					if (setting[0].equals("showCords")) {
						showCords = setting[1].equals("true");
					}

					if (setting[0].equals("limitFramerate")) {
						limitFramerate = setting[1].equals("true");
					}

					for (int index = 0; index < this.bindings.length; index++) {
						if (setting[0].equals("key_" + bindings[index].name)) {
							bindings[index].key = Integer.parseInt(setting[1]);
						}
					}
				}

				reader.close();
			}
		} catch (Exception e) {
			logger.error("Failed to load options");
			logger.error(e);
		}
	}

	private void save() {
		try {
			PrintWriter writer = new PrintWriter(settingsFile.getOutputStream());

			writer.println("autosave:" + autosave);
			writer.println("shadowfog:" + shadowfog);
			writer.println("invertYMouse:" + invertMouse);
			writer.println("showFrameRate:" + showFrameRate);
			writer.println("showCords:" + showCords);
			writer.println("limitFramerate:" + limitFramerate);

			for (int binding = 0; binding < bindings.length; binding++) {
				writer.println("key_" + bindings[binding].name + ":" + bindings[binding].key);
			}

			writer.close();
		} catch (Exception e) {
			logger.error("Failed to save options");
			logger.error(e);
		}
	}

}
