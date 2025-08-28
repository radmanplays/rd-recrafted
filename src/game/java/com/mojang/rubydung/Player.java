package com.mojang.rubydung;
import com.mojang.rubydung.level.Level;
import org.lwjgl.input.Keyboard;

public class Player extends Entity {
    public boolean isFlying = false;
    private float maxFlySpeed = 0.2F;
    private float flyAcceleration = 0.002F;
    
    public Player(Level level) {
        super(level);
        this.heightOffset = 1.62F; 
    }
    
    @Override
    public void tick() {
        super.tick(); 
        float xa = 0.0F;
        float ya = 0.0F;
        if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
            this.resetPos();
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W)) {
            --ya;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S)) {
            ++ya;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A)) {
            --xa;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D)) {
            ++xa;
        }
        if (isFlying) {
            float flySpeed = 0.013F;
            boolean moving = xa != 0 || ya != 0
                    || Keyboard.isKeyDown(Keyboard.KEY_SPACE)
                    || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)
                    || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
            if (moving) {
                flySpeed += flyAcceleration;
                if (flySpeed > maxFlySpeed) {
                    flySpeed = maxFlySpeed;
                }
            } else {
                flySpeed = 0.02F;
            }
            this.moveRelative(xa, ya, flySpeed);
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                this.yd = flySpeed * 10;
            } else if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                this.yd = -(flySpeed * 10);
                if (this.onGround) {
                    isFlying = false;
                    this.yd = 0.0F;
                }
            } else {
                this.yd = 0.0F;
            }
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.91F;
            this.yd *= 0.91F;
            this.zd *= 0.91F;
        } else {
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && this.onGround) {
                this.yd = 0.12F;
            }
            this.moveRelative(xa, ya, this.onGround ? 0.02F : 0.005F);
            this.yd -= 0.005F; 
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.91F;
            this.yd *= 0.98F;
            this.zd *= 0.91F;
            if (this.onGround) {
                this.xd *= 0.8F;
                this.zd *= 0.8F;
            }
        }
    }
}