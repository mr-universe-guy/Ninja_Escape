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
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.mrugames.AudioState;
import com.mrugames.GameState;
import com.mrugames.LD42;
import com.mrugames.interaction.player.PlayerState;
import com.mrugames.physics.BodyControl;
import com.mrugames.physics.PhysicsState;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Step;
import org.dyn4j.dynamics.StepListener;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

/**
 * The BG is a large grid made up of squares where buttons and hazards spawn. Every
 * time the button is pressed it resets the timer and re-builds the bg. Every time
 * the timer reaches 0 one wall at random is closed and the grid is reduced and the
 * bg is rebuilt
 * @author Matt Boudreau
 */
public class BGState extends BaseAppState implements StepListener{
    private static final Random RANDOM = new Random();
    private boolean buttonPressed = false;
    private AudioState audio;
    private int height;
    private Body button;
    private List<Body> platforms = new ArrayList<>();

    @Override
    protected void initialize(Application app) {
        audio = getState(AudioState.class);
    }

    @Override
    protected void cleanup(Application app) {
        for(Body b : platforms){
            Spatial spat = (Spatial)b.getUserData();
            spat.removeFromParent();
        }
        ((Spatial)button.getUserData()).removeFromParent();
    }
    
    private void spawnButton(Vector2 pos){
        buttonPressed = false;
        World world = getState(PhysicsState.class).getWorld();
        
        Circle c = new Circle(0.25);
        button = new Body();
        BodyFixture buttonFixture = button.addFixture(c);
        buttonFixture.setSensor(true);
        buttonFixture.setFilter(new CategoryFilter(PhysicsState.GROUND_GROUP, PhysicsState.PLAYER_GROUP));
        world.addBody(button);
        button.getTransform().setTranslation(pos);
        
        //spatial
        Mesh sphere = new Sphere(8,8,0.25f);
        Spatial buttonGeo = new Geometry("Button", sphere);
        buttonGeo.setMaterial(LD42.DEBUG_MAT);
        buttonGeo.addControl(new BodyControl(button));
        ((LD42)getApplication()).getRootNode().attachChild(buttonGeo);
        button.setUserData(buttonGeo);
//        System.out.println("Button spawned at "+pos);
    }
    
    private void spawnPlatform(Vector2 pos){
        World world = getState(PhysicsState.class).getWorld();
        
        Rectangle rec = new Rectangle(1,1);
        Body platform = new Body();
        BodyFixture platformFixture = platform.addFixture(rec);
        platformFixture.setFilter(new CategoryFilter(PhysicsState.GROUND_GROUP, PhysicsState.PLAYER_GROUP));
        world.addBody(platform);
        platform.getTransform().setTranslation(pos);
        
        //spatial
        Mesh box = new Box(0.5f,0.5f,0);
        Spatial platGeo = new Geometry("Platform", box);
        platGeo.setMaterial(LD42.DEBUG_MAT);
        platGeo.addControl(new BodyControl(platform));
        ((LD42)getApplication()).getRootNode().attachChild(platGeo);
        platform.setUserData(platGeo);
        platforms.add(platform);
    }
    
    public void buildBG(){
        World world = getState(PhysicsState.class).getWorld();
        //clear old button and destroy old platforms
        if(button != null){
            //destroy old button and add a new one
            world.removeBody(button);
            Spatial buttonGeo = (Spatial)button.getUserData();
            buttonGeo.removeFromParent();
            buttonGeo.removeControl(BodyControl.class);
        }
        for(Body b : platforms){
            world.removeBody(b);
            Spatial spat = (Spatial)b.getUserData();
            spat.removeFromParent();
        }
        platforms.clear();
        detectLimits();
        //get the player transform and spawn the button opposite the player
        Vector2 playerPos = getState(PlayerState.class).getPlayerPosition();
        double x;
        if(playerPos.x < 0){
            x = getState(WallState.class).getWallPosition(WallState.RIGHT_WALL).x-1;
        } else{
            x = getState(WallState.class).getWallPosition(WallState.LEFT_WALL).x+1;
        }
        double y = -4+RANDOM.nextInt(height+4);
        spawnButton(new Vector2(x,y));
        //spawn platforms to allow access to high buttons
        if(y+5 > 2){
//            System.out.println("Adding platforms");
            int numPlatforms = ((int)y)/2;
            for(int i=0; i<=numPlatforms; i++){
                double platX;
                if(x <0){
                    platX = x+2;
                } else{
                    platX = x-2;
                }
                Vector2 pos = new Vector2(platX, (i*2)-2);
                spawnPlatform(pos);
            }
        }
    }
    
    public boolean buttonSpawned(){
        return button != null;
    }
    
    private void detectLimits(){
        WallState ws = getState(WallState.class);
        height = (int)ws.getWallPosition(WallState.CEILING).y;
    }

    @Override
    protected void onEnable() {
        getState(PhysicsState.class).getWorld().addListener(this);
    }

    @Override
    protected void onDisable() {
        getState(PhysicsState.class).getWorld().removeListener(this);
    }

    @Override
    public void begin(Step step, World world) {
        
    }

    @Override
    public void updatePerformed(Step step, World world) {
        
    }

    @Override
    public void postSolve(Step step, World world) {
        
    }

    @Override
    public void end(Step step, World world) {
        if(buttonPressed || button == null) return;
        List<ContactPoint> contacts = new ArrayList<>();
        contacts = button.getContacts(true);
        if(!contacts.isEmpty()){
            buttonPressed = true;
//            System.out.println("Button has been pressed");
            GameState game = getState(GameState.class);
            game.setGameAction(GameState.GameAction.BUILDBG);
            game.score();
            audio.playClip(AudioState.BUTTON);
        }
    }
}
