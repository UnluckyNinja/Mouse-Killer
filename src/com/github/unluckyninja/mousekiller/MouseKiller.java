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
package com.github.unluckyninja.mousekiller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.github.unluckyninja.mousekiller.model.Killer;
import com.github.unluckyninja.mousekiller.model.listener.SimpleInputListener;

public class MouseKiller extends Game {

    static int width = 800;
    static int height = 600;
    public static final float PIXEL2BOX = 0.05f;
    public static final float BOX2PIXEL = 20f;

    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Mouse Killer";
        cfg.width = width;
        cfg.height = height;
        cfg.useGL20 = true;
        new LwjglApplication(new MouseKiller(), cfg);
    }
    private static MouseKiller mk;
    public MainMenu menu;
    
    SpriteBatch batch;
    Killer killer;
    World world;
    Box2DDebugRenderer debugRenderer;
    
    @Override
    public void create() {
        mk = this;
        batch = new SpriteBatch();
        killer = new Killer(Gdx.input.getX(), height - Gdx.input.getY());
        world = new World(new Vector2(0, 0), true);
        debugRenderer = new Box2DDebugRenderer();
        
//        Gdx.input.setCursorCatched(true);
        this.setScreen(menu = new MainMenu(this, batch));
        Gdx.input.setInputProcessor(new SimpleInputListener(killer));

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        MouseKiller.width = width;
        MouseKiller.height = height;
    }

    @Override
    public void render() {
        super.render();
        world.step(1 / 60f, 6, 2);
        //box2dManagaer.draw(); //to replace drawment of box2d
        batch.getProjectionMatrix().scale(BOX2PIXEL, BOX2PIXEL, 0);
        debugRenderer.render(world, batch.getProjectionMatrix());
        batch.getProjectionMatrix().scale(PIXEL2BOX, PIXEL2BOX, 0);
    }

    @Override
    public void dispose() {
        getScreen().dispose();
        killer.getTexture().getTexture().dispose();
        batch.dispose();
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static MouseKiller getInstance() {
        return mk;
    }
    
}
