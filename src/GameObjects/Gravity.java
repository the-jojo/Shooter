package GameObjects;

import processing.core.PVector;

public class Gravity extends ForceGenerator{

    private float gravityForce;

    public Gravity(float gravityForce){
        this.gravityForce = gravityForce;
    }

    public void updateForce(SandParticle particle){
        particle.addForce(getAsVector());
    }

    public PVector getAsVector(){
        return new PVector(0, gravityForce);
    }
}
