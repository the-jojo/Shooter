package Game;

import GameObjects.*;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Random;

/**
 * Representation of one set of 5 Confrontations
 */
public class Duel {
    private final MainGame p;
    public static final int FLOOR_Y = MainGame.HEIGHT - 40;
    public static final int MIN_BLOCK_NO = 5;
    public static final int MAX_BLOCK_NO = 20;
    public static final int PLATFORM_HEIGHT = 61/2;
    public static final int PLATFORM_WIDTH = 480/2;
    public static final int PLATFORM_1_X = 2;
    public static final int PLATFORM_2_X = MainGame.WIDTH - PLATFORM_WIDTH - 2;
    public static final int TERRAIN_LEFT_EDGE = PLATFORM_1_X + PLATFORM_WIDTH;
    public static final int TERRAIN_RIGHT_EDGE = PLATFORM_2_X;

    private PImage platform;
    private Tank player1;
    private Tank player2;
    private Shell shell;
    private ArrayList<Block> terrain;
    private ForceRegistry forceRegistry ;
    private Wind wind;
    private Gravity gravity;
    private ArrayList<SandParticle> sand;
    private boolean againstAI;
    private boolean showedAiIntro;

    public Duel(MainGame pa, boolean againstAI){
        p = pa;
        this.againstAI = againstAI;
        showedAiIntro = false;

        // load platforms
        platform = p.loadImage("platform.png", "png");

        reset();
    }

    private void reset(){
        int blockNo = Math.round(p.random(MIN_BLOCK_NO, MAX_BLOCK_NO));
        // reset vars
        shell = null;
        forceRegistry = new ForceRegistry();
        sand = new ArrayList<SandParticle>();

        // generate Map
        terrain = new ArrayList<Block>();
        Random r = new Random();
        for(int i = 0; i < blockNo; i++){
            int x = Math.round(p.random(TERRAIN_LEFT_EDGE, TERRAIN_RIGHT_EDGE - Block.WIDTH));
            int y = Math.round(p.random(Block.HEIGHT, FLOOR_Y - Block.HEIGHT));
            Block b = new Block(p, x, y);
            terrain.add(b);
        }
        // add 5 blocks near the bottom
        for(int i = 0; i < 5; i++){
            int x = Math.round(p.random(TERRAIN_LEFT_EDGE, TERRAIN_RIGHT_EDGE - Block.WIDTH));
            int y = Math.round(p.random(FLOOR_Y -  Block.HEIGHT*2, FLOOR_Y - Block.HEIGHT/2));
            Block b = new Block(p, x, y);
            terrain.add(b);
        }

        // make tanks
        player1 = new Tank(p, 100, FLOOR_Y - Tank.HEIGHT, -p.PI/4, 15f);
        if(againstAI){
            player2 = new AI_Tank(p, MainGame.WIDTH-100-Tank.WIDTH, FLOOR_Y - Tank.HEIGHT, -p.PI/2-p.PI/4, 15f);
        }else{
            player2 = new Tank(p, MainGame.WIDTH-100-Tank.WIDTH, FLOOR_Y - Tank.HEIGHT, -p.PI/2-p.PI/4, 15f);
        }

        // add forces
        wind = new Wind(p.random(-0.5f, 0.5f));
        gravity = new Gravity(0.7f);

        // allow start
        if(againstAI && !showedAiIntro) {
            GameState.get().setStory();
            showedAiIntro = true;
        } else
            GameState.get().nextPlayer();
    }

    public void display(){
        // check if a player is destroyed
        if(!player1.isActive() || !player2.isActive()){
            reset();
        }

        // update forces
        forceRegistry.updateForces() ;

        // draw platforms
        p.image(platform, PLATFORM_1_X, FLOOR_Y, PLATFORM_WIDTH, PLATFORM_HEIGHT);
        p.image(platform, PLATFORM_2_X, FLOOR_Y, PLATFORM_WIDTH, PLATFORM_HEIGHT);

        // draw tanks
        player1.display();
        player2.display();

        // draw shell if firing
        if(GameState.get().isFiring()){
            // delete shell if inactive
            if(shell != null && shell.isActive()){
                shell.integrate(wind, gravity);
                shell.display();
            }else{
                shell = null;
            }

        }else if(GameState.get().isPlaying()){
            // draw player hints
            if(GameState.get().isPlayer1())
                displayPlayerHints(player1);
            else if(GameState.get().isPlayer2())
                displayPlayerHints(player2);
        }

        // draw terrain
        for(int i = 0; i < terrain.size(); i++){
            Block b = terrain.get(i);
            // draw active blocks, remove inactive blocks
            if(b.isActive()) {
                b.display();
            } else {
                terrain.remove(b);
            }
        }

        // draw sand
        for(int i = 0; i < sand.size(); i++){
            SandParticle p = sand.get(i);
            p.display();
        }
    }

    public void fire_p1(){
        if(GameState.get().getState() == GameState.State.PLAYER_1){
            shell = new Shell(p, player1.gun_x(), player1.gun_y(), player1.getGunAngle(), player1.getGunForce(), false);
        }
    }

    public void fire_p2(){
        if(GameState.get().getState() == GameState.State.PLAYER_2){
            shell = new Shell(p, player2.gun_x(), player2.gun_y(), player2.getGunAngle(), player2.getGunForce(), false);
        }
    }

    public void addSandParticle(SandParticle particle){
        forceRegistry.add(particle, wind) ;
        forceRegistry.add(particle, gravity);
        sand.add(particle);
    }

    public void removeSandParticle(SandParticle particle){
        sand.remove(particle);
        forceRegistry.remove(particle);
    }

    public Wind getWind(){
        return wind;
    }

    public Gravity getGravity(){
        return gravity;
    }

    public PVector getPlayer1Pos(){
        return player1.getMiddleCoords();
    }

    private void displayPlayerHints(Tank player){
        // show force bar above gun
        PVector vForce = new PVector(player.getGunForce(), 0);
        PVector vSpace = new PVector(10, 0);
        PVector vMax = new PVector(Tank.FORCE_MAX, 0);
        vForce.rotate(player.getGunAngle());
        vSpace.rotate(player.getGunAngle());
        vMax.rotate(player.getGunAngle());
        p.stroke(255, 153, 0);
        p.strokeWeight(8);
        p.line(player.gun_x() + vSpace.x, player.gun_y() + vSpace.y, player.gun_x() + vSpace.x + vMax.x, player.gun_y() + vSpace.y + vMax.y);

        p.stroke(135, 84, 33);
        p.strokeWeight(8);
        p.line(player.gun_x() + vSpace.x, player.gun_y() + vSpace.y, player.gun_x() + vSpace.x + vForce.x, player.gun_y() + vSpace.y + vForce.y);

        // show triangle on player
        p.fill(255, 0, 0);
        p.stroke(255, 0, 0);
        p.strokeWeight(1);
        p.triangle( (player.getMiddleCoords().x),      (player.getMiddleCoords().y - 100),
                    (player.getMiddleCoords().x - 10), (player.getMiddleCoords().y - 110),
                    (player.getMiddleCoords().x + 10), (player.getMiddleCoords().y - 110));
    }

    public void keyPressed(char key){
        // u cant do anything while firing or exploding
        if(GameState.get().isFiring() || GameState.get().isExploding())
            return;

        if(GameState.get().isPlayer1()){
            switch (key){
                case 'a':
                    // left
                    if(player1.getPosition().x-5 >= PLATFORM_1_X){
                        player1.move(-5);
                    }
                    break;
                case 'd':
                    // right
                    if(player1.getPosition().x + Tank.WIDTH + 5 <= PLATFORM_1_X + PLATFORM_WIDTH){
                        player1.move(5);
                    }
                    break;
                case 'w':
                    // elevation up
                    player1.rotate(-0.05f);
                    break;
                case 's':
                    // elevation down
                    player1.rotate(0.05f);
                    break;
                case 'r':
                    // force up
                    player1.force(1f);
                    break;
                case 'f':
                    // force down
                    player1.force(-1f);
                    break;
                case ' ':
                    // fire
                    fire_p1();
                    break;
            }
        }else if(GameState.get().isPlayer2() && !againstAI){
            switch (key){
                case 'a':
                    // left if possible
                    if(player2.getPosition().x-5 >= PLATFORM_2_X){
                        player2.move(-5);
                    }
                    break;
                case 'd':
                    // right
                    if(player2.getPosition().x + Tank.WIDTH + 5 <= PLATFORM_2_X + PLATFORM_WIDTH){
                        player2.move(5);
                    }
                    break;
                case 'w':
                    // elevation up
                    player2.rotate(0.05f);
                    break;
                case 's':
                    // elevation down
                    player2.rotate(-0.05f);
                    break;
                case 'r':
                    // force up
                    player2.force(1f);
                    break;
                case 'f':
                    // force down
                    player2.force(-1f);
                    break;
                case ' ':
                    // fire
                    fire_p2();
                    break;
            }
        }
    }

    public Tank getPlayer1(){
        return player1;
    }

    public Tank getPlayer2(){
        return player2;
    }

    public ArrayList<Block> getTerrain(){ return terrain; }

}
