package ly.kite.printshop;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
//import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import java.io.File;

import javax.inject.Inject;

//import co.oceanlabs.psprintstudio.PSApplication;
//import co.oceanlabs.psprintstudio.PSFragmentActivity;
//import co.oceanlabs.psprintstudio.R;
//import co.oceanlabs.psprintstudio.model.*;
//import co.oceanlabs.psprintstudio.util.ImageUtils;
import ly.kite.R;
import ly.kite.print.Asset;
import ly.kite.print.AssetGetBytesListener;
import ly.kite.printshop.Photo;

import com.almeros.android.multitouch.gesturedetectors.MoveGestureDetector;
import com.almeros.android.multitouch.gesturedetectors.RotateGestureDetector;

/**
 * @author Andreas C. Osowski
 */
public class PhotoEditActivity extends FragmentActivity implements View.OnTouchListener {

    private final static int SNAP_THRESHOLD = 5;

//    @Inject
//    protected PSApplication application;

    private static final int i = R.id.photoview;
    protected ImageView imageView;

    protected View overlayView;

    @Inject
    protected ImageLoader imageLoader;

    private Asset photo;
    private Bitmap bitmap;

    private Matrix matrix = new Matrix();
    private float scaleFactor = -1f;
    private float rotationDegrees = 0.f;
    private float focusX = 0.f;
    private float focusY = 0.f;
    private int imageHeight = 360, imageWidth = 640;

    private ScaleGestureDetector scaleDetector;
    private RotateGestureDetector rotateDetector;
    private MoveGestureDetector moveDetector;
    private float minScaleFactor;
    private float initialScaleFactor;
    private PhotoEditBorderDrawable borderDrawable;
    private AlertDialog dialog;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_edit);
//        setTitleStyled(getTitle());

        photo = (Asset) getIntent().getSerializableExtra("photo");

        imageView = (ImageView)findViewById(R.id.photoview);
        overlayView = findViewById(R.id.overlay);

//        if (photo.getTransform() != null) {
//            scaleFactor = photo.getTransform().getScaleFactor();
//            initialScaleFactor = photo.getTransform().getInitialScaleFactor();
//            rotationDegrees = photo.getTransform().getRotationDegrees();
//            focusX = photo.getTransform().getFocusX();
//            focusY = photo.getTransform().getFocusY();
//        }

        borderDrawable = new PhotoEditBorderDrawable(this);
        overlayView.setBackgroundDrawable(borderDrawable);


        // Setup Gesture Detectors
        scaleDetector = new ScaleGestureDetector(getApplicationContext(), new ScaleListener());
        rotateDetector = new RotateGestureDetector(getApplicationContext(), new RotateListener());
        moveDetector = new MoveGestureDetector(getApplicationContext(), new MoveListener());

//        dialog = new ProgressDialog.Builder(PhotoEditActivity.this)
//                .setCancelable(false)
//                .setTitle(R.string.loading)
//                .setMessage(R.string.please_wait)
//                .show();


        photo.getBytes(this, new AssetGetBytesListener() {
            @Override
            public void onBytes(Asset asset, byte[] bytes) {
                Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                loadBitmap(image);
            }

            @Override
            public void onError(Asset asset, Exception ex) {

            }
        });

    }

    private void loadBitmap(Bitmap bmp) {
        this.bitmap = bmp;
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);

        imageHeight = drawable.getIntrinsicHeight();
        imageWidth = drawable.getIntrinsicWidth();

        imageView.setOnTouchListener(this);

        final BitmapDrawable finalDrawable = drawable;
        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (scaleFactor == -1f) {
                    float ww = overlayView.getMeasuredWidth() * borderDrawable.getWindowSize();

                    float imw = finalDrawable.getIntrinsicWidth();
                    float imh = finalDrawable.getIntrinsicHeight();

                    // Calculate scale ratio.

                    if (imw >= imh) {
                        scaleFactor = ww / imh;
                    } else {
                        scaleFactor = ww / imw;
                    }

                    initialScaleFactor = scaleFactor;
                    minScaleFactor = 0.5f * initialScaleFactor;

                    //Center image
                    focusX = overlayView.getMeasuredWidth() / 2;
                    focusY = overlayView.getMeasuredHeight() / 2;

                    recalculateMatrix();
                }

                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });

        imageView.setImageDrawable(drawable);
        recalculateMatrix();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.edit, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.menu_save) {
//
//            PhotoTransform transform = new PhotoTransform(
//                    bitmap.getWidth(),
//                    bitmap.getHeight(),
//                    scaleFactor / initialScaleFactor,
//                    initialScaleFactor,
//                    focusX,
//                    focusY,
//                    rotationDegrees,
//                    overlayView.getMeasuredWidth(),
//                    overlayView.getMeasuredHeight()
//            );
//
//            photo.setTransform(transform);
//            new RenderPhotoTask().execute();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

//    private void renderPhoto() {
//        File out = photo.getRendered();
//        if (out != null && out.exists())
//            out.delete();
//
//        out = new File(getApplicationContext().getCacheDir(), photo.getId() + "_" + System.currentTimeMillis() + ".jpg");
//
//        int len = getIntent().getIntExtra("len", -1);
//        if (len == -1)
//            throw new RuntimeException("no length provided!");
////        Matrix m = photo.getTransform().getOutputMatrix(len);
//
//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setDither(true);
//        paint.setFilterBitmap(true);
//
//        Bitmap bmp = Bitmap.createBitmap(len, len, Bitmap.Config.ARGB_8888);
//        bmp.setHasAlpha(false);
//
//        Canvas c = new Canvas(bmp);
//        c.drawColor(Color.WHITE);
//        c.drawBitmap(bitmap, 0, 0, paint);
//        bitmap.recycle();
//        ImageUtils.saveBitmap(bmp, out);
//
//
//        final String absPath = out.getAbsolutePath();
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                imageLoader.loadImage(absPath, new DisplayImageOptions.Builder().cacheInMemory(true).build(), null);
//            }
//        });
//
//        photo.setRendered(out);
//    }

    @SuppressWarnings("deprecation")
    public boolean onTouch(View v, MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        rotateDetector.onTouchEvent(event);
        moveDetector.onTouchEvent(event);

        recalculateMatrix();

        return true;
    }

    private void recalculateMatrix() {
        float scaledImageCenterX = (imageWidth * scaleFactor) / 2;
        float scaledImageCenterY = (imageHeight * scaleFactor) / 2;

        matrix.reset();
        matrix.postScale(scaleFactor, scaleFactor);
        matrix.postRotate(rotationDegrees, scaledImageCenterX, scaledImageCenterY);
        matrix.postTranslate(focusX - scaledImageCenterX, focusY - scaledImageCenterY);

        imageView.setImageMatrix(matrix);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            // Don't let the object get too small or too large.
            scaleFactor = Math.max(minScaleFactor, Math.min(scaleFactor, 20.0f));

            return true;
        }
    }

    private class RotateListener extends RotateGestureDetector.SimpleOnRotateGestureListener {
        @Override
        public boolean onRotate(RotateGestureDetector detector) {
            rotationDegrees -= detector.getRotationDegreesDelta();

            return true;
        }

        @Override
        public void onRotateEnd(RotateGestureDetector detector) {
            rotationDegrees = rotationDegrees % 360;
            if (rotationDegrees < 0)
                rotationDegrees += 360;

            float diff = rotationDegrees % 90;
            if (diff <= SNAP_THRESHOLD) {
                rotationDegrees -= diff;
            } else if ((90 - diff) <= SNAP_THRESHOLD) {
                rotationDegrees += 90 - diff;
            }
        }
    }

    private class MoveListener extends MoveGestureDetector.SimpleOnMoveGestureListener {
        @Override
        public boolean onMove(MoveGestureDetector detector) {
            PointF d = detector.getFocusDelta();
            focusX += d.x;
            focusY += d.y;

            return true;
        }
    }

//    private class RenderPhotoTask extends AsyncTask<Void, Void, Void> {
//
//        private AlertDialog dialog;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            dialog = new ProgressDialog.Builder(PhotoEditActivity.this)
//                    .setCancelable(false)
//                    .setTitle(R.string.saving)
//                    .show();
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            dialog.cancel();
//
//            Intent i = new Intent();
//            i.putExtra("pos", getIntent().getIntExtra("pos", -1));
//            i.putExtra("photo", photo);
//            setResult(RESULT_OK, i);
//            finish();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            renderPhoto();
//            return null;
//        }
//    }
}
