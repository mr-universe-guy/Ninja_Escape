/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mrugames;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.scene.Node;

/**
 *
 * @author Matt Boudreau
 */
public class MenuState extends BaseAppState implements ActionListener{
    public static final String START_GAME = "START_GAME";
    private final Node menuNode = new Node("Main Menu");
    private AudioState audio;

    @Override
    protected void initialize(Application app) {
        audio = getState(AudioState.class);
        //attach menu items to the menu node
        int height = app.getCamera().getHeight();
        int width = app.getCamera().getWidth();
        AssetManager am = app.getAssetManager();
        BitmapFont fnt = am.loadFont(LD42.FONT_DEFAULT);
        BitmapText text = fnt.createLabel(
                "Press enter to start\n"
                +"Use the arrow keys to move left/right and jump\n"
                +"Wall jump to reach high places\n"
                +"Press the button before the time runs out\n"
                + "The higher your streak, the less time you have!"
        );
        text.setSize(24);
        text.setLocalTranslation((width/2)-(text.getLineWidth()/2f),
                (height/2)+(text.getHeight()/2f), 0);
        menuNode.attachChild(text);
    }
    
    public void startGame(){
        GameState gs = new GameState();
        getStateManager().attach(gs);
        setEnabled(false);
        audio.playClip(AudioState.BUTTON);
    }

    @Override
    protected void cleanup(Application app) {
        InputManager im = app.getInputManager();
        im.deleteMapping(START_GAME);
    }

    @Override
    protected void onEnable() {
        LD42 app = (LD42)getApplication();
        app.getGuiNode().attachChild(menuNode);
        InputManager im = app.getInputManager();
        im.addListener(this, START_GAME);
    }

    @Override
    protected void onDisable() {
        menuNode.removeFromParent();
        getApplication().getInputManager().removeListener(this);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if(name.equals(START_GAME) && isPressed){
            getApplication().enqueue(()->{
                startGame();
            });
        }
    }
}
