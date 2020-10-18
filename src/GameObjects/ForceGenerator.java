package GameObjects;

import processing.core.PVector;

/*
 * A force generator can be asked to add forces to
 * one or more particles.
 * Adapted from https://studres.cs.st-andrews.ac.uk/CS4303/Lectures/CS4303_1819_Physics/BungeeSketch/ForceGenerator.pde
 * By Prof Ian Miguel
 */
abstract class ForceGenerator {
    abstract void updateForce(SandParticle p) ;
    abstract PVector getAsVector();
}