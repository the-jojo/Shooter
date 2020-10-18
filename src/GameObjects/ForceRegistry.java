package GameObjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds all the force generators and the particles they apply to
 * Adapted from https://studres.cs.st-andrews.ac.uk/CS4303/Lectures/CS4303_1819_Physics/BungeeSketch/ForceRegistry.pde
 * By Prof Ian Miguel
 */
public class ForceRegistry {

    /**
     * Keeps track of multiple force generators and the particle
     * they apply to.
     */
    private class ForceRegistration {
        public final SandParticle particle ;
        public final ArrayList<ForceGenerator> forceGenerators ;
        ForceRegistration(SandParticle p, ForceGenerator fg) {
            particle = p ;
            forceGenerators = new ArrayList<>();
            forceGenerators.add(fg);
        }
        public void add(ForceGenerator fg){
            forceGenerators.add(fg);
        }
    }

    // Holds the list of registrations
    private HashMap<SandParticle, ForceRegistration> registrations =
            new HashMap<>() ;

    /**
     * Register the given force to apply to the given particle
     */
    public void add(SandParticle p, ForceGenerator fg) {
        ForceRegistration reg = registrations.get(p);
        if(reg != null){
            reg.add(fg);
        }else{
            registrations.put(p, new ForceRegistration(p, fg)) ;
        }
    }

    public void remove(SandParticle p){
        registrations.remove(p);
    }

    /**
     * Clear all registrations from the registry
     */
    public void clear() {
        registrations.clear() ;
    }

    /**
     * Calls all force generators to update the forces of their
     *  corresponding particles.
     */
    public void updateForces() {
        for(Map.Entry<SandParticle, ForceRegistration> entry : registrations.entrySet()){
            SandParticle p = entry.getKey();
            ForceRegistration fr = entry.getValue();
            for(ForceGenerator fg : fr.forceGenerators){
                fg.updateForce(p);
            }
        }
    }
}

