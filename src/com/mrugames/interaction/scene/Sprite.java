/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mrugames.interaction.scene;

import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;

/**
 * A Quad mesh but Centered around 0,0,0 
 * @author Matt Boudreau
 */
public class Sprite extends Mesh{
    private float width, height;
    
    public Sprite(float width, float height){
        updateGeometry(width, height);
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
    
    public void updateGeometry(){
        updateGeometry(width, height);
    }
    
    private void updateGeometry(float width, float height){
        this.width = width;
        this.height = height;
        setBuffer(VertexBuffer.Type.Position, 3, new float[]{-width/2, -height/2, 0,
                                                width/2, -height/2, 0,
                                                width/2, height/2, 0,
                                                -width/2, height/2, 0
                                                });
        
        setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]{0, 0,
                                                1, 0,
                                                1, 1,
                                                0, 1});
        
        setBuffer(VertexBuffer.Type.Normal, 3, new float[]{0, 0, 1,
                                              0, 0, 1,
                                              0, 0, 1,
                                              0, 0, 1});
        if (height < 0){
            setBuffer(VertexBuffer.Type.Index, 3, new short[]{0, 2, 1,
                                                 0, 3, 2});
        }else{
            setBuffer(VertexBuffer.Type.Index, 3, new short[]{0, 1, 2,
                                                 0, 2, 3});
        }
        
        updateBound();
        setStatic();
    }
}
