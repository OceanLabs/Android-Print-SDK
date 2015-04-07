package ly.kite.printshop;

import android.net.Uri;
import android.provider.MediaStore;

/**
 * @author Andreas C. Osowski
 */
public class MediaPhoto extends Photo {

    public MediaPhoto(long id) {
        super(PhotoSource.GALLERY, id);
    }

    public Uri getUri() {
        return Uri.withAppendedPath(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + getId()
        );
    }

}
