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

/**
 *
 * @author UnluckyNinja
 */
public class Test {
    public static void main(String[] args) {
        B b = new B();
        Test test = (Test)b;
        System.out.println(test.a());
    }
    int a(){
        return 1;
    }
}

class B extends Test{
    @Override
    int a (){
        return 2;
    }
    int b(){
        return 3;
    }
}