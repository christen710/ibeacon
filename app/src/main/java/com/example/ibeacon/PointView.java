package com.example.ibeacon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class PointView extends View {
    private List<Point> points;
    private Paint paint;
    private int redPointIndex = -1; // 要設置為紅色的點的索引
    private final Bitmap pointImage;
    private Bitmap ibeaconImage; // 用於繪製點的圖片
    private int horizontalSpacing = 50; // 调整水平间距
    private int verticalSpacing = 50; // 调整垂直间距

    public PointView(Context context) {
        super(context);
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        // 加載圖片，替換 R.drawable.your_point_image
        pointImage = BitmapFactory.decodeResource(getResources(), R.drawable.android_phone);
        ibeaconImage = BitmapFactory.decodeResource(getResources(), R.drawable.ibeacon);
    }

    public void setPoints(List<Point> points) {
        this.points = points;
        invalidate(); // 通知視圖重新繪製
    }

    public void setRedPointIndex(int index) {
        redPointIndex = index;
        invalidate(); // 當索引更改時，通知視圖重新繪製
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (points != null) {
            for (int i = 0; i < points.size(); i++) {
                Point point = points.get(i);
                if (i == redPointIndex) {
                    // 对于特定的点，使用不同的图像
                    Bitmap scaledPointImage = Bitmap.createScaledBitmap(ibeaconImage, ibeaconImage.getWidth() / 4, ibeaconImage.getHeight() / 4, true);
                    canvas.drawBitmap(scaledPointImage, point.x - scaledPointImage.getWidth() / 10, point.y - scaledPointImage.getHeight() / 10, paint);
                } else {
                    // 对于其他点，使用默认的图像
                    Bitmap scaledPointImage = Bitmap.createScaledBitmap(pointImage, pointImage.getWidth() / 8, pointImage.getHeight() / 8, true);
                    canvas.drawBitmap(scaledPointImage, point.x - scaledPointImage.getWidth() / 10 + i * horizontalSpacing, point.y - scaledPointImage.getHeight() / 10 + i * verticalSpacing, paint);
                }
            }
        }
    }
}
