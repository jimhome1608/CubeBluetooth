package com.example.tut1;

import android.graphics.Point;


public class Point3D extends Point {
    public int z;

    public Point3D(int x, int y, int z) {
        super(x,y);
        this.z = z;
    }


}