/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mrugames.interaction.scene;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.mrugames.AudioState;
import com.mrugames.LD42;
import com.mrugames.interaction.tween.TweenState;
import com.mrugames.physics.BodyControl;
import com.mrugames.physics.PhysicsState;
import java.util.Random;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

/**
 * Handles the wall logic, indicating which wall is next to move and managing the
 * walls physics parts
 * @author Matt Boudreau
 */
public class WallState extends BaseAppState{
    public static final float WALL_SPEED = 2f;
    private static final Random random = new Random();
    public static final int LEFT_WALL = 0;
    public static final int RIGHT_WALL = 1;
    public static final int CEILING = 2;
    private final int maxDistance = 6;
    private final int maxCeilDist = 5;
    private AudioState audio;
    private Body left_Wall, right_Wall, ceiling, floor;
    public int nextWall = 0;
    
    @Override
    protected void initialize(Application app) {
        audio = getState(AudioState.class);
        World world = getState(PhysicsState.class).getWorld();
        //we need 2 bodies, 1 for each wall and some spatials to visualize them
        Node rootNode = ((LD42)app).getRootNode();
        left_Wall = createWall();
        rootNode.attachChild(createWallGeometry(left_Wall));
        world.addBody(left_Wall);
        left_Wall.translate(-maxDistance, 0);
        
        right_Wall = createWall();
        rootNode.attachChild(createWallGeometry(right_Wall));
        world.addBody(right_Wall);
        right_Wall.translate(maxDistance,0);
        
        ceiling = createCeiling();
        rootNode.attachChild(createWallGeometry(ceiling));
        world.addBody(ceiling);
        ceiling.translate(0, maxCeilDist);
        
        floor = createCeiling();
        rootNode.attachChild(createWallGeometry(floor));
        world.addBody(floor);
        floor.translate(0, -maxCeilDist);
    }
    
    private Spatial createWallGeometry(Body wall){
        Rectangle rec = (Rectangle)wall.getFixture(0).getShape();
        Mesh m = new Box((float)rec.getWidth()/2f, (float)rec.getHeight()/2f, 0);
        Geometry wallGeo = new Geometry("Wall", m);
        wallGeo.setMaterial(LD42.DEBUG_MAT);
        wallGeo.addControl(new BodyControl(wall));
        wall.setUserData(wallGeo);
        return wallGeo;
    }
    
    private Body createCeiling(){
        Rectangle rec = new Rectangle(12,2);
        Body ceiling = new Body();
        BodyFixture ceilingFixture = ceiling.addFixture(rec);
        ceilingFixture.setFilter(new CategoryFilter(PhysicsState.GROUND_GROUP, Integer.MAX_VALUE));
        ceiling.setMass(MassType.INFINITE);
        return ceiling;
    }
    
    private Body createWall(){
        Rectangle rec = new Rectangle(2,12);
        Body wall = new Body();
        BodyFixture wallFixture = wall.addFixture(rec);
        wallFixture.setFilter(new CategoryFilter(PhysicsState.GROUND_GROUP, Integer.MAX_VALUE));
        wall.setMassType(MassType.INFINITE);
        return wall;
    }
    
    public int pickNextWall(){
        nextWall = random.nextInt(3);
        return nextWall;
    }
    
    public void moveNextWall(){
        audio.playClip(AudioState.WALL);
        Body wall;
        Vector2 destination;
        double curPos;
        int destPos;
        switch(nextWall){
            case LEFT_WALL: wall = left_Wall;
                curPos = left_Wall.getTransform().getTranslation().x;
                destPos = (int)curPos+1;
                destination = new Vector2(destPos, 0);
            break;
            case RIGHT_WALL: wall = right_Wall;
                curPos = right_Wall.getTransform().getTranslation().x;
                destPos = (int)curPos-1;
                destination = new Vector2(destPos, 0);
            break;
            case CEILING: wall = ceiling;
                curPos = ceiling.getTransform().getTranslation().y;
                destPos = (int)curPos-1;
                destination = new Vector2(0, destPos);
            break;
            default: wall = null;
                destination = null;
        }
        
        getState(TweenState.class).addTween(new CloseWallTween(wall, destination, WALL_SPEED));
    }
    
    public Vector2 getWallPosition(int wall){
        switch(wall){
            case LEFT_WALL:
                return left_Wall.getTransform().getTranslation();
            case RIGHT_WALL:
                return right_Wall.getTransform().getTranslation();
            case CEILING:
                return ceiling.getTransform().getTranslation();
        }
        return null;
    }

    @Override
    protected void cleanup(Application app) {
        ((Spatial)left_Wall.getUserData()).removeFromParent();
        ((Spatial)right_Wall.getUserData()).removeFromParent();
        ((Spatial)ceiling.getUserData()).removeFromParent();
        ((Spatial)floor.getUserData()).removeFromParent();
    }

    @Override
    protected void onEnable() {
        
    }

    @Override
    protected void onDisable() {
        
    }
    
}
