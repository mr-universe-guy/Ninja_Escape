/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mrugames;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.system.AppSettings;
import com.mrugames.interaction.player.PlayerState;
import com.mrugames.interaction.tween.TweenState;

/**
 *
 * @author Matt Boudreau
 */
public class LD42 extends SimpleApplication{
    public static final String FONT_DEFAULT = "Interface/Fonts/Default.fnt";
    public static final String FONT_CONSOLE = "Interface/Fonts/Console.fnt";
    public static Material DEBUG_MAT;
    
    public LD42(){
        super(new TweenState(),
                new AudioState(),
                new MenuState(),
                new ScreenshotAppState(""));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LD42 app = new LD42();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Ninja Escape");
        settings.setSettingsDialogImage("com/mrugames/assets/NinjaEscape.png");
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        DEBUG_MAT = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        DEBUG_MAT.getAdditionalRenderState().setWireframe(true);
        System.out.println("Hello LD42");
        //register all mappings here
        inputManager.addMapping(MenuState.START_GAME, new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping(GameOverState.RESTART, new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping(GameState.CLOSE_WALL, new KeyTrigger(KeyInput.KEY_INSERT));
        inputManager.addMapping(GameState.END_GAME, new KeyTrigger(KeyInput.KEY_END));
        inputManager.addMapping(PlayerState.MOVE_LEFT, new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping(PlayerState.MOVE_RIGHT, new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping(PlayerState.JUMP, new KeyTrigger(KeyInput.KEY_UP));
    }
    
}
