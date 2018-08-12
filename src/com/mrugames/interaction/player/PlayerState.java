/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mrugames.interaction.player;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.mrugames.AudioState;
import com.mrugames.GameState;
import com.mrugames.LD42;
import com.mrugames.interaction.scene.Sprite;
import com.mrugames.physics.BodyControl;
import com.mrugames.physics.PhysicsState;
import java.util.List;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Step;
import org.dyn4j.dynamics.StepListener;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

/**
 * //keeping the player state seperate for it's complex state machine based interactions!
 * @author Matt Boudreau
 */
public class PlayerState extends BaseAppState implements ActionListener, StepListener{
    public static final String MOVE_LEFT = "LEFT_MOVEMENT";
    public static final String MOVE_RIGHT = "RIGHT_MOVEMENT";
    public static final String JUMP = "JUMP_MOVEMENT";
    public static final String CROUCH = "CROUCH_MOVEMENT";
    private final double fallGrav = 2.5;
    private final double wallGrav = 0.2;
    private final double jumpForce = 5;
    private final double playerSpeed = 2.5;
    private final double brakeFriction = 1.0;
    private final double runFriction = 0.1;
    private float animTimer = 0f;
    private AudioState audio;
    private Material playerMat;
    private double dirToWall = 0;
    private final Vector2f driver = new Vector2f(0,0);
    private PlayerAction state = PlayerAction.FALLING;
    private Spatial player;
    private Body playerBody;
    private BodyFixture baseFixture, feetFixture, handFixture;

    @Override
    protected void initialize(Application app) {
        audio = getState(AudioState.class);
        //load material
        playerMat = new Material(app.getAssetManager(), "com/mrugames/assets/Spritesheet.j3md");
        playerMat.setFloat("SizeX", 2);
        playerMat.setFloat("SizeY", 3);
        playerMat.setFloat("Position", 0);
        Texture spriteSheet = app.getAssetManager().loadTexture("com/mrugames/assets/Ninja.png");
        spriteSheet.setMagFilter(Texture.MagFilter.Nearest);
        spriteSheet.setWrap(Texture.WrapMode.EdgeClamp);
        spriteSheet.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        playerMat.setTexture("ColorMap", spriteSheet);
        playerMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        playerMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        //spawn player for now
        player = spawnPlayer();
        player.setLocalTranslation(0, 0, 0.1f);
        player.setQueueBucket(RenderQueue.Bucket.Transparent);
        ((LD42)app).getRootNode().attachChild(player);
        //controls
        InputManager im = app.getInputManager();
        im.addListener(this, MOVE_LEFT, MOVE_RIGHT, JUMP);
    }
    
    protected Spatial spawnPlayer(){
        Mesh playerMesh = new Sprite(1,1);
        Spatial spat = new Geometry("Player", playerMesh);
        spat.setMaterial(playerMat);
        //rigid body
        Circle circle = new Circle(0.5);
        playerBody = new Body();
        baseFixture = playerBody.addFixture(circle);
        baseFixture.setFilter(new CategoryFilter(PhysicsState.PLAYER_GROUP,
                PhysicsState.GROUND_GROUP));
        baseFixture.setFriction(brakeFriction);
        //feet sensor
        Convex feetShape = new Rectangle(0.8,0.2);
        feetFixture = playerBody.addFixture(feetShape);
        feetFixture.setSensor(true);
        feetFixture.setFilter(new CategoryFilter(PhysicsState.PLAYER_GROUP, PhysicsState.GROUND_GROUP));
        feetShape.translate(0, -0.9);
        //wall sensor
        Convex handShape = new Rectangle(1.2, 0.8);
        handFixture = playerBody.addFixture(handShape);
        handFixture.setSensor(true);
        handFixture.setFilter(new CategoryFilter(PhysicsState.PLAYER_GROUP, PhysicsState.GROUND_GROUP));
        //finalize player body
        playerBody.setMass(new Mass(new Vector2(0,0), 1.0, 1.0));
        playerBody.setMassType(MassType.FIXED_ANGULAR_VELOCITY);
        playerBody.setAutoSleepingEnabled(false);
        getState(PhysicsState.class).getWorld().addBody(playerBody);
        //controls
        spat.addControl(new BodyControl(playerBody));
        return spat;
    }

    @Override
    public void update(float tpf) {
        //set the sprite animation depending on what state we are in
        if(state == PlayerAction.GROUNDED){
            double speed = playerBody.getLinearVelocity().x;
            if(Math.abs(speed) <= 0.01){
                animTimer = 0;
                playerMat.setFloat("Position", 0);
            } else{
                animTimer += tpf;
                animTimer %= 1;
                int pos = 1+(int)(animTimer*4);
                playerMat.setFloat("Position", pos);
                if(speed < 0){
                    player.setLocalScale(-1, 1, 1);
                } else{
                    player.setLocalScale(1,1,1);
                }
            }
        } if(state == PlayerAction.WALLSLIDING){
            playerMat.setFloat("Position", 5);
            if(dirToWall < 0){
                player.setLocalScale(1,1,1);
            } else{
                player.setLocalScale(-1,1,1);
            }
        } if(state == PlayerAction.FALLING || state == PlayerAction.JUMPING){
            playerMat.setFloat("Position", 0);
            if(playerBody.getLinearVelocity().x < 0){
                player.setLocalScale(-1,1,1);
            } else{
                player.setLocalScale(1,1,1);
            }
        }
    }
    
    
    
    public Body getPlayerBody(){
        return playerBody;
    }
    
    public Vector2 getPlayerPosition(){
        return playerBody.getTransform().getTranslation();
    }

    @Override
    protected void cleanup(Application app) {
        player.removeFromParent();
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
    public void onAction(String name, boolean isPressed, float tpf) {
        if(name.equals(MOVE_LEFT)){
            if(isPressed){
                driver.x = -1;
            } else{
                driver.x = 0;
            }
        } else if(name.equals(MOVE_RIGHT)){
            if(isPressed){
                driver.x = 1;
            } else{
                driver.x = 0;
            }
        }
        if(name.equals(JUMP)){
            if(isPressed){
                driver.y = 1;
            } else{
                driver.y = 0;
            }
        } else if(name.equals(CROUCH)){
            if(isPressed){
                driver.y = -1;
            } else{
                driver.y = 0;
            }
        }
    }
    
    private void setState(PlayerAction state){
        if(this.state == state) return;
        
        this.state = state;
        
        switch(state){
            case FALLING:
                playerBody.setGravityScale(fallGrav);
                break;
            case GROUNDED:
                playerBody.setGravityScale(1);
                break;
            case JUMPING:
                playerBody.applyImpulse(new Vector2(0, jumpForce));
                playerBody.setGravityScale(1);
                audio.playClip(AudioState.JUMP);
                break;
            case WALLSLIDING:
                playerBody.setGravityScale(wallGrav);
                break;
            case DEAD:
                audio.playClip(AudioState.DEAD);
                break;
        }
    }

    @Override
    public void begin(Step step, World world) {
        if(state == PlayerAction.DEAD) return;
        //poll the player input and apply it to the player body
        if(state == PlayerAction.GROUNDED){
            //on ground, let's check for jumping
            if(driver.y > 0){
                setState(PlayerAction.JUMPING);
            }
            if(Math.abs(driver.x) > 0.1f){
                baseFixture.setFriction(runFriction);
            } else{
                baseFixture.setFriction(brakeFriction);
            }
        } else if(state == PlayerAction.WALLSLIDING){
            if(driver.y > 0){
                setState(PlayerAction.JUMPING);
                //get dir to wall and jump away from wall
                playerBody.applyImpulse(new Vector2(jumpForce/2*-dirToWall,0));
            }
        }
        //left/right movement
        //TODO: collision detection with ground and walls
        Vector2 movement = new Vector2(driver.x*playerSpeed, 0);
        playerBody.applyForce(movement.multiply(step.getDeltaTime()*100));
    }

    @Override
    public void updatePerformed(Step step, World world) {
        
    }

    @Override
    public void postSolve(Step step, World world) {
        
    }

    @Override
    public void end(Step step, World world) {
        if(state == PlayerAction.DEAD) return;
        //cast down to detect ground
        List<ContactPoint> collisions = playerBody.getContacts(false);
        if(!collisions.isEmpty()){
            for(ContactPoint point : collisions){
                if(point.getDepth() >= 0.3){
                    getState(GameState.class).setGameAction(GameState.GameAction.GAMEOVER);
                    setState(PlayerAction.DEAD);
                }
            }
        }
        List<ContactPoint> sensors = playerBody.getContacts(true);
        if(!sensors.isEmpty()){
            //check for feet on ground
            for(ContactPoint point : sensors){
                if(point.getFixture1().equals(feetFixture) || point.getFixture2().equals(feetFixture)){
                    //if feet can detect ground and the velocity is not to high, we have landed
                    if(Math.abs(playerBody.getLinearVelocity().y) < 0.1){
                        setState(PlayerAction.GROUNDED);
                    }
                    //on the ground, gravity should be normal now
                    break;
                } else if(point.getFixture1().equals(handFixture) || point.getFixture2().equals(handFixture)){
                    //check for wall sliding
                    if(Math.abs(playerBody.getLinearVelocity().y) < 0.1){
                        dirToWall = Math.signum(point.getPoint()
                                .x-playerBody.getTransform().getTranslationX());
                        setState(PlayerAction.WALLSLIDING);
                    }
                }
            }
        } else{
            if(playerBody.getLinearVelocity().y < 0){
                setState(PlayerAction.FALLING);
            }
        }
    }
    
    public enum PlayerAction{
        GROUNDED,
        JUMPING,
        WALLSLIDING,
        FALLING,
        DEAD
    }
}
