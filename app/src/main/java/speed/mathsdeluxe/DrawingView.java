package speed.mathsdeluxe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.google.mlkit.vision.digitalink.Ink;

public class DrawingView extends View {
    private final int tickSize = 100;

    private Bitmap bitmap;

    private float mouseX, mouseY;

    private Canvas canvas;

    private Path path;

    private Paint bitmapPaint;
    private Paint paint;

    Context context;

    Ink.Builder inkBuilder;

    Ink.Stroke.Builder strokeBuilder;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        path = new Path();
        bitmapPaint = new Paint(Paint.DITHER_FLAG);
        paint = new Paint();
        setupPaint();

        inkBuilder = Ink.builder();
    }

    void setupPaint() {
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(16);
    }

    void clear() {
        inkBuilder = Ink.builder();
        canvas.drawColor(Color.WHITE);
        invalidate();
    }

    Ink build() {
        return inkBuilder.build();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
        canvas.drawPath(path, paint);
    }

    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        path.reset();
        path.moveTo(x, y);
        mouseX = x;
        mouseY = y;

    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mouseX);
        float dy = Math.abs(y - mouseY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(mouseX, mouseY, (x + mouseX) / 2, (y + mouseY) / 2);
            mouseX = x;
            mouseY = y;
        }
    }

    private void touch_up() {
        path.lineTo(mouseX, mouseY);
        canvas.drawPath(path, paint);
        path.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        long t = System.currentTimeMillis();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();

                strokeBuilder = Ink.Stroke.builder();
                strokeBuilder.addPoint(Ink.Point.create(x, y, t));
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();

                strokeBuilder.addPoint(Ink.Point.create(x, y, t));
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();

                strokeBuilder.addPoint(Ink.Point.create(x, y, t));
                inkBuilder.addStroke(strokeBuilder.build());
                strokeBuilder = null;
                GameActivity.getInstance().checkAnswer();
                break;
        }
        return true;
    }
}