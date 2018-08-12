/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mrugames.interaction.tween;

/**
 * A simple timer that will call the listener when the tween has reached it's duration
 * @author Matt Boudreau
 */
public class NotifyTween implements Tween{
    private final float duration;
    private final Runnable action;
    private float elapsed = 0;
    
    public NotifyTween(float duration, Runnable action){
        this.duration = duration;
        this.action = action;
    }

    @Override
    public boolean evaluate(float tpf) {
        if((elapsed+=tpf) >= duration){
            action.run();
            return true;
        }else{
            return false;
        }
    }
}
