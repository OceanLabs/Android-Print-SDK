package ly.kite.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.*;

/*****************************************************
 *
 * This class provides various helper methods for
 * converting bitmaps in order to be stored in a
 * database
 *
 *****************************************************/
public class BitmapDBConverter {
    // convert bitmap to byte array
    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert byte array to bitmap
    public static Bitmap getBitmapFromBytes(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
