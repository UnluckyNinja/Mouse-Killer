/*
 * Copyright (C) 2013 UnluckyNinja
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.github.unluckyninja.mousekiller.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author UnluckyNinja
 */
public class Killer {

    private static final TextureRegion defaultTex = new TextureRegion(new Texture(Gdx.files.internal("res/killer/cursor.png")));
    private TextureRegion texture;
    private Vector2 coords = new Vector2(0, 0);

    public Killer() {
        texture = defaultTex;
    }

    public Killer(TextureRegion texture) {
        this.texture = texture;
        if (texture == null) {
            this.texture = defaultTex;
        }
    }
    
    public Killer(float x, float y) {
        texture = defaultTex;
        coords.x = x;
        coords.y = y;
    }
    
    public Killer(float x, float y,TextureRegion texture) {
        texture = defaultTex;
        coords.x = x;
        coords.y = y;
    }

    public TextureRegion getTexture() {
        return texture;
    }

    public Vector2 getCoords() {
        return coords;
    }

    public void setCoords(float x, float y) {
        coords.set(x, y);
    }
    
}
