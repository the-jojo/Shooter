package GameObjects;

import processing.core.PVector;

public class Wind extends ForceGenerator{

    private float windForce;

    public Wind(float windForce){
        this.windForce = windForce;
    }

    public void updateForce(SandParticle particle){
        particle.addForce(getAsVector());
    }

    public PVector getAsVector(){
        return new PVector(windForce, 0);
    }
}
