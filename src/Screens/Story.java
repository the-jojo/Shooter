package Screens;

import Game.*;

public class Story {
    protected final MainGame p;
    private boolean btn_continue_over = false;

    private static final int TEXT_X = MainGame.WIDTH/2,
                             TEXT_Y = 50;
    private static final int BTN_WIDTH = 160,
                             BTN_HEIGHT = 30;
    private static final int BTN_X = MainGame.WIDTH/2 - BTN_WIDTH/2,
                             BTN_Y = MainGame.HEIGHT - BTN_HEIGHT - 20;

    private static final String[] STORIES = {
                    "4018 AD - Humanity has conquered the milky way, harnessed\n" +
                    "suns and travelled through dimensions.                   \n\n" +
                    "The galaxies are divided between factions. Economies are \n" +
                    "run by slave labour.                                     \n\n" +
                    "Once a year one slave from each faction is selected to   \n" +
                    "fight, beamed to alien worlds while the rich bet planets \n" +
                    "on their victories.                                      \n\n" +
            "You must win at least 3/5 games or your faction master will execute you.",

                    "\nYou won.\n" +
                    "\nYour victory provoked a row between the faction masters and\n" +
                    "your home planet was eradicated by accident.",
                    "\nYou lost.\n" +
                    "Your home will be burnt with your family inside.\n" +
                    "Better luck next time?",
    };


    public Story(MainGame pa){
        this.p = pa;
    }

    public void display(int storyNu){
        draw_text(storyNu);

        draw_btn();
    }

    private void draw_text(int n){
        p.textFont(MainGame.font_norm);
        p.textSize(18);
        p.fill(255);
        p.textAlign(p.CENTER);
        p.text(STORIES[n], TEXT_X, TEXT_Y);
    }

    private void draw_btn(){
        if (btn_continue_over) {
            p.fill(255, 153, 255);
            p.strokeWeight(2);
        } else {
            p.fill(153, 102, 255);
            p.strokeWeight(0.1f);
        }
        p.stroke(255);
        p.rect(BTN_X, BTN_Y, BTN_WIDTH, BTN_HEIGHT);
        p.textSize(16);
        p.fill(255);
        p.textAlign(p.CENTER, p.CENTER);
        p.text("Continue", BTN_X + BTN_WIDTH/2, BTN_Y + BTN_HEIGHT/2);
    }

    public void update(int x, int y) {
        if ( overRect(BTN_X, BTN_Y, BTN_WIDTH, BTN_HEIGHT) ) {
            btn_continue_over = true;
        } else {
            btn_continue_over = false;
        }
    }

    public void keyPressed(char key){
        if(key == ' '){
            //p.startGame();
        }
    }

    public void mousePressed(){
        if(btn_continue_over){
            p.progressFromStory();
        }
    }

    private boolean overRect(int x, int y, int width, int height)  {
        if (p.mouseX >= x && p.mouseX <= x+width &&
                p.mouseY >= y && p.mouseY <= y+height) {
            return true;
        } else {
            return false;
        }
    }
}
