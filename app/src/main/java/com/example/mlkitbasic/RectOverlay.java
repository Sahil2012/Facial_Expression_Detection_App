package com.example.mlkitbasic;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class RectOverlay extends GraphicOverlay.Graphic {


    private GraphicOverlay graphicOverlay;
    private Rect rect;
    private Paint paint;

    public RectOverlay(GraphicOverlay overlay, Rect rect) {
        super(overlay);

        this.graphicOverlay = overlay;
        this.rect = rect;

        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {

        paint = new Paint();
        paint.setStrokeWidth(15.0f);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);

        RectF rectF = new RectF(rect);

        rectF.top = translateX(rectF.top);
        rectF.right = translateX(rectF.right);
        rectF.left = translateX(rectF.left);
        rectF.bottom = translateX(rectF.bottom);

        canvas.drawRect(rectF,paint);
    }
}
