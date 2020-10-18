package GameObjects;

import Game.*;

import processing.core.PImage;
import processing.core.PVector;

public class Tank {
    public static final int EXPLOSION_FRAMES = 30;
    public static final int WIDTH = 120/2,
                            HEIGHT = 60/2;
    public static final float FORCE_MAX = 30f,
                              FORCE_MIN = 5f;
    public static final float ANGLE_MAX = 0f,
                              ANGLE_MIN = (float)-Math.PI;

    protected final MainGame p;
    protected PImage img_tank;
    protected PImage img_expl;
    protected PVector position;
    protected float gunAngle;
    protected float gunForce;
    protected int exploding;
    protected boolean active;

    public Tank(MainGame pa, int startx, int starty, float startAngle, float startForce){
        p = pa;

        // load img
        img_tank = p.loadImage("tank1.png", "png");
        img_expl = p.loadImage("tile2_expl.png", "png");

        // set position
        position = new PVector(startx, starty);
        gunAngle = startAngle;
        gunForce = startForce;

        // reset exploding
        exploding = -1;
        active = true;
    }

    public void display(){
        // do not show inactive tank
        if(!active)
            return;

        if(exploding < 0){
            // not exploding
            // show image and gun
            p.image(img_tank, position.x, position.y, WIDTH, HEIGHT);
            p.stroke(153, 204, 255);
            p.strokeWeight(4);
            p.line(position.x + WIDTH/2, position.y+4, gun_x(), gun_y());
        }else{
            // explosion in process
            exploding ++;

            if(exploding >= EXPLOSION_FRAMES){
                // explosion done. allow deletion
                active = false;
            }else{
                // display explosion
                p.image(img_expl, position.x, position.y, WIDTH, HEIGHT);
            }
        }
    }

    public void explode(){
        exploding = 0;
        GameState.get().setExplosion();;
    }

    public void move(int dist){
        position.add(dist, 0);
    }

    public void rotate(float angle){
        gunAngle = gunAngle + angle;
        if(gunAngle > ANGLE_MAX)
            gunAngle = ANGLE_MAX;
        if(gunAngle < ANGLE_MIN)
            gunAngle = ANGLE_MIN;
    }

    public void force(float f){
        gunForce = gunForce + f;
        if(gunForce > FORCE_MAX)
            gunForce = FORCE_MAX;
        if(gunForce < FORCE_MIN)
            gunForce = FORCE_MIN;
    }

    public int gun_x(){
        PVector gun = new PVector(25, 0);
        gun.rotate(gunAngle);
        return Math.round(gun.x + position.x + WIDTH/2);
    }

    public int gun_y(){
        PVector gun = new PVector(25, 0);
        gun.rotate(gunAngle);
        return Math.round(gun.y + position.y + 4);
    }

    public PVector getPosition(){return position;}

    public boolean isActive(){return active;}

    public float getGunAngle(){
        return gunAngle;
    }

    public float getGunForce(){
        return gunForce;
    }

    public PVector getMiddleCoords(){
        return new PVector(position.x + WIDTH/2, position.y + HEIGHT/2);
    }
}
