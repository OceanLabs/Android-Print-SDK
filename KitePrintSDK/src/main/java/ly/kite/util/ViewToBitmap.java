package ly.kite.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

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
     * Returns a bitmap version of the specified item from
     * the GridView (first item has position 0)
     * Returns blank image in case of failure
     *
     *****************************************************/
    public static Bitmap getItemBitmap(GridView view, int position) {
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
        if( (height == -1 && width == -1) || bitmap == null) {
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
     * Removes transparency from bitmap image
     *
     *****************************************************/
    public static Bitmap removeBitmapBlankSpace(Bitmap bitmap) {
        return removeBitmapBlankSpace(null, bitmap, false);
    }

    /*****************************************************
     *
     * Removes transparency from bitmap image
     *
     *****************************************************/
    public static Bitmap removeBitmapBlankSpace(Context context,Bitmap bitmap, boolean isReviewAndEdit) {

        if(bitmap == null) {
            return null;
        }
        int imageHeight = bitmap.getHeight();
        int imageWidth  = bitmap.getWidth();

        // Find top non-blank position
        int topPosition = 0;
        for(int y = 0; y < imageHeight; y++) {
            if (topPosition == 0) {
                for (int x = 0; x < imageWidth ; x++) {
                    if (bitmap.getPixel(x, y) != Color.TRANSPARENT) {
                        topPosition = y;
                        break;
                    }
                }
            } else break;
        }

        // Find right non-blank position
        int rightPosition  = 0;
        for(int x = imageWidth - 1; x >= 0; x--) {
            if (rightPosition == 0) {
                for (int y = 0; y < imageHeight; y++) {
                    if (bitmap.getPixel(x, y) != Color.TRANSPARENT) {
                        rightPosition = x;
                        break;
                    }
                }
            } else break;
        }

        // Find bottom non-blank position
        int bottomPosition = 0;
        for(int y = imageHeight - 1; y >= 0; y--) {
            if (bottomPosition == 0 ) {
                for (int x = 0; x < imageWidth; x++) {
                    if (bitmap.getPixel(x, y) != Color.TRANSPARENT) {
                        bottomPosition = y;
                        break;
                    }
                }
            } else break;
        }

        // Find left non-blank position
        int leftPosition = 0;
        for(int x = 0; x < imageWidth; x++) {
            if (leftPosition == 0) {
                for (int y = 0; y < imageHeight; y++) {
                    if (bitmap.getPixel(x, y) != Color.TRANSPARENT) {
                        leftPosition = x;
                        break;
                    }
                }
            } else break;
        }

        //for a review and edit image cut extra for the bottom buttons
        if( isReviewAndEdit ) {
            int dpToRemove = context.getResources().getDimensionPixelSize(R.dimen.cta_button_height);
            // add extra 10% to make sure the buttons are cropped
            dpToRemove += dpToRemove * 0.1;
            bottomPosition -= dpToRemove;
        }

        return Bitmap.createBitmap(
            bitmap,
            leftPosition,
            topPosition,
            rightPosition - leftPosition,
            bottomPosition - topPosition
        );

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
