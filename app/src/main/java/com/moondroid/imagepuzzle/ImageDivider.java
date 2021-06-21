package com.moondroid.imagepuzzle;

import android.graphics.Bitmap;

public class ImageDivider {

    public Bitmap[] divideSquare(Bitmap originBitmap, int square){
        Bitmap[] bitmaps = new Bitmap[square*square];

        int width = originBitmap.getWidth();
        int height = originBitmap.getHeight();

        int widthUnit = width/square;
        int heightUnit = height/square;

        for (int i = 0; i< square; i++){
            for (int j= 0 ; j<square; j++){
                bitmaps[square*i+j] = Bitmap.createBitmap(originBitmap, widthUnit*j, heightUnit*i, widthUnit, heightUnit);
            }
        }

        return bitmaps;
    }
}
