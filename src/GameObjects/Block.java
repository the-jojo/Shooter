package GameObjects;

import Game.*;

import processing.core.PImage;
import processing.core.PVector;

import java.util.Random;

public class Block {
    public static final int EXPLOSION_FRAMES = 30;
    public static final int WIDTH  = 80/2,
                            HEIGHT = 80/2;
    private static final float SAND_PROB = 0.01f;

    private final MainGame p;
    private float maxHoverDist;

    private PImage img_block;
    private PImage img_expl;
    private PVector initCoords;
    private float curY;
    private float hover;
    private int exploding;
    private boolean active;

    public Block(MainGame pa, int x, int y) {
        p = pa;

        // load imgs
        img_block = p.loadImage("tile2.png", "png");
        img_expl  = p.loadImage("tile2_expl.png", "png");

        // set coords
        initCoords = new PVector(x, y);
        this.curY = y;

        // set hover config
        Random r = new Random();
        hover = r.nextFloat() * (0.1f + 0.1f) - 0.1f;
        maxHoverDist = r.nextFloat() * (4.5f - 3f) + 3f;

        // reset exploding
        exploding = -1;
        active = true;
    }

    public void display(){
        // do not show inactive block
        if(!active)
            return;

        if(exploding < 0){
            // not exploding
            // hover a little
            curY = curY + hover;
            if(Math.abs(curY - initCoords.y) >= maxHoverDist)
                hover  = hover * -1;
            // display image
            p.image(img_block, initCoords.x, curY, WIDTH, HEIGHT);
            // spawn sand by random
            if(p.random(0, 1) <= SAND_PROB){
                float x = p.random(initCoords.x, initCoords.x + WIDTH);
                float y = p.random(curY, curY + HEIGHT);
                SandParticle particle = new SandParticle(p, Math.round(x), Math.round(y), p.getCurDuel().getWind().getAsVector().mult(10f));
                p.getCurDuel().addSandParticle(particle);
            }
        }else{
            // explosion in process
            exploding ++;

            if(exploding >= EXPLOSION_FRAMES){
                // explosion done. allow deletion
                active = false;
                GameState.get().nextPlayer();
            }else{
                // display explosion
                p.image(img_expl, initCoords.x, curY, WIDTH, HEIGHT);
            }
        }

    }

    public PVector getPosition(){
        return new PVector(initCoords.x, curY);
    }

    public void explode(PVector shellVeloc){
        PVector middle = new PVector(initCoords.x + WIDTH/2, curY + HEIGHT/2);
        for(int i = 0; i < 500; i++){
            float x = p.random(initCoords.x, initCoords.x + WIDTH);
            float y = p.random(curY, curY + HEIGHT);
            PVector v = new PVector((x-middle.x)/3, (y-middle.y)/3).add(shellVeloc);
            SandParticle particle = new SandParticle(p, Math.round(x), Math.round(y), v);
            p.getCurDuel().addSandParticle(particle);
        }
        exploding = 0;
        GameState.get().setExplosion();
    }
    public boolean isActive(){return active;}
}
