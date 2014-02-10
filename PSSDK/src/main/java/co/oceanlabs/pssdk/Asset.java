package co.oceanlabs.pssdk;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by deonbotha on 06/02/2014.
 */
public class Asset {

    public static enum MimeType {
        JPEG("image/jpeg"),
        PNG("image/png");

        private final String mimeTypeString;

        MimeType(String mimeTypeString) {
            this.mimeTypeString = mimeTypeString;
        }

        public String getMimeTypeString() {
            return mimeTypeString;
        }

        public static MimeType fromString(String mimeType) {
            if (mimeType.equalsIgnoreCase(JPEG.mimeTypeString)) {
                return JPEG;
            } else if (mimeType.equalsIgnoreCase(PNG.mimeTypeString)) {
                return PNG;
            } else {
                throw new UnsupportedOperationException("Requested mimetype " + mimeType + " is not supported");
            }
        }
    }

    static enum AssetType {
        IMAGE_URI,
        BITMAP_RESOURCE_ID,
        IMAGE_BYTES,
        IMAGE_PATH,
        REMOTE_URL
    }

    private Uri imageUri;
    private URL remoteURL;
    private int bitmapResourceId;
    private String imagePath;
    private byte[] imageBytes;
    private MimeType mimeType;
    private AssetType type;

    private boolean uploaded;

    // The next two are only valid once an asset has been uploaded to the server
    private long id;
    private URL previewURL;

    public Asset(Uri uri) {
        if (!uri.getScheme().equalsIgnoreCase("content") /*&& !uri.getScheme().equalsIgnoreCase("http") && !uri.getScheme().equalsIgnoreCase("https")*/) {
            throw new IllegalArgumentException("Only uris with content schemes are currently supported, your scheme " + uri.getScheme() + " is not");
        }

        this.type = AssetType.IMAGE_URI;
        this.imageUri = uri;
    }

    public Asset(URL url) {
        if (!url.getProtocol().equalsIgnoreCase("http") && !url.getProtocol().equalsIgnoreCase("https")) {
            throw new IllegalArgumentException("currently only support http and https URL schemes");
        }

        this.type = AssetType.REMOTE_URL;
        this.remoteURL = url;

        String file = url.getFile().toLowerCase();
        if (file.endsWith(".jpg") || file.endsWith(".jpeg")) {
            this.mimeType = MimeType.JPEG;
        } else if (file.endsWith("png")) {
            this.mimeType = MimeType.PNG;
        } else {
            throw new IllegalArgumentException("currently support URL's the identify the mime type by ending with a supported file extension i.e. '.jpeg', '.jpg' or '.png'");
        }
    }

    public Asset(String imagePath) {
        String path = imagePath.toLowerCase();
        if (!path.endsWith("jpeg") && !path.endsWith("jpg") && !path.endsWith("png")) {
            throw new IllegalArgumentException("Currently only JPEG & PNG assets are supported");
        }

        this.type = AssetType.IMAGE_PATH;
        this.imagePath = imagePath;
    }

    public Asset(int bitmapResourceId) {
        this.type = AssetType.BITMAP_RESOURCE_ID;
        this.bitmapResourceId = bitmapResourceId;
    }

    public Asset(byte[] imageBytes, MimeType mimeType) {
        this.type = AssetType.IMAGE_BYTES;
        this.imageBytes = imageBytes;
        this.mimeType = mimeType;
    }

    AssetType getType() {
        return type;
    }

    URL getRemoteURL() {
        return remoteURL;
    }

    void markAsUploaded(long assetId, URL previewURL) {
        this.uploaded = true;
        this.id = assetId;
        this.previewURL = previewURL;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public long getId() {
        assert uploaded : "id is only valid once an asset has been uploaded";
        return id;
    }

    public URL getPreviewURL() {
        assert uploaded : "preview url is only valid once an asset has been uploaded to the server";
        return previewURL;
    }

    public MimeType getMimeType(Context c) {
        switch (type) {
            case BITMAP_RESOURCE_ID:
                return MimeType.JPEG; // TODO: actually look up mimetype correctly
            case IMAGE_BYTES:
                return mimeType;
            case IMAGE_URI:
                ContentResolver cR = c.getContentResolver();
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                return MimeType.fromString(cR.getType(imageUri));
            case IMAGE_PATH:
                String path = imagePath.toLowerCase();
                if (path.endsWith("jpeg") || path.endsWith("jpg")) {
                    return MimeType.JPEG;
                } else if (path.endsWith("png")) {
                    return MimeType.PNG;
                } else {
                    throw new IllegalStateException("Currently only JPEG & PNG assets are supported");
                }
            case REMOTE_URL:
                return mimeType;
            default:
                throw new IllegalStateException("should never arrive here");
        }
    }

    public void getBytesLength(final Context c, final AssetGetBytesLengthListener listener) {

        AsyncTask<Void, Void, Object> t = new AsyncTask<Void, Void, Object>() {
            @Override
            protected Object doInBackground(Void... voids) {
                switch (type) {
                    case BITMAP_RESOURCE_ID: {
                        InputStream is = c.getResources().openRawResource(bitmapResourceId);
                        try {
                            int avail = is.available();
                            is.close();
                            return Long.valueOf(avail);
                        } catch (IOException e) {
                           return e;
                        }
                    }
                    case IMAGE_BYTES:
                        return Long.valueOf(imageBytes.length);
                    case IMAGE_URI: {
                        InputStream is = null;
                        try {
                            is = c.getContentResolver().openInputStream(imageUri);
                            long avail = is.available();
                            return Long.valueOf(avail);
                        } catch (Exception ex) {
                            return ex;
                        } finally {
                            try {
                                is.close();
                            } catch (Exception ex) {/* Ignore as we're already returning something */}
                        }
                    }
                    case IMAGE_PATH: {
                        File file = new File(imagePath);
                        return Long.valueOf(file.length());
                    }
                    case REMOTE_URL:
                        return Long.valueOf(0);
                    default:
                        throw new IllegalStateException("should never arrive here");
                }
            }

            @Override
            protected void onPostExecute(Object o) {
                if (o instanceof Exception) {
                    listener.onError(Asset.this, (Exception) o);
                } else {
                    listener.onBytesLength(Asset.this, ((Long) o).longValue());
                }
            }
        };

        t.execute();
    }

    private Object readBytesOrError(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(is.available());
            byte[] buffer = new byte[8192];
            int numRead = -1;
            while ((numRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, numRead);
            }

            return baos.toByteArray();
        } catch (IOException e) {
            return e;
        } finally {
            try {
                is.close();
            } catch(IOException ex) {/* Already returning something so just ignore this one */}
        }
    }

    public void getBytes(final Context c, final AssetGetBytesListener listener) {
        AsyncTask<Void, Void, Object> t = new AsyncTask<Void, Void, Object>() {
            @Override
            protected Object doInBackground(Void... voids) {
                switch (type) {
                    case BITMAP_RESOURCE_ID: {
                        BufferedInputStream is = new BufferedInputStream(c.getResources().openRawResource(bitmapResourceId));
                        return readBytesOrError(is);
                    }
                    case IMAGE_BYTES:
                        return imageBytes;
                    case IMAGE_URI: {
                        try {
                            BufferedInputStream is = new BufferedInputStream(c.getContentResolver().openInputStream(imageUri));
                            return readBytesOrError(is);
                        } catch (FileNotFoundException ex) {
                            return ex;
                        }
                    }
                    case IMAGE_PATH: {
                        try {
                            File file = new File(imagePath);
                            BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
                            return readBytesOrError(is);
                        } catch (FileNotFoundException ex) {
                            return ex;
                        }
                    }
                    case REMOTE_URL:
                        throw new UnsupportedOperationException("Getting the bytes of a remote url is not supported!");
                    default:
                        throw new IllegalStateException("should never arrive here");
                }
            }

            @Override
            protected void onPostExecute(Object o) {
                if (o instanceof Exception) {
                    listener.onError(Asset.this, (Exception) o);
                } else {
                    listener.onBytes(Asset.this, (byte[]) o);
                }
            }
        };

        t.execute();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof  Asset)) {
            return false;
        }

        Asset a = (Asset) o;
        if (a == this) {
            return true;
        }

        if (a.mimeType != this.mimeType || a.type != this.type) {
            return false;
        }

        switch (this.type) {
            case REMOTE_URL:
                return a.remoteURL.equals(this.remoteURL);
            case IMAGE_URI:
                return a.imageUri.equals(this.imageUri);
            case IMAGE_PATH:
                return a.imagePath.equals(this.imagePath);
            case BITMAP_RESOURCE_ID:
                return a.bitmapResourceId == this.bitmapResourceId;
            case IMAGE_BYTES:
                return Arrays.equals(a.imageBytes, this.imageBytes);
        }

        throw new IllegalStateException("should not get here");
    }

    @Override
    public int hashCode() {
        switch (this.type) {
            case REMOTE_URL:
                return this.remoteURL.hashCode();
            case IMAGE_URI:
                return this.imageUri.hashCode();
            case IMAGE_PATH:
                return this.imagePath.hashCode();
            case BITMAP_RESOURCE_ID:
                return bitmapResourceId;
            case IMAGE_BYTES:
                return Arrays.hashCode(this.imageBytes);
        }

        throw new IllegalStateException("should not get here");
    }
}
