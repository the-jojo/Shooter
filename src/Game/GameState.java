package Game;

import java.util.ArrayList;

/**
 * Singelton class holding global game state and its history
 */
public final class GameState {
    public static enum State {
        PLAYER_1,
        PLAYER_2,
        FIRING,
        EXPLODING,
        INTRO,
        STORY
    }

    private static final GameState gameState = new GameState();

    private ArrayList<State> history = new ArrayList<State>(); // last entry represents current state
    private int points_p1 = 0;
    private int points_p2 = 0;
    private int lastWin = -1;
    private int storyPos = 0;
    private int ai_Tolerance = 15;

    // always start with Intro
    private GameState(){
        history.add(State.INTRO);
    }

    public static GameState get(){
        return gameState;
    }

    /**
     * IF player *player* hits other player
     * @param player the player who scored (1 or 2)
     */
    public void hitBy(int player){
        // check player number is right
        if(player < 1 || player > 2){
            throw new IllegalArgumentException("Player number '" + player + "' unknown.");
        }

        // add score
        if(player == 1){
            points_p1++;
        }else if(player == 2){
            points_p2++;
        }
    }

    /**
     * Resets the game.
     */
    public void resetGame(){
        points_p1 = 0;
        points_p2 = 0;
        storyPos = 0;
        ai_Tolerance = 15;
        history = new ArrayList<State>();
        history.add(State.INTRO);
        System.out.println("@INTRO");
    }

    public int getGamesPlayed(){return points_p1 + points_p2;}

    public State getState(){
        return history.get(history.size()-1);
    }

    public void setExplosion(){
        history.add(State.EXPLODING);
        System.out.println("@EXPLODING");
    }

    public void setFiring(){
        history.add(State.FIRING);System.out.println("@FIRING");
    }

    public void setIntro(){
        history.add(State.INTRO);System.out.println("@INTRO");
    }

    public void setStory(){
        history.add(State.STORY);System.out.println("@STORY");
    }

    public void startGame(){
        history.add(State.PLAYER_1);System.out.println("@PLAYER1");
    }

    public boolean hasFired(){
        // loop backwards and see if firing happened before the last player state
        for(int i = history.size()-1; i >= 0; i--){
            if(history.get(i) == State.PLAYER_1 || history.get(i) == State.PLAYER_2){
                return false;
            }
            if(history.get(i) == State.FIRING){
                return true;
            }
        }
        return false;
    }

    public void nextPlayer(){
        // check if game is over
        if(getGamesPlayed() >= MainGame.MAX_GAMES){
            System.out.println("DONE");
            if(points_p1 >= points_p2){
                // p1 wins
                lastWin = 1;
                storyPos = 1;
            }else{
                // p2 wins
                lastWin = 2;
                storyPos = 2;
            }
            if(wasStory()){
                setStory();
            }else{
                resetGame();
            }

            return;
        }

        int p = getCurPlayer();
        // do not progress if no Firing happened
        if(p >= 0  && !hasFired())
            return;

        if(p == 1){
            history.add(State.PLAYER_2);
            System.out.println("@PLAYER2");
            ai_Tolerance = ai_Tolerance - 3;
        }else if(p == 2){
            history.add(State.PLAYER_1);
            System.out.println("@PLAYER1");
            ai_Tolerance = ai_Tolerance - 3;
        }else{
            history.add(State.PLAYER_1);
            System.out.println("@PLAYER1");
        }
    }

    public int getLastWinner(){
        return lastWin;
    }

    public int getCurPlayer(){
        // loop backwards and find the last player state
        for(int i = history.size()-1; i >= 0; i--){
            if(history.get(i) == State.PLAYER_1){
                return 1;
            }
            if(history.get(i) == State.PLAYER_2){
                return 2;
            }
        }
        return -1;
    }

    public boolean wasStory(){
        // loop backwards and find the last player state
        for(int i = history.size()-1; i >= 0; i--){
            if(history.get(i) == State.INTRO){
                return false;
            }
            if(history.get(i) == State.STORY){
                return true;
            }
        }
        return false;
    }

    public int getAi_Tolerance(){return ai_Tolerance;}

    public boolean isFiring(){
        return getState() == State.FIRING;
    }

    public boolean isExploding(){
        return getState() == State.EXPLODING;
    }

    public boolean isIntro(){
        return getState() == State.INTRO;
    }

    public boolean isPlaying(){
        return getState() == State.PLAYER_1 || getState() == State.PLAYER_2;
    }

    public boolean inGame(){
        return getState() != State.INTRO && getState() != State.STORY;
    }

    public boolean isPlayer1(){
        return getState() == State.PLAYER_1;
    }

    public boolean isPlayer2(){
        return getState() == State.PLAYER_2;
    }

    public int getPoints_p1(){return points_p1;}
    public int getPoints_p2(){return points_p2;}

    public int getStoryPos(){return storyPos;}
}
