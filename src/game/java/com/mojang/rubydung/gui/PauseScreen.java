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
		this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 24, "Generate new level..."));
		this.buttons.add(new Button(1, this.width / 2 - 100, this.height / 4 + 48, "Save level.."));
		this.buttons.add(new Button(2, this.width / 2 - 100, this.height / 4 + 72, "Load level.."));
		this.buttons.add(new Button(3, this.width / 2 - 100, this.height / 4 + 96, "Back to game"));
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
		if(button.id == 0) {
			minecraft.level.reset();
			minecraft.player.resetPos();
			minecraft.level = new Level(256, 256, 64);
			minecraft.levelRenderer = new LevelRenderer(minecraft.level);
			minecraft.player = new Player(minecraft.level);
			this.minecraft.setScreen((Screen)null);
			this.minecraft.grabMouse();
		}
		if(button.id == 1) {
			minecraft.level.save();
		}
		if(button.id == 2) {
			minecraft.level.load();
		}

		if(button.id == 3) {
			this.minecraft.setScreen((Screen)null);
			this.minecraft.grabMouse();
		}

	}

	public void render(int xm, int ym) {
		this.fillGradient(0, 0, this.width, this.height, 1610941696, -1607454624);
		super.render(xm, ym);

	}
}
