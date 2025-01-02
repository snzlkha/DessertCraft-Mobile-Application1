package com.yourapp.desertcraft.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Stack;

public class PaintView extends View {

    private Paint paint;
    private Path path;
    private ArrayList<Path> paths;
    private ArrayList<Paint> paints;
    private Stack<Path> undonePaths;
    private Stack<Paint> undonePaints;
    private Bitmap backgroundBitmap;
    private Bitmap scaledBitmap;

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(8f);

        path = new Path();
        paths = new ArrayList<>();
        paints = new ArrayList<>();
        undonePaths = new Stack<>();
        undonePaints = new Stack<>();
    }

    public void setBrushSize(float size) {
        paint.setStrokeWidth(size);
    }

    public void setBrushColor(int color) {
        paint.setColor(color);
    }

    public void setBackgroundImage(Bitmap bitmap) {
        backgroundBitmap = bitmap;
//        scaledBitmap = scaleCenterCrop(bitmap, getWidth(), getHeight());
        scaledBitmap = scaleFitCenter(bitmap, getWidth(), getHeight());
        invalidate();
    }

//    private Bitmap scaleCenterCrop(Bitmap source, int newWidth, int newHeight) {
//        int sourceWidth = source.getWidth();
//        int sourceHeight = source.getHeight();
//
//        float scale = Math.max((float) newWidth / sourceWidth, (float) newHeight / sourceHeight);
//
//        float scaledWidth = scale * sourceWidth;
//        float scaledHeight = scale * sourceHeight;
//
//        float left = (newWidth - scaledWidth) / 2;
//        float top = (newHeight - scaledHeight) / 2;
//
//        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(scaledBitmap);
//        Matrix matrix = new Matrix();
//        matrix.setScale(scale, scale);
//        matrix.postTranslate(left, top);
//        canvas.drawBitmap(source, matrix, new Paint(Paint.FILTER_BITMAP_FLAG));
//
//        return scaledBitmap;
//    }

    private Bitmap scaleFitCenter(Bitmap source, int newWidth, int newHeight) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scale factors to fit the new dimensions while maintaining aspect ratio
        float scaleWidth = (float) newWidth / sourceWidth;
        float scaleHeight = (float) newHeight / sourceHeight;
        float scale = Math.min(scaleWidth, scaleHeight);

        // Calculate the scaled width and height
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Calculate left and top position to center the image
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // Create a matrix for the scaling operation
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);

        // Adjust the vertical position to center the image vertically
        top = Math.max(0, top); // Ensure top position is not less than zero
        matrix.postTranslate(left, top);

        // Create a new bitmap with the specified dimensions
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        // Create a canvas to draw the scaled bitmap
        Canvas canvas = new Canvas(scaledBitmap);

        // Apply the matrix transformation to the canvas
        canvas.drawBitmap(source, matrix, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }


    public void undo() {
        if (!paths.isEmpty()) {
            undonePaths.push(paths.remove(paths.size() - 1));
            undonePaints.push(paints.remove(paints.size() - 1));
            invalidate();
        }
    }

    public void redo() {
        if (!undonePaths.isEmpty()) {
            paths.add(undonePaths.pop());
            paints.add(undonePaints.pop());
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (scaledBitmap != null) {
            canvas.drawBitmap(scaledBitmap, 0, 0, null);
        }

        for (int i = 0; i < paths.size(); i++) {
            canvas.drawPath(paths.get(i), paints.get(i));
        }
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                paths.add(path);
                paints.add(new Paint(paint));
                path = new Path();
                // Clear the redo stack when a new path is drawn
                undonePaths.clear();
                undonePaints.clear();
                invalidate();
                return true;
            default:
                return false;
        }
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return bitmap;
    }

}
