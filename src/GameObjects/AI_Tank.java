package GameObjects;

import Game.*;

public class AI_Tank extends Tank {

    private enum AiState {
        DECIDING_MOVE,
        MOVING,
        FINDING_BEST,
        ADJUSTING,
        FIRING
    }

    private static final int AI_ITER = 20;

    private AiState aiState;

    private float desired_angle;
    private float desired_force;
    private int   desired_xPos;

    public AI_Tank(MainGame pa, int startx, int starty, float startAngle, float startForce){
        super(pa, startx, starty, startAngle, startForce);
        aiState = AiState.DECIDING_MOVE;
    }

    public void display(){
        // do not show inactive tank
        if(!active)
            return;

        if(exploding < 0){
            // not exploding

            // is it my turn?
            if(GameState.get().getState() == GameState.State.PLAYER_2){
                switch (aiState){
                    case DECIDING_MOVE:
                        decideMove();
                        break;
                    case MOVING:
                        move();
                        break;
                    case FINDING_BEST:
                        findBest();
                        break;
                    case ADJUSTING:
                        adjust();
                        break;
                    case FIRING:
                        p.getCurDuel().fire_p2();
                        aiState = AiState.DECIDING_MOVE;
                        break;
                }
            }

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

    private void decideMove(){
        float leftBound = Math.max(Duel.PLATFORM_2_X, position.x - 30);
        float rightBound = Math.min(Duel.PLATFORM_2_X + Duel.PLATFORM_WIDTH, position.x + 30);
        desired_xPos = Math.round(p.random(leftBound, rightBound));
        aiState = AiState.MOVING;
    }

    private void move(){
        if(position.x < desired_xPos){
            move(1);
        }else if(position.x > desired_xPos){
            move(-1);
        }else{
            aiState = AiState.FINDING_BEST;
        }
    }

    private void findBest(){
        float angle = gunAngle;
        float force = gunForce;
        float bestDist = Float.MAX_VALUE;
        desired_angle = angle;
        desired_force = force;
        // loop 20x with random angles and forces to find best simulation
        for(int i = 0; i < AI_ITER; i++){
            // choose semi-random values
            force = p.random(10f, FORCE_MAX);
            angle = p.random(-p.PI, -p.PI/2);
            // run simulation on shell
            Shell s = new Shell(p, gun_x(), gun_y(), angle, force, true);
            float d = s.simulate(p.getCurDuel().getWind(), p.getCurDuel().getGravity(), p.getCurDuel().getPlayer1Pos());
            // check min distance of shell to enemy
            if(d <= bestDist){
                desired_angle = angle;
                desired_force = force;
                bestDist = d;
            }
            // would this shell hit?
            if(d < (Shell.EXPLOSION_R + p.random(0, GameState.get().getAi_Tolerance()))){
                aiState = AiState.ADJUSTING;
                break;
            }
        }
    }

    /**
     * Adjusts the gun angle and force to the desired values.
     */
    private void adjust(){
        if(desired_force - gunForce > 1.1f){
            gunForce++;
        }else if(desired_force - gunForce < -1.1f){
            gunForce--;
        }else if(desired_angle - gunAngle > 0.06f){
            gunAngle = gunAngle + 0.05f;
        }else if(desired_angle - gunAngle < -0.06f){
            gunAngle = gunAngle - 0.05f;
        }else{
            aiState = AiState.FIRING;
        }
    }
}
