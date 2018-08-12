/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mrugames.interaction.scene;

import com.jme3.math.FastMath;
import com.mrugames.interaction.tween.Tween;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;

/**
 *
 * @author Matt Boudreau
 */
public class CloseWallTween implements Tween{
    private final float duration;
    private final Body wall;
    private final Vector2 start;
    private final Vector2 destination;
    private float elapsed;
    
    public CloseWallTween(Body wall, Vector2 destination, float dur){
        this.wall = wall;
        this.destination = destination;
        this.duration = dur;
        start = wall.getTransform().getTranslation();
    }

    @Override
    public boolean evaluate(float tpf) {
        elapsed = FastMath.clamp(elapsed+tpf, 0, duration);
        
        float percent = elapsed/duration;
        double xdist = destination.x - start.x;
        double ydist = destination.y - start.y;
        double xdif = xdist*percent;
        double ydif = ydist*percent;
        wall.getTransform().setTranslation(xdif+start.x, ydif+start.y);
        
        return elapsed >= duration;
    }
    
}
