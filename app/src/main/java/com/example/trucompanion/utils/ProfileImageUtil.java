package com.example.trucompanion.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;

public class ProfileImageUtil {

    public static Bitmap generateInitialsBitmap(Context context, String name, String profileColor) { // Renamed param for clarity

        int size = 200;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.parseColor(profileColor));


        float cx = size / 2f;
        float cy = size / 2f;
        float radius = size / 2f;

        canvas.drawCircle(cx, cy, radius, bgPaint);

        String initial = "?";
        if (name != null && !name.trim().isEmpty()) {
            initial = name.substring(0, 1).toUpperCase();
        }

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(90);
        textPaint.setTextAlign(Paint.Align.CENTER);

        Rect textBounds = new Rect();
        textPaint.getTextBounds(initial, 0, initial.length(), textBounds);

        float textX = cx;
        float textY = cy - textBounds.exactCenterY();

        canvas.drawText(initial, textX, textY, textPaint);

        return bitmap;
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;

        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());

        Bitmap squaredBitmap = Bitmap.createBitmap(
                bitmap,
                (bitmap.getWidth() - size) / 2,
                (bitmap.getHeight() - size) / 2,
                size,
                size
        );

        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, size, size);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(Color.WHITE);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(squaredBitmap, rect, rect, paint);

        if (squaredBitmap != bitmap) {
            squaredBitmap.recycle();
        }

        return output;
    }

    public static Uri saveBitmapAndGetUri(Context context, Bitmap bitmap) {
        try {
            File folder = new File(context.getFilesDir(), "profile_images");
            if (!folder.exists()) folder.mkdir();

            String filename = "profile_" + System.currentTimeMillis() + ".png";
            File file = new File(folder, filename);

            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            return FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".fileprovider",
                    file
            );

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}