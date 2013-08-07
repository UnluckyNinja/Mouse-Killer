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
package com.github.unluckyninja.mousekiller.model.listener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.github.unluckyninja.mousekiller.MouseKiller;
import com.github.unluckyninja.mousekiller.model.Killer;

/**
 *
 * @author UnluckyNinja
 */
public class SimpleInputListener implements InputProcessor {

    Killer killer;

    public SimpleInputListener(Killer killer) {
        this.killer = killer;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE) {
            Gdx.app.exit();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return onMoved(screenX, screenY);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return onMoved(screenX, screenY);
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    private boolean onMoved(int screenX, int screenY) {
        int x = screenX;
        int y = screenY;
        if (x < 0) {
            x = 0;
        } else if (x >= MouseKiller.getWidth()) {
            x = MouseKiller.getWidth();
        }
        if (y < 0) {
            y = 0;
        } else if (y >= MouseKiller.getHeight()) {
            y = MouseKiller.getHeight();
        }
        killer.setCoords(x, MouseKiller.getHeight() - y);
        Gdx.input.setCursorPosition(x, MouseKiller.getHeight() - y);
        return true;
    }
}
