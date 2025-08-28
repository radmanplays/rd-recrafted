package com.mojang.rubydung.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.rubydung.Player;
import com.mojang.rubydung.level.Level;
import com.mojang.rubydung.level.LevelListener;
import com.mojang.rubydung.level.LevelRenderer;
import com.mojang.rubydung.settings.GameSettings;

public class OptionsScreen extends Screen {
	private Screen parent;
	private String title = "Options";
	private GameSettings settings;
	
	public OptionsScreen(Screen var1, GameSettings var2) {
		this.parent = var1;
		this.settings = var2;
	}

	public void init() {
		this.buttons.clear();
		for (int var1 = 0; var1 < this.settings.settingCount; ++var1) {
			this.buttons.add(new OptionButton(var1, this.width / 2 - 155 + var1 % 2 * 160,
					this.height / 6 + 24 * (var1 >> 1), this.settings.getSetting(var1)));
		}
//		this.buttons.add(new Button(100, this.width / 2 - 100, this.height / 6 + 120 + 12, "Controls..."));
		this.buttons.add(new Button(200, this.width / 2 - 100, this.height / 6 + 168, "Done"));
	}

	protected void keyPressed(char eventCharacter, int eventKey) {
	}

	protected void mouseClicked(int x, int y, int buttonNum) {
		if(buttonNum == 0) {
			for(int i = 0; i < this.buttons.size(); ++i) {
				Button button = (Button)this.buttons.get(i);
				if(x >= button.x && y >= button.y && x < button.x + button.width && y < button.y + button.height) {
					this.buttonClicked(button);
				}
			}
		}

	}

	private void buttonClicked(Button var1) {
		if (var1.active) {
			if (var1.id < 100) {
				this.settings.toggleSetting(var1.id, 1);
				var1.text = this.settings.getSetting(var1.id);
			}

			if (var1.id == 100) {
//				this.minecraft.setCurrentScreen(new ControlsScreen(this, this.settings));
			}

			if (var1.id == 200) {
				this.minecraft.setScreen(this.parent);
			}

		}

	}

	public void render(int xm, int ym) {
		this.fillGradient(0, 0, this.width, this.height, 0x60000000, -1607454624);
		drawCenteredString(this.title, this.width / 2, 20, 16777215);
		super.render(xm, ym);
	}
}
