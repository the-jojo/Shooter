package Game;

import Screens.Background;
import Screens.Intro;
import Screens.Story;
import processing.core.PApplet;
import processing.core.PFont;

public class MainGame extends PApplet {
    // screen config
    public static final int HEIGHT = 450, WIDTH = 900;
    public static final int MAX_GAMES = 5;
    // fonts
    public static PFont font_norm;
    public static PFont font_bold;
    // duel elements
    private Intro intro;
    private Story story;
    private Background bg;
    private Duel duel;

    /**
     * Setup called first by processing.
     */
    public void setup(){
        font_norm = createFont("Courier",19);
        font_bold = createFont("Courier Bold",19);
        surface.setTitle("Shooter");
    }

    /**
     * Settings called second by processing.
     */
    public void settings() {
        intro = new Intro(this);
        story = new Story(this);
        bg = new Background(this);

        size(WIDTH, HEIGHT) ;
    }

    /**
     * Called 1x per frame.
     */
    public void draw() {
        // always show background
        bg.display();
        switch (GameState.get().getState()){
            case INTRO:
                intro.update(mouseX, mouseY);
                intro.display();
                break;
            case STORY:
                story.update(mouseX, mouseY);
                story.display(GameState.get().getStoryPos());
                break;
            default:
                displayGameStats();
                duel.display();
                break;

        }

    }

    /**
     * Shows the # of games played with dots and the scores
     */
    private void displayGameStats(){
        // games played
        strokeWeight(9);
        for(int i = 1; i <= MAX_GAMES; i++){
            if(i <= GameState.get().getGamesPlayed()){
                // orange
                stroke(255, 102, 0);
            }else {
                // grey
                stroke(191, 191, 191);
            }
            point(15 * i, 10);
        }

        // scores
        textFont(font_bold);
        textSize(34);
        fill(255);
        textAlign(LEFT, CENTER);
        text(GameState.get().getPoints_p1(), 20, 35);
        textAlign(RIGHT, CENTER);
        text(GameState.get().getPoints_p2(), WIDTH - 20, 35);

    }

    /**
     * Fired when a key is pressed.
     */
    public void keyPressed(){
        switch (GameState.get().getState()){
            case INTRO:
                intro.keyPressed(key);
                break;
            case STORY:
                story.keyPressed(key);
                break;
            default:
                //
                duel.keyPressed(key);
                break;
        }
    }

    /**
     * Fired when a mouse is pressed.
     */
    public void mousePressed(){
        switch (GameState.get().getState()){
            case INTRO:
                intro.mousePressed();
                break;
            case STORY:
                story.mousePressed();
                break;
            default:
                //
                break;
        }
    }

    /**
     * Called when the "continue" button in the story screen is clicked
     */
    public void progressFromStory(){
        if(GameState.get().getGamesPlayed() < MAX_GAMES){
            // progress from "introduction story"
            GameState.get().startGame();
        }else{
            // progress from end story
            GameState.get().resetGame();
        }
    }

    /**
     * Starts a player vs player game
     */
    public void startGamePVP(){
        duel = new Duel(this, false);
    }

    /**
     * Starts a player vs AI  game
     */
    public void startGamePVA(){
        duel = new Duel(this, true);
    }

    /**
     * Returns current Duel
     * @return duel
     */
    public Duel getCurDuel(){
        return duel;
    }

    /**
     * Runs the Game
     */
    public static void main(String[] args) {
        PApplet.main("Game.MainGame");
    }
}
