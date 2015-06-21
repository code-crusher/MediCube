package com.appyware.medicube;

import android.graphics.Bitmap;

public class Reduce {

	public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else { 
            height = maxSize;
            width = (int) (height * bitmapRatio);
        } 
        Bitmap redpic = Bitmap.createScaledBitmap(image, width, height, true);
        return Bitmap.createScaledBitmap(image, width, height, true);
        
} 
}
