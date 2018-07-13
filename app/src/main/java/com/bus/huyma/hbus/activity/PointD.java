package com.bus.huyma.hbus.activity;

import android.annotation.SuppressLint;
import android.graphics.PointF;

@SuppressLint("ParcelCreator")
public class PointD extends PointF {
    public double x;
    public double y;

    PointD parent;

    public PointD(double x,double y){
        this.set((float)x,(float)y);
        this.x=x;
        this.y=y;
    }

    public double Length(){
        double l = (double ) this.length();
        return l;
    }

    public final void set(double x,double y){
        this.set((float)x,(float)y);
        this.x=x;
        this.y=y;
    }
}
