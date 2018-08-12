/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mrugames;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import com.mrugames.interaction.player.PlayerState;
import com.mrugames.interaction.scene.BGState;
import com.mrugames.interaction.scene.WallState;
import com.mrugames.interaction.tween.NotifyTween;
import com.mrugames.interaction.tween.TweenState;
import com.mrugames.physics.PhysicsState;
import java.util.concurrent.TimeUnit;

/**
 * The base game state, from here all the other states are initialized and prepared
 * to be ready to play!
 * 
 * The clock is central to the game loop and thus should be handled here
 * @author Matt Boudreau
 */
public class GameState extends BaseAppState implements ActionListener{
    public static final String CLOSE_WALL = "CLOSE_WALL";
    public static final String END_GAME = "ENG_GAME";
    private GameAction state;
    private BitmapText clock, points;
    private int numSeconds = 10;
    private int streak = 0;
    private double timer = numSeconds;
    private long score = 0;

    @Override
    protected void initialize(Application app) {
//        System.out.println("Game has been initialized!");
        AppStateManager sm = app.getStateManager();
        sm.attachAll(new PhysicsState(),
                new PlayerState(),
                new WallState(),
                new BGState());
        AssetManager am = app.getAssetManager();
        BitmapFont fnt = am.loadFont(LD42.FONT_CONSOLE);
        fnt.getPage(0).getTextureParam("ColorMap").getTextureValue().setMagFilter(Texture.MagFilter.Nearest);
        int screenWidth = app.getCamera().getWidth();
        int screenHeight = app.getCamera().getHeight();
        Node gui = ((LD42)app).getGuiNode();
        
        //timer
        clock = fnt.createLabel("00:00");
        clock.setSize(32f);
        clock.setLocalTranslation((screenWidth/2)-(clock.getLineWidth()/2f),
                screenHeight-clock.getLineHeight(), 0);
        gui.attachChild(clock);
        
        //score
        points = fnt.createLabel(Long.MAX_VALUE+":Pts");
        points.setBox(new Rectangle(points.getLocalTranslation().x, points.getLocalTranslation().y,
                points.getLineWidth(), points.getLineHeight()));
        points.setAlignment(BitmapFont.Align.Right);
        points.setSize(32f);
        points.setLocalTranslation((screenWidth)-(points.getLineWidth()),
                screenHeight-points.getLineHeight(), 0);
        points.setText("0:Pnts");
        gui.attachChild(points);
        
        //input
        InputManager im = app.getInputManager();
        
        im.addListener(this, CLOSE_WALL, END_GAME);
        
        getState(TweenState.class).addTween(new NotifyTween(0.1f, ()->{
            setGameAction(GameAction.BUILDBG);
        }));
    }
    
    public void setGameAction(GameAction action){
//        System.out.println("Setting game action to "+action);
        state = action;
        switch(state){
            case BUILDBG:
                BGState bgs = getState(BGState.class);
                bgs.buildBG();
                setGameAction(GameAction.TRIAL);
                break;
            case MOVEWALL:
                streak = 0;
                numSeconds++;
                timer = 0;
                WallState ws = getState(WallState.class);
                ws.moveNextWall();
                getState(TweenState.class).addTween(new NotifyTween(WallState.WALL_SPEED, () -> {
                    ws.pickNextWall();
                    setGameAction(GameAction.BUILDBG);
                }));
                break;
            case TRIAL:
                resetTimer();
                break;
            case GAMEOVER:
                getStateManager().attach(new GameOverState());
                //pause all game states
                setEnabled(false);
                break;
        }
    }

    @Override
    public void update(float tpf) {
        if(state == GameAction.TRIAL){
            if((timer -= tpf*1000) <= 0){
                setGameAction(GameAction.MOVEWALL);
                clock.setText("00:00");
            } else{
//                double seconds = Math.abs(timer);
                int millis = (int)((timer/10)%100);
                int seconds = (int)(timer/1000);
                clock.setText(String.format("%02d", seconds)+":"+String.format("%02d", millis));
            }
        }
    }
    
    public void resetTimer(){
        timer = TimeUnit.SECONDS.toMillis(numSeconds);
    }
    
    public void score(){
        int add = 1+streak;
        if(add >= 3 && numSeconds > 0){
            numSeconds--;
        }
        score += add;
        points.setText(score+":Pnts");
//        System.out.println("Score: "+score);
        streak ++;
    }
    
    @Override
    protected void cleanup(Application app) {
        clock.removeFromParent();
        points.removeFromParent();
        AppStateManager sm = app.getStateManager();
        sm.detach(sm.getState(PhysicsState.class));
        sm.detach(sm.getState(PlayerState.class));
        sm.detach(sm.getState(WallState.class));
        sm.detach(sm.getState(BGState.class));
    }
    
    public long getScore(){
        return score;
    }

    @Override
    protected void onEnable() {
        getState(PhysicsState.class).setEnabled(true);
        getState(PlayerState.class).setEnabled(true);
        getState(WallState.class).setEnabled(true);
        getState(BGState.class).setEnabled(true);
    }

    @Override
    protected void onDisable() {
        getState(PhysicsState.class).setEnabled(false);
        getState(PlayerState.class).setEnabled(false);
        getState(WallState.class).setEnabled(false);
        getState(BGState.class).setEnabled(false);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if(name.equals(CLOSE_WALL) && isPressed){
            setGameAction(GameAction.MOVEWALL);
        }
        if(name.equals(END_GAME) && isPressed){
            setGameAction(GameAction.GAMEOVER);
        }
    }
    
    public enum GameAction{
        TRIAL,
        MOVEWALL,
        BUILDBG,
        GAMEOVER
    }
}
