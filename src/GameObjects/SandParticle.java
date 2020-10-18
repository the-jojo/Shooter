package GameObjects;

import Game.*;

import processing.core.PVector;

/*
  * a representation of a point mass
  * Adapted from https://studres.cs.st-andrews.ac.uk/CS4303/Lectures/CS4303_1819_Physics/BungeeSketch/Particle.pde
  * By Prof Ian Miguel
  */
public class SandParticle {

    private final MainGame p;

    private PVector position, velocity ;

    // Vector to accumulate forces prior to integration
    private PVector forceAccumulator ;

    // damping factor to simulate drag, as per Millington
    private static final float DAMPING = .945f ;

    // Store inverse mass to allow simulation of infinite mass
    private float invMass ;

    // If you do need the mass, here it is:
    public float getMass() {return 1/invMass ;}

    public SandParticle(MainGame pa, int x, int y) {
        p = pa;
        position = new PVector(x, y) ;
        velocity = new PVector(0, 0) ;
        forceAccumulator = new PVector(0, 0) ;
        invMass = 1f/0.001f ;
    }

    public SandParticle(MainGame pa, int x, int y, PVector initVel) {
        p = pa;
        position = new PVector(x, y) ;
        velocity = initVel ;
        forceAccumulator = new PVector(0, 0) ;
        invMass = 1f/3f ;
    }

    // Add a force to the accumulator
    public void addForce(PVector force) {
        forceAccumulator.add(force) ;
    }

    // update position and velocity
    public void integrate() {
        // If infinite mass, we don't integrate
        if (invMass <= 0f) return ;

        // update position
        position.add(velocity) ;

        PVector resultingAcceleration = forceAccumulator.get() ;
        resultingAcceleration.mult(invMass) ;

        // update velocity
        velocity.add(resultingAcceleration) ;

        // apply damping - disabled when Drag force present
        velocity.mult(DAMPING) ;

        // Remove if off the screen (but not if off the top)
        if (position.x < 0 || position.x > MainGame.WIDTH || position.y > MainGame.HEIGHT) {
            p.getCurDuel().removeSandParticle(this);
        }

        // check if it landed on platforms
        if((position.y > Duel.FLOOR_Y && position.y < Duel.FLOOR_Y + Duel.PLATFORM_HEIGHT) &&
                ((position.x < Duel.PLATFORM_1_X + Duel.PLATFORM_WIDTH && position.x > Duel.PLATFORM_1_X) ||
                (position.x < Duel.PLATFORM_2_X + Duel.PLATFORM_WIDTH && position.x > Duel.PLATFORM_2_X))){
            velocity.y = 0;
            velocity.x = velocity.x * 0.8f; // friction on the metal
        }

        // Clear accumulator
        forceAccumulator.x = 0 ;
        forceAccumulator.y = 0 ;
    }

    public void display(){
        integrate();

        p.stroke(134, 71, 9);
        p.strokeWeight(3);
        p.point(position.x, position.y);
    }
}
