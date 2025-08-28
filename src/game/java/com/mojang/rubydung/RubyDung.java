package com.mojang.rubydung;

import com.mojang.rubydung.gui.Font;
import com.mojang.rubydung.gui.PauseScreen;
import com.mojang.rubydung.gui.Screen;
import com.mojang.rubydung.level.Chunk;
import com.mojang.rubydung.level.Level;
import com.mojang.rubydung.level.LevelRenderer;
import com.mojang.rubydung.level.Tesselator;
import com.mojang.rubydung.settings.GameSettings;
import com.mojang.util.GLAllocation;
import com.mojang.util.MathHelper;

import net.lax1dude.eaglercraft.EagRuntime;

import java.io.IOException;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import net.lax1dude.eaglercraft.internal.EnumPlatformType;
import net.lax1dude.eaglercraft.internal.buffer.FloatBuffer;
import net.lax1dude.eaglercraft.internal.buffer.IntBuffer;

public class RubyDung implements Runnable {
	private static final boolean FULLSCREEN_MODE = false;
	public int width;
	public int height;
	private int lastWidth;
	private int lastHeight;
	public Font font;
	private FloatBuffer fogColor = GLAllocation.createFloatBuffer(4);
	private Timer timer = new Timer(60.0F);
	public Level level;
	public LevelRenderer levelRenderer;
	public Player player;
	private Screen screen = null;
	private IntBuffer viewportBuffer = GLAllocation.createIntBuffer(16);
	private IntBuffer selectBuffer = GLAllocation.createIntBuffer(2000);
	private HitResult hitResult = null;
	public GameSettings settings;
	int frames = 0;
	private boolean mouseGrabbed = false;
	private String fpsString = "";
	private boolean showAutosave = false;
	private int autosaveTex = -1;
	private int autosaveFrame = 0;
	private int autosaveTick = 0;
	private int autosaveDisplayTime = 0;
	private static final int[] AUTOSAVE_INTERVALS = {
		    600,
		    900,
		    1800,
		    10800,
		    -1
		};

	public void init() throws LWJGLException, IOException {
		int col = 920330;
		float fr = 0.5F;
		float fg = 0.8F;
		float fb = 1.0F;
		this.fogColor.put(new float[]{(float)(col >> 16 & 255) / 255.0F, (float)(col >> 8 & 255) / 255.0F, (float)(col & 255) / 255.0F, 1.0F}).flip();
		Display.create();
		Keyboard.create();
		Mouse.create();
		this.width = Display.getWidth();
		this.height = Display.getHeight();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glClearColor(fr, fg, fb, 0.0F);
		GL11.glClearDepth(1.0D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		setupProjection(this.width, this.height);
		this.settings = new GameSettings(this);
		this.level = new Level(256, 256, 64);
		this.levelRenderer = new LevelRenderer(this.level);
		this.player = new Player(this.level);
		float[] pos = level.loadPlayer();
		if (pos != null) {
		    player.x = pos[0];
		    player.y = pos[1];
		    player.z = pos[2];
		    player.setPos(pos[0], pos[1], pos[2]);
		}
		this.font = new Font("/default.gif", new Textures());
		this.autosaveTex = Textures.loadTexture("/autosave.png", GL11.GL_NEAREST);
		this.grabMouse();
		int interval = AUTOSAVE_INTERVALS[settings.autosave];
		saveCountdown = interval > 0 ? interval : Integer.MAX_VALUE;
	}
	
    private void setupProjection(int width, int height) {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GLU.gluPerspective(70.0F, (float) width / height, 0.05F, 1000.0F);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

	public void destroy() {
		this.level.save();
		level.savePlayer(player.x, player.y, player.z);
		EagRuntime.destroy();
	}
	
	public void grabMouse() {
		if(!this.mouseGrabbed) {
			this.mouseGrabbed = true;
			Mouse.setGrabbed(true);

			this.setScreen((Screen)null);
		}
	}

	public void releaseMouse() {
		if(this.mouseGrabbed) {
			this.mouseGrabbed = false;
			Mouse.setGrabbed(false);

			this.setScreen(new PauseScreen());
		}
	}
	
	public void setScreen(Screen screen) {
	    if (this.height == 0) {
	        return;
	    }
		this.screen = screen;
		if(screen != null) {
			int screenWidth = this.width * 240 / this.height;
			int screenHeight = this.height * 240 / this.height;
			screen.init(this, screenWidth, screenHeight);
		}

	}

	public void run() {
		try {
			this.init();
		} catch (Exception var9) {
			System.out.println("Failed to start RubyDung");
			throw new RuntimeException(var9);
		}

		long lastTime = System.currentTimeMillis();

		try {
			while(!Display.isCloseRequested()) {
				this.timer.advanceTime();

				for(int e = 0; e < this.timer.ticks; ++e) {
					this.tick();
				}

				this.render(this.timer.a);
				++frames;

				while(System.currentTimeMillis() >= lastTime + 1000L) {
					this.fpsString = frames + " fps, " + Chunk.updates + " chunk updates";
					System.out.println(frames + " fps, " + Chunk.updates);
					Chunk.updates = 0;
					lastTime += 1000L;
					frames = 0;
				}
			}
		} catch (Exception var10) {
			var10.printStackTrace();
		} finally {
			this.destroy();
		}

	}
	
	private int saveCountdown;

	private void levelSave() {
	    if (level == null) return;
	    
	    int interval = AUTOSAVE_INTERVALS[settings.autosave];
	    if (interval <= 0) return;

	    saveCountdown--;
	    if (saveCountdown <= 0) {
	        level.save();
	        level.savePlayer(player.x, player.y, player.z);
	        saveCountdown = interval;
	        
	        showAutosave = true;
	        autosaveFrame = 0;
	        autosaveTick = 0;
	        autosaveDisplayTime = 60;
	    }
	}


	public void tick() {
		if(this.screen != null) {
			this.screen.updateEvents();
			if(this.screen != null) {
				this.screen.tick();
			}
		}
		this.player.tick();
		levelSave();
		if (showAutosave) {
		    autosaveTick++;
		    if (autosaveTick >= 6) {
		        autosaveTick = 0;
		        autosaveFrame = (autosaveFrame + 1) % 9;
		    }

		    autosaveDisplayTime--;
		    if (autosaveDisplayTime <= 0) {
		        showAutosave = false;
		    }
		}
	}


	private void moveCameraToPlayer(float a) {
		GL11.glTranslatef(0.0F, 0.0F, -0.3F);
		GL11.glRotatef(this.player.xRot, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(this.player.yRot, 0.0F, 1.0F, 0.0F);
		float x = this.player.xo + (this.player.x - this.player.xo) * a;
		float y = this.player.yo + (this.player.y - this.player.yo) * a;
		float z = this.player.zo + (this.player.z - this.player.zo) * a;
		GL11.glTranslatef(-x, -y, -z);
	}

	private void setupCamera(float a) {
        this.width = Display.getWidth();
        this.height = Display.getHeight();

	    if (width != lastWidth || height != lastHeight) {
	        lastWidth = width;
	        lastHeight = height;
            GL11.glViewport(0, 0, this.width, this.height);
            setupProjection(this.width, this.height);
        }
		GL11.glLoadIdentity();
		this.moveCameraToPlayer(a);
	}

	private void pick(float a) {
	    double px = player.x;
	    double py = player.y;
	    double pz = player.z;

	    float yaw = (float) Math.toRadians(player.yRot);
	    float pitch = (float) Math.toRadians(player.xRot);

	    double dx = Math.sin(yaw) * Math.cos(pitch);
	    double dy = -Math.sin(pitch);
	    double dz = -Math.cos(yaw) * Math.cos(pitch);

	    double reach = 3.0;
	    double step = 0.05;

	    HitResult closestHit = null;

	    double closestT = reach + 1;

	    for (int x = (int) Math.floor(px - reach); x <= (int) Math.floor(px + reach); x++) {
	        for (int y = (int) Math.floor(py - (reach + 1)); y <= (int) Math.floor(py + (reach + 1)); y++) {
	            for (int z = (int) Math.floor(pz - reach); z <= (int) Math.floor(pz + reach); z++) {
	                int block = level.getTile(x, y, z);
	                if (block != 0) {

	                    double txmin = (x - px) / dx;
	                    double txmax = (x + 1 - px) / dx;
	                    if (txmin > txmax) { double temp = txmin; txmin = txmax; txmax = temp; }

	                    double tymin = (y - py) / dy;
	                    double tymax = (y + 1 - py) / dy;
	                    if (tymin > tymax) { double temp = tymin; tymin = tymax; tymax = temp; }

	                    double tzmin = (z - pz) / dz;
	                    double tzmax = (z + 1 - pz) / dz;
	                    if (tzmin > tzmax) { double temp = tzmin; tzmin = tzmax; tzmax = temp; }

	                    double tEnter = Math.max(Math.max(txmin, tymin), tzmin);
	                    double tExit = Math.min(Math.min(txmax, tymax), tzmax);

	                    if (tEnter <= tExit && tEnter < closestT && tEnter >= 0 && tEnter <= reach + 1) {
	                        closestT = tEnter;

	                        int face;
	                        if (tEnter == txmin) {
	                            face = dx > 0 ? 4 : 5;
	                        } else if (tEnter == tymin) {
	                            face = dy > 0 ? 0 : 1;
	                        } else {
	                            face = dz > 0 ? 2 : 3;
	                        }

	                        closestHit = new HitResult(x, y, z, block, face);
	                    }
	                }
	            }
	        }
	    }

	    this.hitResult = closestHit;
	}

	public void render(float a) {
		float xo = (float)Mouse.getDX();
		float yo = (float)Mouse.getDY();
		if (settings.invertMouse) yo = -yo;
		this.player.turn(xo, yo);
		this.pick(a);
		if(this.screen == null) {
			while(Mouse.next()) {
				if (!this.mouseGrabbed && Mouse.getEventButtonState()) {
					this.grabMouse();
				}
	
				if(Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.hitResult != null) {
					this.level.setTile(this.hitResult.x, this.hitResult.y, this.hitResult.z, 0);
				}
	
				if(Mouse.getEventButton() == 1 && Mouse.getEventButtonState() && this.hitResult != null) {
					int x = this.hitResult.x;
					int y = this.hitResult.y;
					int z = this.hitResult.z;
					if(this.hitResult.f == 0) {
						--y;
					}
	
					if(this.hitResult.f == 1) {
						++y;
					}
	
					if(this.hitResult.f == 2) {
						--z;
					}
	
					if(this.hitResult.f == 3) {
						++z;
					}
	
					if(this.hitResult.f == 4) {
						--x;
					}
	
					if(this.hitResult.f == 5) {
						++x;
					}
	
					this.level.setTile(x, y, z, 1);
				}
			}
	
			while(Keyboard.next()) {
				Mouse.setGrabbed(true);
				if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
					this.releaseMouse();
				}
				if(Keyboard.getEventKey() == Keyboard.KEY_RETURN && Keyboard.getEventKeyState()) {
					this.level.save();
					level.savePlayer(player.x, player.y, player.z);
			        showAutosave = true;
			        autosaveFrame = 0;
			        autosaveTick = 0;
			        autosaveDisplayTime = 60;
				}
				if(Keyboard.getEventKey() == Keyboard.KEY_BACK && Keyboard.getEventKeyState()) {
					this.level.reset();
					this.player.resetPos();
					this.level = new Level(256, 256, 64);
					this.levelRenderer = new LevelRenderer(this.level);
					this.player = new Player(this.level);
				}
			}
		}

		if (Display.wasResized()) {
			this.width = Display.getWidth();
			this.height = Display.getHeight();
			GL11.glViewport(0, 0, this.width, this.height);
			if(this.screen != null) {
				Screen sc = this.screen;
				this.setScreen((Screen)null);
				this.setScreen(sc);
			}
		}

		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
		this.setupCamera(a);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
	    float density;
	    switch (settings.shadowfog) {
	        case 1:
	            density = 0.05f;
	            break;
	        case 2:
	            density = 0.1f;
	            break;
	        case 3:
	            density = 0.2f;
	            break;
	        default:
	            density = 0f;
	    }
		GL11.glFogf(GL11.GL_FOG_DENSITY, density);
		GL11.glFog(GL11.GL_FOG_COLOR, this.fogColor);
		GL11.glDisable(GL11.GL_FOG);
		this.levelRenderer.render(this.player, 0);
		GL11.glEnable(GL11.GL_FOG);
		this.levelRenderer.render(this.player, 1);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		if(this.hitResult != null) {
			this.levelRenderer.renderHit(this.hitResult);
		}

		GL11.glDisable(GL11.GL_FOG);
		drawgui();
		if(Display.isVSyncSupported()) {
			Display.setVSync(this.settings.limitFramerate);
		} else {
			this.settings.limitFramerate = false;
		}
		Display.update();
	}
	
	double round(double value) {
	    return Math.round(value * 100.0) / 100.0;
	}
	String format(double value) {
	    int temp = (int) Math.round(value * 100);
	    int integerPart = temp / 100;
	    int decimalPart = temp % 100;
	    return integerPart + "." + (decimalPart < 10 ? "0" + decimalPart : decimalPart);
	}
	
	public void drawgui() {
	    if (this.height == 0) {
	        return;
	    }
		int screenWidth = this.width * 240 / this.height;
		int screenHeight = this.height * 240 / this.height;
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, (double)screenWidth, (double)screenHeight, 0.0D, 100.0D, 300.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -200.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(settings.showFrameRate || settings.showCords) {
			this.font.drawShadow("rd-132211(rd: recrafted)", 2, 2, 16777215);
		}
		if(settings.showFrameRate) {
			this.font.drawShadow(fpsString, 2, 12, 16777215);
		}
		if (settings.showCords) {
		    int y = settings.showFrameRate ? 22 : 12;
		    this.font.drawShadow("XYZ: " + format(player.x) + " " + format(player.y) + " " + format(player.z), 2, y, 16777215);
		}

		int wc = screenWidth / 2;
		int hc = screenHeight / 2;
		Tesselator t = Tesselator.instance;
		t.init();
		t.vertex((float)(wc + 1), (float)(hc - 4), 0.0F);
		t.vertex((float)(wc - 0), (float)(hc - 4), 0.0F);
		t.vertex((float)(wc - 0), (float)(hc + 5), 0.0F);
		t.vertex((float)(wc + 1), (float)(hc + 5), 0.0F);
		t.vertex((float)(wc + 5), (float)(hc - 0), 0.0F);
		t.vertex((float)(wc - 4), (float)(hc - 0), 0.0F);
		t.vertex((float)(wc - 4), (float)(hc + 1), 0.0F);
		t.vertex((float)(wc + 5), (float)(hc + 1), 0.0F);
		t.flush();
		
		if (showAutosave && autosaveTex >= 0) {
			GL11.glEnable(GL11.GL_BLEND);
		    GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		    GL11.glBindTexture(GL11.GL_TEXTURE_2D, autosaveTex);

		    int frameWidth = 18;
		    int frameHeight = 30;

		    float u0 = (autosaveFrame * frameWidth) / 162.0f;
		    float u1 = ((autosaveFrame + 1) * frameWidth) / 162.0f;
		    float v0 = 0.0f;
		    float v1 = 1.0f;

		    int x = screenWidth - frameWidth - 15;
		    int y = 5;

		    t.init();
		    t.vertexUV(x, y + frameHeight, 0.0F, u0, v1);
		    t.vertexUV(x + frameWidth, y + frameHeight, 0.0F, u1, v1);
		    t.vertexUV(x + frameWidth, y, 0.0F, u1, v0);
		    t.vertexUV(x, y, 0.0F, u0, v0);
		    t.flush();
		    this.font.drawShadow("Saving...", screenWidth - this.font.width("Saving...") - 5, 40, 16777215);

		    GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
		int xMouse = Mouse.getX() * screenWidth / this.width;
		int yMouse = screenHeight - Mouse.getY() * screenHeight / this.height - 1;
		if(this.screen != null) {
			this.screen.render(xMouse, yMouse);
		}
	    GL11.glMatrixMode(GL11.GL_PROJECTION);
	    GL11.glPopMatrix();
	    GL11.glMatrixMode(GL11.GL_MODELVIEW);
	    GL11.glPopMatrix();

	}

	public static void checkError() {
		int e = GL11.glGetError();
		if(e != 0) {
			throw new IllegalStateException(GLU.gluErrorString(e));
		}
	}
}
