/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mrugames.physics;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

/**
 * Binds the spatial the the physics bodies transform
 * @author Matt Boudreau
 */
public class BodyControl extends AbstractControl{
    public static final String SPATIAL_DATA = "SPATIAL_DATA";
    private Body body;
    
    public BodyControl(Body body){
        this.body = body;
    }
    
    public void setBody(Body body){
        this.body = body;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(body != null){
            Transform trans = body.getTransform();
            Vector2 pos = trans.getTranslation();
            body.setActive(true);
            Vector3f spos = spatial.getLocalTranslation();
            spatial.setLocalTranslation((float)pos.x, (float)pos.y, spos.z);
            float rot = (float)trans.getRotation();
            spatial.setLocalRotation(new Quaternion().fromAngleAxis(rot, Vector3f.UNIT_Z));
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
}
