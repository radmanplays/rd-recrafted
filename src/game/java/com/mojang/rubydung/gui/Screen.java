package com.mojang.rubydung.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.mojang.rubydung.RubyDung;
import com.mojang.rubydung.Textures;
import com.mojang.rubydung.level.Tesselator;

import net.lax1dude.eaglercraft.opengl.Tessellator;
import net.lax1dude.eaglercraft.opengl.VertexFormat;
import net.lax1dude.eaglercraft.opengl.WorldRenderer;

public class Screen {
	protected RubyDung minecraft;
	protected int width;
	protected int height;
	protected Font fontRenderer;
	protected List<Button> buttons = new ArrayList<Button>();
	protected float imgZ = 0.0F;

	public void render(int var1, int var2) {
		for(int i = 0; i < this.buttons.size(); ++i) {
			Button button = (Button)this.buttons.get(i);
			if (button.visible) {
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glBindTexture(3553, Textures.loadTexture("/gui.png", 9728));
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				byte var9 = 1;
				boolean var6 = var1 >= button.x && var2 >= button.y && var1 < button.x + button.width
						&& var2 < button.y + button.height;
				if (!button.active) {
					var9 = 0;
				} else if (var6) {
					var9 = 2;
				}

				drawImage(button.x, button.y, 0, 46 + var9 * 20, button.width / 2, button.height);
				drawImage(button.x + button.width / 2, button.y, 200 - button.width / 2, 46 + var9 * 20, button.width / 2,
						button.height);
				if (!button.active) {
					drawCenteredString(button.text, button.x + button.width / 2, button.y + (button.height - 8) / 2,
							-6250336);
				} else if (var6) {
					drawCenteredString(button.text, button.x + button.width / 2, button.y + (button.height - 8) / 2,
							16777120);
				} else {
					drawCenteredString(button.text, button.x + button.width / 2, button.y + (button.height - 8) / 2,
							14737632);
				}
			}
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

	}
	
	public final void drawImage(int x, int y, int u, int v, int width, int height) {
	    float texU = 0.00390625F;
	    float texV = 0.00390625F;

		Tesselator t = Tesselator.instance;
		t.init();
	    t.vertexUV(x, y + height, imgZ, u * texU, (v + height) * texV);
	    t.vertexUV(x + width, y + height, imgZ, (u + width) * texU, (v + height) * texV);
	    t.vertexUV(x + width, y, imgZ, (u + width) * texU, v * texV);
	    t.vertexUV(x, y, imgZ, u * texU, v * texV);
		t.flush();
	}


	public void init(RubyDung minecraft, int width, int height) {
		this.minecraft = minecraft;
		this.width = width;
		this.height = height;
		this.init();
	}

	public void init() {
	}

	protected void fill(int x0, int y0, int x1, int y1, int col) {
		float a = (float)(col >> 24 & 255) / 255.0F;
		float r = (float)(col >> 16 & 255) / 255.0F;
		float g = (float)(col >> 8 & 255) / 255.0F;
		float b = (float)(col & 255) / 255.0F;
		Tesselator t = Tesselator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(r, g, b, a);
		t.init();
		t.vertex((float)x0, (float)y1, 0.0F);
		t.vertex((float)x1, (float)y1, 0.0F);
		t.vertex((float)x1, (float)y0, 0.0F);
		t.vertex((float)x0, (float)y0, 0.0F);
		t.flush();
		GL11.glDisable(GL11.GL_BLEND);
	}

	protected void fillGradient(int x0, int y0, int x1, int y1, int col1, int col2) {
		float a1 = (float)(col1 >> 24 & 255) / 255.0F;
		float r1 = (float)(col1 >> 16 & 255) / 255.0F;
		float g1 = (float)(col1 >> 8 & 255) / 255.0F;
		float b1 = (float)(col1 & 255) / 255.0F;
		float a2 = (float)(col2 >> 24 & 255) / 255.0F;
		float r2 = (float)(col2 >> 16 & 255) / 255.0F;
		float g2 = (float)(col2 >> 8 & 255) / 255.0F;
		float b2 = (float)(col2 & 255) / 255.0F;
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4f(r1, g1, b1, a1);
		GL11.glVertex2f((float)x1, (float)y0);
		GL11.glVertex2f((float)x0, (float)y0);
		GL11.glColor4f(r2, g2, b2, a2);
		GL11.glVertex2f((float)x0, (float)y1);
		GL11.glVertex2f((float)x1, (float)y1);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	public void drawCenteredString(String str, int x, int y, int color) {
		Font font = this.minecraft.font;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		font.drawShadow(str, x - font.width(str) / 2, y, color);
	}

	public void drawString(String str, int x, int y, int color) {
		Font font = this.minecraft.font;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		font.drawShadow(str, x, y, color);
	}

	public void updateEvents() {
		while(Mouse.next()) {
			if(Mouse.getEventButtonState()) {
				int xm = Mouse.getEventX() * this.width / this.minecraft.width;
				int ym = this.height - Mouse.getEventY() * this.height / this.minecraft.height - 1;
				this.mouseClicked(xm, ym, Mouse.getEventButton());
			}
		}

		while(Keyboard.next()) {
			if(Keyboard.getEventKeyState()) {
				this.keyPressed(Keyboard.getEventCharacter(), Keyboard.getEventKey());
			}
		}

	}

	protected void keyPressed(char eventCharacter, int eventKey) {
	}

	protected void mouseClicked(int x, int y, int button) {
	}

	public void tick() {
	}
}
