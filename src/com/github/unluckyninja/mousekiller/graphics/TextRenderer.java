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
package com.github.unluckyninja.mousekiller.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author UnluckyNinja
 */
public class TextRenderer {

    private float posX;
    private float posY;
    private boolean randomStyle;
    private boolean boldStyle;
    private boolean strikethroughStyle;
    private boolean underlineStyle;
    private boolean italicStyle;
    /**
     * Array of the start/end column (in upper/lower nibble) for every glyph in
     * the /font directory.
     */
    private byte[] glyphWidth;
    private int[] colorCode = new int[32];
    private int textColor;
    private float alpha = 1.0f;
    private int pixelWeight = 15;
    // for rendering
    private Pixmap[] pixmaps;
    private Texture texture;
    private int currentPage;
    private SpriteBatch batch;
    // never log this!!
    private static String allowedCharacters;
    private Random fontRandom;
    private boolean unicodeFlag = true;

    static {
        allowedCharacters = getAllowedCharacters();
    }

    {
        glyphWidth = new byte[65536];
        readGlyphSizes();
        pixmaps = new Pixmap[256];
        readTextures(0);
        initColor();
    }

    public TextRenderer() {
        batch = new SpriteBatch();
        batch.enableBlending();
    }

    // 将INT颜色转换成byte
    private static byte[] readColor(int[] intcolor) {
        int i = 0;
        byte[] bytecolor = new byte[65535 * 4];
        for (int j = 0; j < 65535; j++) {
            int index = intcolor[j];
            if (index == 0) {
                bytecolor[i++] = 0;
                bytecolor[i++] = 0;
                bytecolor[i++] = 0;
                bytecolor[i++] = 0;
            } else {
                bytecolor[i++] = -1;
                bytecolor[i++] = -1;
                bytecolor[i++] = -1;
                bytecolor[i++] = -1;
            }
        }
        return bytecolor;
    }

    /**
     * 加载材质，当需要用到时加载，并保存在数组中。
     *
     * @param i
     */
    public void readTextures(int i) {
        if (pixmaps[i] != null) {
            return;
        }
        String s = String.format("res/textures/font/unicode_page_%02X.png", new Object[]{Integer.valueOf(i)});
        FileHandle file = Gdx.files.internal(s);
        if (file.exists()) {
            try {
                BufferedImage image = ImageIO.read(file.read());
                Pixmap map = new Pixmap(256, 256, Pixmap.Format.RGBA8888);
                int[] colors = new int[65536];
                for (int j = 0; j < 65536; j++) {
                    colors[j] = 0;
                }
                image.getData().getPixels(0, 0, 256, 256, (int[]) colors);
                ByteBuffer pixels = map.getPixels();
                pixels.clear();
                pixels.put(readColor(colors));
                pixels.position(0);
                pixmaps[i] = map;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 初始化完成后，设置一次投影矩阵，然后就可以开始绘制了。
     *
     * @param toRender
     * @param x
     * @param y
     * @param size
     */
    public void drawString(String toRender, float x, float y, float size) {
        posX = x;
        posY = y;
        pixelWeight = getPixelWeight(size);
        batch.begin();
        this.renderString(toRender, false);
        batch.end();
    }

    /**
     * Render a single line string at the current (posX,posY) and update posX
     * 通过两个域 posX, posY 绘制单行文本并更新 posX
     */
    private void renderString(String toRender, boolean isShadow) {
        // 遍历每个字符
        for (int i = 0; i < toRender.length(); ++i) {
            // 获取字符
            char c = toRender.charAt(i);
            // 下面在控制符部分作为字体索引，在绘制部分作为字符的索引
            int index;
            // 在控制符部分这个变量作为包装成 int 的 RGB 颜色，在绘制时作为随机字索引
            int color;

            // 检测字体控制符(alt+1 6 7)，忽略句尾
            if (c == 167 && i + 1 < toRender.length()) {
                // 16种色彩+6种字形
                index = "0123456789abcdefklmnor".indexOf(toRender.toLowerCase().charAt(i + 1));

                // 如果是颜色符
                if (index < 16) {
                    // 清除全部字形，也就是说游戏中输入时，不能把颜色放字形后面，会导致字形无效
                    this.randomStyle = false;
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;

                    // 左边是控制符后跟其它字符时，右边不知道……
                    if (index < 0 || index > 15) {
                        index = 15;
                    }

                    // 是否为阴影
                    if (isShadow) {
                        index += 16;
                    }

                    // colorCode是一个包装成 int 的颜色数组，一共有32种，前16种普通颜色，后16种暗一点，用于做字的阴影
                    color = this.colorCode[index];
                    this.textColor = color;

                    // LWJGL设置颜色，从此可以看出该 int的高4位被丢弃了，仅作为RGB使用
                    batch.setColor((float) (color >> 16) / 255.0F, (float) (color >> 8 & 255) / 255.0F, (float) (color & 255) / 255.0F, this.alpha);

                    // 如果是§k
                } else if (index == 16) {
                    this.randomStyle = true;

                    // 如果是§l
                } else if (index == 17) {
                    this.boldStyle = true;

                    // 如果是§m
                } else if (index == 18) {
                    this.strikethroughStyle = true;

                    // 如果是§n
                } else if (index == 19) {
                    this.underlineStyle = true;

                    // 如果是§o
                } else if (index == 20) {
                    this.italicStyle = true;

                    // 如果是§r
                } else if (index == 21) {
                    this.randomStyle = false;
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;
                    batch.setColor((float) (textColor >> 16) / 255.0F, (float) (textColor >> 8 & 255) / 255.0F, (float) (textColor & 255) / 255.0F, this.alpha);
                }

                // 跳过控制符后面的字符
                ++i;

                // 绘制部分
            } else {

                index = allowedCharacters.indexOf(c);

                // 随机字，获取时保证原字符与随机字的宽度相等。前提是allowedCharacters中存在该字符
                if (this.randomStyle && index > 0) {
                    int w = 0;
                    do {
                        //获取随机字符索引
                        color = this.fontRandom.nextInt(allowedCharacters.length());
                        i++;
                        if (i == 32) {
                            break;
                        }
                        // 当字体宽度相等时跳出。
                    } while (this.glyphWidth[index + 32] != this.glyphWidth[color + 32]);

                    // 替换原字符索引
                    index = color;
                }
                // 用于阴影或 Unicode偏移，应该是为了让 Unicode字符看起来更紧凑点 
                float offset = this.unicodeFlag ? 0.5F * pixelWeight / 16.0f : 1.0F * pixelWeight / 16.0f;
                boolean compact = (index <= 0 || this.unicodeFlag) && isShadow;

                // 开始绘制 Unicode字符时变得紧凑
                if (compact) {
                    this.posX -= offset;
                    this.posY -= offset;
                }

                // 渲染单个字符并返回占用长度
                float offsetX = this.renderCharAtPos(index, c, this.italicStyle);

                // 绘制完 Unicode字符后恢复原状
                if (compact) {
                    this.posX += offset;
                    this.posY += offset;
                }

                // 粗体则多绘制一遍
                if (this.boldStyle) {
                    this.posX += offset;

                    // 这部分同上
                    if (compact) {
                        this.posX -= offset;
                        this.posY -= offset;
                    }

                    this.renderCharAtPos(index, c, this.italicStyle);

                    this.posX -= offset;

                    if (compact) {
                        this.posX += offset;
                        this.posY += offset;
                    }
                    // 避免粗体和其它字符挤在一起
                    ++offsetX;
                }
                /* 以后实现下划线和删除线字体
                 // 镶嵌器，绘制删除线和下划线用。
                 Tessellator drawer;

                 if (this.strikethroughStyle) {
                 drawer = Tessellator.instance;
                 // OPENGL取消材质渲染
                 GL11.glDisable(GL11.GL_TEXTURE_2D);
                 drawer.startDrawingQuads();
                 drawer.addVertex((double) this.posX, (double) (this.posY + (float) (this.FONT_HEIGHT / 2)), 0.0D);
                 drawer.addVertex((double) (this.posX + offsetX), (double) (this.posY + (float) (this.FONT_HEIGHT / 2)), 0.0D);
                 drawer.addVertex((double) (this.posX + offsetX), (double) (this.posY + (float) (this.FONT_HEIGHT / 2) - 1.0F), 0.0D);
                 drawer.addVertex((double) this.posX, (double) (this.posY + (float) (this.FONT_HEIGHT / 2) - 1.0F), 0.0D);
                 drawer.draw();
                 // 恢复开启状态
                 GL11.glEnable(GL11.GL_TEXTURE_2D);
                 }

                 if (this.underlineStyle) {
                 drawer = Tessellator.instance;
                 // OPENGL取消材质渲染
                 GL11.glDisable(GL11.GL_TEXTURE_2D);
                 drawer.startDrawingQuads();
                 // 如此则百分百为-1，意义不明，猜测是给斜体也加上斜移的下划线，因此该是：
                 // "int offset = this.italicStyle ? -1 : 0;"
                 // 1.4也存在这个BUG
                 int underlineOffset = this.underlineStyle ? -1 : 0;
                 drawer.addVertex((double) (this.posX + (float) underlineOffset), (double) (this.posY + (float) this.FONT_HEIGHT), 0.0D);
                 drawer.addVertex((double) (this.posX + offsetX), (double) (this.posY + (float) this.FONT_HEIGHT), 0.0D);
                 drawer.addVertex((double) (this.posX + offsetX), (double) (this.posY + (float) this.FONT_HEIGHT - 1.0F), 0.0D);
                 drawer.addVertex((double) (this.posX + (float) underlineOffset), (double) (this.posY + (float) this.FONT_HEIGHT - 1.0F), 0.0D);
                 drawer.draw();
                 // 恢复开启状态
                 GL11.glEnable(GL11.GL_TEXTURE_2D);
                 }
                 */
                this.posX += (float) ((int) offsetX);
            }
        }
    }

    /**
     * Pick how to render a single character and return the width used.
     * 选择渲染单字符方式（默认字符或Unicode字符），然后返回占用的宽度。
     */
    private float renderCharAtPos(int index, char c, boolean isItalic) {
        // 是否空格（是则不绘制，直接返回长度），再检测是否使用的是 unicode 字符
        return c == 32 ? 4.0F * pixelWeight / 16.0f : (index > 0 && !this.unicodeFlag ? /*this.renderDefaultChar(index + 32, isItalic)*/ 0.0f : this.renderUnicodeChar(c, isItalic));
    }

    /**
     * Render a single character with the default.png font at current
     * (posX,posY) location...
     *//* 现在用不到该方法，因为都是UTF-8字体
     private float renderDefaultChar(int index, boolean isItalic) {
     float pixelX = (float) (index % 16 * 8);
     float pixelY = (float) (index / 16 * 8);
     float offset = isItalic ? 1.0F : 0.0F;

     // 确保 OPENGL绑定的材质是文本材质
     this.renderEngine.bindTexture(this.fontTextureName);

     // 获取字符宽度
     float width = (float) this.charWidth[index] - 0.01F;

     GL11.glBegin(GL11.GL_TRIANGLE_STRIP);// 从左上角逆时针绘制
     GL11.glTexCoord2f(pixelX / 128.0F, pixelY / 128.0F);
     GL11.glVertex3f(this.posX + offset, this.posY, 0.0F);
     GL11.glTexCoord2f(pixelX / 128.0F, (pixelY + 7.99F) / 128.0F);
     GL11.glVertex3f(this.posX - offset, this.posY + 7.99F, 0.0F);
     GL11.glTexCoord2f((pixelX + width) / 128.0F, pixelY / 128.0F);
     GL11.glVertex3f(this.posX + width + offset, this.posY, 0.0F);
     GL11.glTexCoord2f((pixelX + width) / 128.0F, (pixelY + 7.99F) / 128.0F);
     GL11.glVertex3f(this.posX + width - offset, this.posY + 7.99F, 0.0F);
     GL11.glEnd();

     return (float) this.charWidth[index];
     }*/

    /**
     * Render a single Unicode character at current (posX,posY) location using
     * one of the /font/glyph_XX.png files... 在当前位置(posX,posY)绘制单个 Unicode字符， 使用
     * assets/minecraft/textures/font下的 glyph_XX.png文件
     */
    private float renderUnicodeChar(char c, boolean isItalic) {

        // glyphWidth是从 glyph_sizes.bin文件解码而来，
        // 该文件保存了各 Unicode字符宽度
        if (this.glyphWidth[c] == 0) {
            return 0.0F;
        } else {
            // 获取字符所在页数，1页256个字符
            int page = c / 256;
            if (currentPage != page) {
                batch.flush();
                if(texture != null){
                    texture.dispose();
                }
                readTextures(page);
                texture = new Texture(pixmaps[page]);
                currentPage = page;
            }

            // 将字符宽度转换为材质顶点。
            // glyphWidth存储了字符对应材质(16p*16p)的最左最右有像素两列的序号，
            // 高四位是左列，低四位是右列
            int highNibble = this.glyphWidth[c] >>> 4;
            int lowNibble = this.glyphWidth[c] & 15;
            float leftColumn = (float) highNibble;
            float rightColumn = (float) (lowNibble + 1);

            // 求得字符列数+左列像素数即为横向坐标 x
            float pixelX = (float) (c % 16 * 16) + leftColumn;
            // 求得该字符在该材质中的对应行数*高度(16p)即为纵向坐标
            float pixelY = (float) ((c & 255) / 16 * 16);
            float width = rightColumn - leftColumn - 0.02F;
            float offset = isItalic ? 1.0F : 0.0F;

            // opengl 绘图，左上角逆时针
//            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
//            GL11.glTexCoord2f(pixelX / 256.0F, pixelY / 256.0F);
//            GL11.glVertex3f(this.posX + offset, this.posY, 0.0F);
//            GL11.glTexCoord2f(pixelX / 256.0F, (pixelY + 15.98F) / 256.0F);
//            GL11.glVertex3f(this.posX - offset, this.posY + 7.99F, 0.0F);
//            GL11.glTexCoord2f((pixelX + width) / 256.0F, pixelY / 256.0F);
//            GL11.glVertex3f(this.posX + width / 2.0F + offset, this.posY, 0.0F);
//            GL11.glTexCoord2f((pixelX + width) / 256.0F, (pixelY + 15.98F) / 256.0F);
//            GL11.glVertex3f(this.posX + width / 2.0F - offset, this.posY + 7.99F, 0.0F);
//            GL11.glEnd();
            TextureRegion region = new TextureRegion(texture, pixelX / 256.0F, pixelY / 256.0F, (pixelX + width) / 256.0F, (pixelY + 15.98F) / 256.0F);
            batch.draw(region, posX, posY, width * pixelWeight / 16.0f, 16f * pixelWeight / 16.0f);
//            batch.draw(texture, posX, posY);
            return ((rightColumn - leftColumn) + 1.0F) * pixelWeight / 16.0f;
        }
    }

    public static int getPixelWeight(float point) {
        return (int) ((point / 72f) * 96);
    }

    private void readGlyphSizes() {
        this.glyphWidth = Gdx.files.internal("res/font/glyph_sizes.bin").readBytes();
    }

    private void initColor() {
        for (int i = 0; i < 32; ++i) {
            int j = (i >> 3 & 1) * 85;
            int k = (i >> 2 & 1) * 170 + j;
            int l = (i >> 1 & 1) * 170 + j;
            int i1 = (i & 1) * 170 + j;

            if (i == 6) {
                k += 85;
            }

            if (i >= 16) {
                k /= 4;
                l /= 4;
                i1 /= 4;
            }

            this.colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
        }
    }

    /**
     * Load the font.txt resource file, that is on UTF-8 format. This file
     * contains the characters that minecraft can render Strings on screen.
     */
    private static String getAllowedCharacters() {
        StringBuilder s = new StringBuilder(4096);
        try (BufferedReader bufferedreader = Gdx.files.internal("res/font.txt").reader(1024, "UTF-8")) {
            String s1 = "";

            while ((s1 = bufferedreader.readLine()) != null) {
                if (!s1.startsWith("#")) {
                    s = s.append(s1);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(TextRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return s.toString();
    }

    /**
     * 初始化、改变窗口大小时需要重新传入一次投影矩阵。
     *
     * @param matrix
     */
    public void setProjectionMatrix(Matrix4 matrix) {
        batch.setProjectionMatrix(matrix);
    }
}
