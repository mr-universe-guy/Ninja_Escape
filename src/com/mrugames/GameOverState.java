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
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FadeFilter;
import com.mrugames.interaction.tween.NotifyTween;
import com.mrugames.interaction.tween.TweenState;

/**
 *
 * @author Matt Boudreau
 */
public class GameOverState extends BaseAppState implements ActionListener{
    public static final String RESTART = "RESTART";
    private FadeFilter fade;
    private FilterPostProcessor fpp;
    private BitmapText endText;

    @Override
    protected void initialize(Application app) {
        AssetManager am = app.getAssetManager();
        fpp = new FilterPostProcessor(am);
        app.getViewPort().addProcessor(fpp);
        FadeFilter fade = new FadeFilter();
        fpp.addFilter(fade);
        fade.fadeOut();
        
        //display score info
        int screenWidth = app.getCamera().getWidth();
        int screenHeight = app.getCamera().getHeight();
        BitmapFont fnt = am.loadFont(LD42.FONT_DEFAULT);
        endText = fnt.createLabel("Game Over\n"
                + "You scored "+getState(GameState.class).getScore()+"\n"
                + "Press enter to try again");
        endText.setLocalTranslation((screenWidth/2)-(endText.getLineWidth()/2),
                (screenHeight/2)+(endText.getHeight()/2), 0);
        endText.setSize(16);
        ((LD42)app).getGuiNode().attachChild(endText);
        //wait until fade out to detach game and register restart listener
        getState(TweenState.class).addTween(new NotifyTween(1.1f, () -> {
            getStateManager().detach(getState(GameState.class));
            InputManager im = app.getInputManager();
            
            im.addListener(this, RESTART);
        }));
    }

    @Override
    protected void cleanup(Application app) {
        app.getViewPort().removeProcessor(fpp);
        fpp.removeAllFilters();
        endText.removeFromParent();
        app.getInputManager().removeListener(this);
    }

    @Override
    protected void onEnable() {
        
    }

    @Override
    protected void onDisable() {
        
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if(name.equals(RESTART) && isPressed){
            getApplication().enqueue(()->{
                getState(MenuState.class).setEnabled(true);
                getStateManager().detach(this);
                getState(AudioState.class).playClip(AudioState.BUTTON);
            });
        }
    }
}
