/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mrugames.physics;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.mrugames.LD42;
import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

/**
 *
 * @author Matt Boudreau
 */
public class PhysicsState extends BaseAppState{
    public static final int GROUND_GROUP = 1<<0;
    public static final int PLAYER_GROUP = 1<<1;
    public static final String USER_BODY="User_Body";
    private World world;

    @Override
    protected void initialize(Application app) {
        world = new World(new AxisAlignedBounds(100,100));
        world.getSettings().setStepFrequency(1.0/60.0);
        world.setGravity(new Vector2(0,-10));
        
//        Rectangle rec = new Rectangle(12,2);
//        Body floor = new Body();
//        BodyFixture fix = floor.addFixture(rec);
//        fix.setFilter(new CategoryFilter(GROUND_GROUP, Integer.MAX_VALUE));
//        floor.setMassType(MassType.INFINITE);
//        floor.translate(0, -5);
//        Mesh q = new Box((float)rec.getWidth()/2, (float)rec.getHeight()/2,0);
//        Geometry debug = new Geometry("Bounds", q);
//        debug.addControl(new BodyControl(floor));
//        debug.setMaterial(LD42.DEBUG_MAT);
//        ((LD42)app).getRootNode().attachChild(debug);
//        world.addBody(floor);
    }

    @Override
    public void update(float tpf) {
        if(world.update(tpf)){
            
        }        
    }

    @Override
    protected void cleanup(Application app) {
        world.removeAllJoints();
        world.removeAllBodies();
        world.removeAllListeners();
    }

    @Override
    protected void onEnable() {
        
    }

    @Override
    protected void onDisable() {
        
    }

    public World getWorld() {
        return world;
    }
    
}
