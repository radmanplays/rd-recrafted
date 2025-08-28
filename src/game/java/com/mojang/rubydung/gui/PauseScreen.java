package com.mojang.rubydung.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.rubydung.Player;
import com.mojang.rubydung.level.Level;
import com.mojang.rubydung.level.LevelListener;
import com.mojang.rubydung.level.LevelRenderer;

public class PauseScreen extends Screen {

	public void init() {
		this.buttons.clear();
		this.buttons.add(new Button(1, this.width / 2 - 100, this.height / 4 + 24, "Back to game"));
		this.buttons.add(new Button(2, this.width / 2 - 100, this.height / 4 + 48, "Options"));
		this.buttons.add(new Button(3, this.width / 2 - 100, this.height / 4 + 72, "Save level.."));
		this.buttons.add(new Button(4, this.width / 2 - 100, this.height / 4 + 96, "Load level.."));
		this.buttons.add(new Button(5, this.width / 2 - 100, this.height / 4 + 120, "Generate new level..."));
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

	private void buttonClicked(Button button) {
		if(button.id == 1) {
			this.minecraft.setScreen((Screen)null);
			this.minecraft.grabMouse();
		}
		if(button.id == 2) {
			this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.settings));
		}
		if(button.id == 3) {
			minecraft.level.save();
			minecraft.level.savePlayer(minecraft.player.x, minecraft.player.y, minecraft.player.z);
		}
		if(button.id == 4) {
			minecraft.level.load();
			float[] pos = minecraft.level.loadPlayer();
			if (pos != null) {
			    minecraft.player.x = pos[0];
			    minecraft.player.y = pos[1];
			    minecraft.player.z = pos[2];
			    minecraft.player.setPos(pos[0], pos[1], pos[2]);
			}
		}
		if(button.id == 5) {
			minecraft.level.reset();
			minecraft.player.resetPos();
			minecraft.level = new Level(256, 256, 64);
			minecraft.levelRenderer = new LevelRenderer(minecraft.level);
			minecraft.player = new Player(minecraft.level);
			this.minecraft.setScreen((Screen)null);
			this.minecraft.grabMouse();
		}

	}

	public void render(int xm, int ym) {
		this.fillGradient(0, 0, this.width, this.height, 1610941696, -1607454624);
		super.render(xm, ym);
		drawCenteredString("Pause menu", this.width / 2, 40, 16777215);
	}
}
