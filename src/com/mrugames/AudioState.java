/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mrugames;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Matt Boudreau
 */
public class AudioState extends BaseAppState{
    public static final String JUMP = "JUMP";
    public static final String DEAD = "DEAD";
    public static final String BUTTON = "BUTTON";
    public static final String WALL = "WALL";
    private Map<String, AudioNode> clips = new HashMap<>();

    @Override
    protected void initialize(Application app) {
        AssetManager am = app.getAssetManager();
        Node root = ((LD42)app).getRootNode();
        AudioNode jumpNode = new AudioNode(am, "com/mrugames/assets/sound/Jump.wav",
                AudioData.DataType.Buffer);
        clips.put(JUMP, jumpNode);
        root.attachChild(jumpNode);
        AudioNode buttonNode = new AudioNode(am, "com/mrugames/assets/sound/ButtonPress.wav",
                AudioData.DataType.Buffer);
        clips.put(BUTTON, buttonNode);
        root.attachChild(buttonNode);
        AudioNode deadNode = new AudioNode(am, "com/mrugames/assets/sound/Dead.wav",
                AudioData.DataType.Buffer);
        clips.put(DEAD, deadNode);
        root.attachChild(deadNode);
        AudioNode wallNode = new AudioNode(am, "com/mrugames/assets/sound/WallMove.wav",
                AudioData.DataType.Buffer);
        clips.put(WALL, wallNode);
        root.attachChild(wallNode);
    }
    
    public void playClip(String clip){
        AudioNode node = clips.get(clip);
        node.playInstance();
    }

    @Override
    protected void cleanup(Application app) {
        for(AudioNode n : clips.values()){
            n.removeFromParent();
        }
    }

    @Override
    protected void onEnable() {
        
    }

    @Override
    protected void onDisable() {
        
    }
    
}
