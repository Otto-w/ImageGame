package com.shicaigj.otto.bean;

import android.graphics.Bitmap;

/**
 * Created by Otto on 2015/6/2.
 */
public class ImagePiece {

    private int index;
    private Bitmap bitmap;

    public ImagePiece(){

    }

    public ImagePiece(Bitmap bitmap, int index) {
        this.bitmap = bitmap;
        this.index = index;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "ImagePiece{" +
                "bitmap=" + bitmap +
                ", index=" + index +
                '}';
    }
}
