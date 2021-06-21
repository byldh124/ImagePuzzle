package com.moondroid.imagepuzzle;

import android.graphics.Bitmap;

public class PuzzleItem {
    Bitmap bitmap;
    int index;

    public PuzzleItem(Bitmap bitmap, int index) {
        this.bitmap = bitmap;
        this.index = index;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getIndex() {
        return index;
    }
}
