package GameObjects;

import Game.*;

import processing.core.PVector;

public class Shell {
    private final MainGame p;

    // damping factor to simulate drag, as per Millington
    private static final float DAMPING = .995f ;
    public  static final float EXPLOSION_R = 7f;

    // Vector to accumulate forces prior to integration
    private PVector forceAccumulator ;

    // store old position to allow shell representation as line
    private PVector oldPosition, position, velocity ;

    // Store inverse mass to allow simulation of infinite mass
    private float invMass ;
    private float heading, speed;
    private boolean active = true;

    public Shell(MainGame pa, int initX, int initY, float initHeading, float initSpeed, boolean isSimulation){
        if(!isSimulation)
            GameState.get().setFiring();
        p = pa;
        oldPosition = new PVector(initX, initY);
        position = new PVector(initX, initY);
        velocity = new PVector(initSpeed, 0);
        velocity.rotate(initHeading);
        forceAccumulator = new PVector(0, 0);
        invMass = 1f/2f; // seems good
        active = true;
    }

    // update position and velocity
    public void integrate(Wind wind, Gravity gravity) {
        // If infinite mass, we don't integrate
        if (invMass <= 0f) return ;

        oldPosition = position.copy();

        // add forces
        forceAccumulator.add(wind.getAsVector()) ;
        forceAccumulator.add(gravity.getAsVector()) ;

        // update position
        position.add(velocity) ;

        PVector resultingAcceleration = forceAccumulator.get() ;
        resultingAcceleration.mult(invMass) ;

        // update velocity
        velocity.add(resultingAcceleration) ;

        // apply damping - disabled when Drag force present
        velocity.mult(DAMPING) ;

        // Clear accumulator
        forceAccumulator.x = 0 ;
        forceAccumulator.y = 0 ;
    }

    public void display(){
        // do not draw if not active
        if(!active)
            return;

        // draw
        p.stroke(255, 0, 0);
        p.strokeWeight(7);
        p.line(oldPosition.x, oldPosition.y, position.x, position.y);

        checkCollision();

        if(outBounds()){
            GameState.get().nextPlayer();
            active = false;
        }


    }

    // gets best distance to goal
    public float simulate(Wind wind, Gravity gravity, PVector goal){
        float bestDist = Float.MAX_VALUE;
        // loop until leaves the screen
        while (isActive()) {
            // integrate
            integrate(wind, gravity);

            // out of bounds?
            if(outBounds()){
                active = false;
                break;
            }

            // get distance
            float dist = Math.abs(p.dist(position.x, position.y, goal.x, goal.y));
            if(dist <= bestDist){
                bestDist = dist;
            }
        }
        return bestDist;
    }

    // If you do need the mass, here it is:
    public float getMass() {return 1/invMass ;}

    private void checkCollision(){
        Tank p1 = p.getCurDuel().getPlayer1();
        Tank p2 = p.getCurDuel().getPlayer2();
        if(hitTank(p1)){
            // hit tank 1
            System.out.println("HIT tank 1");
            p1.explode();
            GameState.get().hitBy(2);
            active = false;
        }
        if(hitTank(p2)){
            // hit tank 2
            System.out.println("HIT tank 2");
            p2.explode();
            GameState.get().hitBy(1);
            active = false;
        }
        // check blocks
        for (int i = 0; i < p.getCurDuel().getTerrain().size(); i++) {
            Block b = p.getCurDuel().getTerrain().get(i);
            if(hitBlock(b)){
                b.explode(velocity);
                active = false;
                return;
            }
        }
    }

    private boolean hitBlock(Block block){
        float block_left_edge  = block.getPosition().x;
        float block_right_edge = block.getPosition().x + Block.WIDTH;
        float shell_left_edge  = position.x  - EXPLOSION_R;
        float shell_right_edge = position.x  + EXPLOSION_R;
        float block_upper_edge = block.getPosition().y;
        float block_lower_edge = block.getPosition().y + Block.HEIGHT;
        float shell_upper_edge = position.y - EXPLOSION_R;
        float shell_lower_edge = position.y + EXPLOSION_R;

        // explosion touches block?
        // x axis
        if((block_left_edge <= shell_right_edge && block_right_edge >= shell_right_edge) || (block_left_edge <= shell_left_edge && block_right_edge >= shell_left_edge)){
            // y axis
            if((block_upper_edge <= shell_lower_edge && block_lower_edge >= shell_lower_edge) || (block_upper_edge <= shell_upper_edge && block_lower_edge >= shell_upper_edge)){
                // collision
                System.out.println("HIT block");
                return true;
            }
        }
        return false;
    }

    private boolean hitTank(Tank player){
        float tank_left_edge = player.getPosition().x;
        float tank_right_edge = player.getPosition().x + Tank.WIDTH;
        float shell_left_edge = position.x  - EXPLOSION_R;
        float shell_right_edge = position.x  + EXPLOSION_R;
        float tank_upper_edge = player.getPosition().y;
        float tank_lower_edge = player.getPosition().y + Tank.HEIGHT;
        float shell_upper_edge = position.y - EXPLOSION_R;
        float shell_lower_edge = position.y + EXPLOSION_R;

        // explosion touches tank?
        // x axis
        if((tank_left_edge <= shell_right_edge && tank_right_edge >= shell_right_edge) || (tank_left_edge <= shell_left_edge && tank_right_edge >= shell_left_edge)){
            // y axis
            if((tank_upper_edge <= shell_lower_edge && tank_lower_edge >= shell_lower_edge) || (tank_upper_edge <= shell_upper_edge && tank_lower_edge >= shell_upper_edge)){
                return true;
            }
        }
        return false;
    }

    private boolean outBounds(){
        if(position.x <= 0 || position.x >= MainGame.WIDTH || position.y > MainGame.HEIGHT){
            return true;
        }
        return false;
    }

    public boolean isActive(){
        return active;
    }
    public PVector getPosition(){return position;}
}
