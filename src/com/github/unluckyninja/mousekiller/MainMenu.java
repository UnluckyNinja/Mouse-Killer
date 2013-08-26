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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author UnluckyNinja
 */
public class MainMenu implements Screen {

    MouseKiller mk;
    Music bgm;
    public OrthographicCamera camera;
    Stage stage;

    public MainMenu(MouseKiller mk, SpriteBatch batch) {
        this.mk = mk;
        bgm = Gdx.audio.newMusic(Gdx.files.internal("HeyThere1.ogg"));
        bgm.setLooping(true);
        camera = new OrthographicCamera();
        stage = new Stage(MouseKiller.width, MouseKiller.height, true, mk.batch);

        bodyDef.position.set(20, 20);
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.linearDamping = 0;
        box.setAsBox(2, 2);

        ShaderProgram.pedantic = false;
        
    }
    BodyDef bodyDef = new BodyDef();
    PolygonShape box = new PolygonShape();
    long lastshottime = 0;
    private int i;
    @Override
    public void render(float delta) {
        //game logic
//        logics(delta);
        //graphics
        graphics(delta);
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    private void logics(float delta) {
        if (TimeUtils.millis() - lastshottime >= 200) {
            lastshottime = TimeUtils.millis();
            Body body = mk.world.createBody(bodyDef);
            body.setLinearVelocity(5 * MathUtils.cos(i), 5 * MathUtils.sin(i++));
            body.createFixture(box, 1);
        }
        Array<Body> list = new Array<>();
//        mk.world.getBodies(list);
        HashSet<Body> set = new HashSet<>();
        for (Iterator<Body> it = list.iterator(); it.hasNext();) {
            Body body = it.next();
            Vector2 vec = body.getWorldCenter();
            boolean remove = false;
            float xdiffer = vec.x - 20;
            if (xdiffer >= 3 || xdiffer < -3) {
                remove = true;
            }
            float ydiffer = vec.y - 20;
            if (!remove && (ydiffer >= 3 || ydiffer < -3)) {
                remove = true;
            }
            if (remove) {
                set.add(body);
            }
        }
        for (Body body : set) {
            mk.world.destroyBody(body);
        }
//        camera.position.set(mk.killer.getCoords().x, mk.killer.getCoords().y, 0);
//        camera.update();
    }

    
    //called for drawing. should it be the beginning of all drawings? I dont know.
    private void graphics(float delta) {
        Gdx.gl20.glClearColor(0, 1, 1, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        camera.update();
        mk.batch.setProjectionMatrix(camera.combined);
        mk.batch.begin();
        if (Gdx.input.isCursorCatched()) {
            mk.batch.draw(mk.killer.getTexture(), mk.killer.getCoords().x, mk.killer.getCoords().y - mk.killer.getTexture().getRegionHeight());
        }
        mk.batch.end();
    }

    @Override
    public void show() {
//        bgm.play();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        bgm.dispose();
        box.dispose();
        stage.dispose();
        mk.killer.getTexture().getTexture().dispose();
        
    }
}
