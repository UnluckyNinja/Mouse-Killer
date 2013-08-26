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
package com.github.unluckyninja.mousekiller.graphics.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 *
 * @author UnluckyNinja
 */
public class MeshHelper {

    private Mesh mesh;
    private ShaderProgram meshShader;

    public MeshHelper() {
        createShader();
    }

    public void createMesh(float[] vertices) {
        mesh = new Mesh(true, vertices.length, 0,
                new VertexAttribute(Usage.Position, 2, "a_position"),
                new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
        mesh.setVertices(vertices);
    }

    public void drawMesh() {
        // this should be called in render()
        if (mesh == null) {
            throw new IllegalStateException("drawMesh called before a mesh has been created.");
        }
        Gdx.gl20.glEnable(GL20.GL_BLEND);

        meshShader.begin();
        mesh.render(meshShader, GL20.GL_TRIANGLES);
        meshShader.end();
    }

    private void createShader() {
        // this shader tells opengl where to put things
        String vertexShader = "attribute vec4 a_position;    \n"
                + "attribute vec4 a_color;       \n"
                + "varying vec4 v_color;         \n"
                + "void main()                   \n"
                + "{                             \n"
                + "   v_color = a_color;         \n"
                + "   gl_Position = a_position;  \n"
                + "}                             \n";

        // this one tells it what goes in between the points (i.e colour/texture)
        String fragmentShader = "#ifdef GL_ES                \n"
                + "precision mediump float;    \n"
                + "#endif                      \n"
                + "varying vec4 v_color;       \n"
                + "void main()                 \n"
                + "{                           \n"
                + "  gl_FragColor = v_color;   \n"
                + "}                           \n";

        // make an actual shader from our strings
        meshShader = new ShaderProgram(vertexShader, fragmentShader);
        // check there's no shader compile errors
        if (meshShader.isCompiled() == false) {
            throw new IllegalStateException(meshShader.getLog());
        }
    }

    public void dispose() {
        mesh.dispose();
        meshShader.dispose();
    }
}
