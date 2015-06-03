package com.shicaigj.otto.utils;

import android.graphics.Bitmap;

import com.shicaigj.otto.bean.ImagePiece;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Otto on 2015/6/2.
 */
public class ImageSplitterUtil {

    public static List<ImagePiece> splitImage(Bitmap bitmap,int pieces){
        List<ImagePiece> imagePieces = new ArrayList<>();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int imagePieceWidth = Math.min(width,height) / pieces;

        for (int i = 0; i < pieces; i++){
            for (int j = 0; j < pieces; j++){
                ImagePiece imagePiece = new ImagePiece();
                imagePiece.setIndex(j + i * pieces);

                int x = j * imagePieceWidth;
                int y = i * imagePieceWidth;

                imagePiece.setBitmap(Bitmap.createBitmap(bitmap,x,y,imagePieceWidth,imagePieceWidth));
                imagePieces.add(imagePiece);
            }
        }

        return imagePieces;
    }
}
