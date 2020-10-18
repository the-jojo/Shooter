package Screens;

import Game.*;

import processing.core.PImage;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Background {
    private class Star{
        public int x;
        public int y;
        public Color col;
        public float twinkleFactor;
        public void display(){
            // random chance to twinkle
            if(p.random(0f,1f) > twinkleFactor){
                p.stroke(col.getRGB());
                p.strokeWeight(2);
                p.point(x, y);
            }
        }
    }

    // coordinates
    private static final int TOP_BAR_WIDTH = 200/2,
                             TOP_BAR_HEIGHT = 40/2;

    // Game elements
    private PImage img_topBar;
    private MainGame p;
    private ArrayList<Star> stars;

    public Background(MainGame pa){
        p = pa;
        stars = makeStars(400);
        img_topBar = p.loadImage("top.png", "png");
    }

    public void display(){
        // background
        p.background(0, 0, 26);

        // stars
        for(Star s: stars){
            s.display();
        }

        // top bar if in game
        if(GameState.get().inGame()){
            p.image(img_topBar, 0, 0, TOP_BAR_WIDTH, TOP_BAR_HEIGHT);
        }

    }

    /**
     * Generates some random stars with random yellow colour and random twinkleFactor.
     * @param n - number of stars
     * @return List of random stars
     */
    private ArrayList<Star> makeStars(int n){
        ArrayList<Star> res = new ArrayList<Star>();
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            Star s = new Star();
            s.x = rand.nextInt(MainGame.WIDTH);
            s.y = rand.nextInt(MainGame.HEIGHT);
            s.col = new Color(255, 255, Math.round(p.random(80, 255)));
            s.twinkleFactor = p.random(0, 0.025f);
            res.add(s);
        }
        return res;
    }
}
