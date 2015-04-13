package ly.kite.printshop;

/**
 * Created by kostas on 3/17/15.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

public class WhiteSquare extends View {
    Paint paint = new Paint();

    public WhiteSquare(Context context) {
        super(context);
    }

    public WhiteSquare(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    public WhiteSquare(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Point s = new Point(canvas.getWidth(), canvas.getHeight());
        float p = 0.2f;

        canvas.drawARGB(0,0,0,0);

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(s.y * p, s.y * p, s.x - (s.y * p), s.y * (1-p) , paint);

    }

}
