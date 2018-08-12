/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mrugames.interaction.tween;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Matt Boudreau
 */
public class TweenState extends BaseAppState{
    private List<Tween> tweens = new ArrayList<>();

    @Override
    protected void initialize(Application app) {
        
    }

    @Override
    public void update(float tpf) {
        Iterator<Tween> it = tweens.iterator();
        while(it.hasNext()){
            Tween tween = it.next();
            if(tween.evaluate(tpf)) it.remove();
        }
    }

    @Override
    protected void cleanup(Application app) {
        tweens.clear();
    }

    @Override
    protected void onEnable() {
        
    }

    @Override
    protected void onDisable() {
        
    }
    
    public void addTween(Tween tween){
        tweens.add(tween);
    }
}
