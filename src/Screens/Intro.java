package Screens;

import Game.*;

public class Intro {
    private final MainGame p;

    // is mouse hovering over buttons?
    private boolean btn_start_pvp_over = false;
    private boolean btn_start_pva_over = false;

    // coordinates for texts and buttons
    private static final int TITLE_Y = 60,
                             TITLE_X = MainGame.WIDTH/2;
    private static final int BTN_START_PVP_WIDTH  = 160,
                             BTN_START_PVP_HEIGHT = 30;
    private static final int BTN_START_PVP_X = MainGame.WIDTH/2 - BTN_START_PVP_WIDTH/2,
                             BTN_START_PVP_Y = TITLE_Y + 60;
    private static final int BTN_START_PVA_WIDTH  = 160,
                             BTN_START_PVA_HEIGHT = 30;
    private static final int BTN_START_PVA_X = MainGame.WIDTH/2 - BTN_START_PVA_WIDTH/2,
                             BTN_START_PVA_Y = BTN_START_PVP_Y + BTN_START_PVP_HEIGHT + 60;
    private static final int INSTR_Y = BTN_START_PVA_Y + BTN_START_PVA_HEIGHT + 100,
                             INSTR_X = MainGame.WIDTH/2;

    public Intro(MainGame pa){
        this.p = pa;
    }

    /**
     * Display shows the intro screen.
     */
    public void display(){
        draw_title();

        draw_btn_start();
        draw_btn_start_2();
        draw_Instructions();
    }

    private void draw_title(){
        p.textFont(MainGame.font_norm);
        p.textSize(24);
        p.fill(255);
        p.textAlign(p.CENTER, p.CENTER);
        if(GameState.get().getLastWinner() > 0){
            p.text("Player " + GameState.get().getLastWinner() + " Won. Congratulations!", TITLE_X, TITLE_Y);
        }else {
            p.text("Welcome to Shooter", TITLE_X, TITLE_Y);
        }
    }

    private void draw_Instructions(){
        p.textFont(MainGame.font_norm);
        p.textSize(18);
        p.fill(255);
        p.textAlign(p.CENTER, p.CENTER);
        p.text( "Both players use:          \n" +
                "[a]/[d] -> Move Left/Right \n" +
                "[w]/[s] -> Adjust Gun Angle\n" +
                "[r]/[f] -> Adjust Gun Power\n" +
                "[SPACE] -> Fire Gun        ",
                INSTR_X, INSTR_Y);
    }

    private void draw_btn_start(){
        if (btn_start_pvp_over) {
            p.fill(255, 153, 255);
            p.strokeWeight(2);
        } else {
            p.fill(153, 102, 255);
            p.strokeWeight(0.1f);
        }
        p.stroke(255);
        p.rect(BTN_START_PVP_X, BTN_START_PVP_Y, BTN_START_PVP_WIDTH, BTN_START_PVP_HEIGHT);
        p.textSize(16);
        p.fill(255);
        p.textAlign(p.CENTER, p.CENTER);
        p.text("PLAY 1 v 1", BTN_START_PVP_X + BTN_START_PVP_WIDTH/2, BTN_START_PVP_Y + BTN_START_PVP_HEIGHT/2);
    }

    private void draw_btn_start_2(){
        if (btn_start_pva_over) {
            p.fill(255, 153, 255);
            p.strokeWeight(2);
        } else {
            p.fill(153, 102, 255);
            p.strokeWeight(0.1f);
        }
        p.stroke(255);
        p.rect(BTN_START_PVA_X, BTN_START_PVA_Y, BTN_START_PVA_WIDTH, BTN_START_PVA_HEIGHT);
        p.textSize(16);
        p.fill(255);
        p.textAlign(p.CENTER, p.CENTER);
        p.text("PLAY Story", BTN_START_PVA_X + BTN_START_PVA_WIDTH/2, BTN_START_PVA_Y + BTN_START_PVA_HEIGHT/2);
    }

    public void update(int x, int y) {
        if ( overRect(BTN_START_PVP_X, BTN_START_PVP_Y, BTN_START_PVP_WIDTH, BTN_START_PVP_HEIGHT) ) {
            btn_start_pvp_over = true;
        } else {
            btn_start_pvp_over = false;
        }
        if ( overRect(BTN_START_PVA_X, BTN_START_PVA_Y, BTN_START_PVA_WIDTH, BTN_START_PVA_HEIGHT) ) {
            btn_start_pva_over = true;
        } else {
            btn_start_pva_over = false;
        }
    }

    public void keyPressed(char key){
        if(key == ' '){
            //p.startGame();
        }
    }

    public void mousePressed(){
        if(btn_start_pvp_over){
            p.startGamePVP();
        }
        if(btn_start_pva_over){
            p.startGamePVA();
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
