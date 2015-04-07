package ly.kite.printshop;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;

/**
 * @author Andreas C. Osowski
 */
public class PhotoEditBorderDrawable extends Drawable {
    private final Context context;

    private final static int COLOR_BORDER = Color.WHITE;
    private final static int COLOR_OVERLAY = 0x70000000;
    public final static float WINDOW_SIZE = 0.8f;
    private final static float EDGE_SIZE = 0.1f;

    private Paint paintBorder = new Paint();
    private Paint paintBorderEdge = new Paint();
    private Paint paintOverlay = new Paint();

    private RectF topRect, leftRect, bottomRect, rightRect;
    private Path borderPath, edgePath;

    public PhotoEditBorderDrawable(Context context) {
        this.context = context;

        init();
    }

    private void init() {
        paintBorder.setColor(COLOR_BORDER);
        paintBorder.setAntiAlias(true);
        paintBorder.setStrokeWidth(5);
        paintBorder.setStyle(Paint.Style.STROKE);

        paintBorderEdge.setColor(COLOR_BORDER);
        paintBorderEdge.setAntiAlias(true);
        paintBorderEdge.setStrokeWidth(15);
        paintBorderEdge.setStyle(Paint.Style.STROKE);


        paintOverlay.setColor(COLOR_OVERLAY);
        paintOverlay.setAntiAlias(true);
        paintOverlay.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        float width = bounds.width();
        float height = bounds.height();

        // As we only do portrait mode, we can assume that height >>> width
        float centerX = bounds.centerX();
        float centerY = bounds.centerY();

        // Square!
        float len = width * WINDOW_SIZE;
        float len_2 = len / 2;
        float horiz = centerX - len_2;
        float vert = centerY - len_2;

        borderPath = new Path();

        borderPath.moveTo(horiz, vert);
        borderPath.lineTo(horiz + len, vert);
        borderPath.lineTo(horiz + len, vert + len);
        borderPath.lineTo(horiz, vert + len);
        borderPath.close();

        edgePath = new Path();
        // The offset causes the edges to become a bit bigger.
        // Acceptable bug, as otherwise, we'd have to recaclulate our vars based on the relationship between len & off
        float off = paintBorder.getStrokeWidth() * 0.5f;

        // Top left
        edgePath.moveTo(horiz + off, vert + len * EDGE_SIZE + off);
        edgePath.lineTo(horiz + off, vert + off);
        edgePath.lineTo(horiz + len * EDGE_SIZE + off, vert + off);

        // Top Right
        edgePath.moveTo(horiz + len * (1 - EDGE_SIZE) - off, vert + off);
        edgePath.lineTo(horiz + len - off, vert + off);
        edgePath.lineTo(horiz + len - off, vert + len * EDGE_SIZE + off);

        // Bottom Right
        edgePath.moveTo(horiz + len - off, vert + len * (1 - EDGE_SIZE) - off);
        edgePath.lineTo(horiz + len - off, vert + len - off);
        edgePath.lineTo(horiz + len * (1 - EDGE_SIZE) - off, vert + len - off);

        // Bottom Left
        edgePath.moveTo(horiz + len * EDGE_SIZE + off, vert + len - off);
        edgePath.lineTo(horiz + off, vert + len - off);
        edgePath.lineTo(horiz + off, vert + len * (1 - EDGE_SIZE) - off);

        // PorterDuffModes drive me crazy. let's just create 4 rects and draw our overlay inside of them.
        topRect = new RectF(0, 0, width, vert);
        leftRect = new RectF(0, vert, horiz, vert + len);
        bottomRect = new RectF(0, vert + len, width, height);
        rightRect = new RectF(horiz + len, vert, width, vert + len);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(topRect, paintOverlay);
        canvas.drawRect(leftRect, paintOverlay);
        canvas.drawRect(bottomRect, paintOverlay);
        canvas.drawRect(rightRect, paintOverlay);
        canvas.drawPath(borderPath, paintBorder);
        canvas.drawPath(edgePath, paintBorderEdge);


    }

    public float getWindowSize() {
        return WINDOW_SIZE;
    }

    @Override
    public void setAlpha(int alpha) {
        // Nope
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // Nope
    }

    @Override
    public int getOpacity() {
        return 255;
    }
}
