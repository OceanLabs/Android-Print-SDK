package ly.kite.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import ly.kite.R;

/*****************************************************
 *
 * This class converts views to bitmap images
 *
 *****************************************************/
public class ViewToBitmap {

    private static final String LOG_TAG = "ViewToBitmap";

    /*****************************************************
     *
     * Returns a bitmap version of the RecyclerView
     * exactly how it appears onscreen
     * Returns blank image in case of failure
     *
     *****************************************************/
    public static Bitmap getFullBitmap(View view) {
        try {
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
            return bitmap;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Unable to get item bitmap", e);
            return getBlankBitmap();
        }
    }

    /*****************************************************
     *
     * Returns a bitmap version of the specified item from
     * the RecyclerView (first item has position 0)
     * Returns blank image in case of failure
     *
     *****************************************************/
    public static Bitmap getItemBitmap(RecyclerView view, int position) {
        try {
            View item = view.getChildAt(position);
            item.setDrawingCacheEnabled(true);
            item.buildDrawingCache(true);
            Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
            item.setDrawingCacheEnabled(false);
            return bitmap;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Unable to get item bitmap", e);
            return getBlankBitmap();
        }
    }

    /*****************************************************
     *
     * Returns a bitmap with the set width and height but
     * does not keep the aspect ratio
     *
     *****************************************************/
    public static Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
        if(height == -1 && width == -1) {
            return bitmap;
        }
        if(width == -1) {
            float ratio = ((float) bitmap.getWidth()) / (float) (bitmap.getHeight());
            int newWidth =  (int) (ratio * height);

            return Bitmap.createScaledBitmap(bitmap, newWidth, height, false);
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    /*****************************************************
     *
     * Returns a bitmap with the set height maintaining the
     * aspect ratio
     *
     *****************************************************/
    public static Bitmap resizeBitmap(Bitmap bitmap, int height) {
        return resizeBitmap(bitmap, -1, height);
    }

    /*****************************************************
     *
     * Returns a bitmap with the size set for preview
     * image
     *
     *****************************************************/
    public static Bitmap resizeAsPreviewImage(Context context, Bitmap bitmap) {
        int imageHeight = context.getResources().getDimensionPixelSize(R.dimen.product_preview_size);
        return resizeBitmap(bitmap, imageHeight);
    }

    /*****************************************************
     *
     * Returns a 100px by 100px blank bitmap
     *
     *****************************************************/
    private static Bitmap getBlankBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.WHITE);
        return bitmap;
    }
}
