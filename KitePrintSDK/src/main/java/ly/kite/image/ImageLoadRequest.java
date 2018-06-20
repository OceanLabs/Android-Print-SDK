/*****************************************************
 *
 * ImageLoadRequest.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2016 Kite Tech Ltd. https://www.kite.ly
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The software MAY ONLY be used with the Kite Tech Ltd platform and MAY NOT be modified
 * to be used with any competitor platforms. This means the software MAY NOT be modified 
 * to place orders with any competitors to Kite Tech Ltd, all orders MUST go through the
 * Kite Tech Ltd platform servers. 
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 *****************************************************/

///// Package Declaration /////

package ly.kite.image;


///// Import(s) /////

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import ly.kite.KiteSDK;
import ly.kite.util.Asset;
import ly.kite.util.AssetFragment;
import ly.kite.util.AssetHelper;
import ly.kite.util.FileDownloader;


///// Class Declaration /////

/*****************************************************
 *
 * This class is a request for an image to be loaded.
 *
 *****************************************************/
public class ImageLoadRequest
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG              = "ImageLoadRequest";

  static private final boolean FORCE_FILE_DOWNLOAD  = false;

  static private final int     MAX_SUB_SAMPLE_SIZE  = Integer.MAX_VALUE;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Context             mApplicationContext;

  private ASource             mSource;
  private ATarget             mTarget;
  private IImageSizeConsumer  mImageSizeConsumer;

  private IImageTransformer   mPreResizeTransformer;
  private IImageTransformer   mPostResizeTransformer;

  private int                 mResizeWidth;
  private int                 mResizeHeight;

  private boolean             mOnlyLoadBounds;
  private boolean             mOnlyScaleDown;
  private Bitmap.Config       mBitmapConfig;

  private Size                mOriginalSize;
  //private int                 mOriginalWidth;
  //private int                 mOriginalHeight;
  private Bitmap              mBitmap;
  private Exception           mException;
  private boolean             mIsHighPriority = false;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns a scale size to bring a bitmap width down to
   * a resize width.
   *
   * Uses a centre inside algorithm to calculate the
   * scaling, i.e. both scaled dimensions should lie
   * within their respective resize dimensions.
   *
   *****************************************************/
  static int sampleSizeForResize( int originalWidth, int originalHeight, int resizeWidth, int resizeHeight )
    {
    int sampleSize = 1;

    if ( resizeWidth > 0 && resizeHeight > 0 )
      {
      int width      = originalWidth;
      int height     = originalHeight;
      int nextWidth  = width  >>> 1;  // / 2
      int nextHeight = height >>> 1;  // / 2

      while ( nextWidth > 0 && nextHeight > 0 && ( nextWidth >= resizeWidth || nextHeight >= resizeHeight ) )
        {
        width        = nextWidth;
        height       = nextHeight;
        sampleSize <<= 1;             //  * 2
        nextWidth    = width  >>> 1;  //  / 2
        nextHeight   = height >>> 1;  //  / 2
        }
      }

    return ( sampleSize );
    }


  /*****************************************************
   *
   * Returns a bitmap options object with common options
   * set.
   *
   *****************************************************/
  static private BitmapFactory.Options getCommonBitmapOptions( Bitmap.Config bitmapConfig )
    {
    BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options();

    bitmapFactoryOptions.inBitmap                 = null;
    bitmapFactoryOptions.inDensity                = 0;
    bitmapFactoryOptions.inDither                 = false;
    bitmapFactoryOptions.inPreferQualityOverSpeed = false;
    bitmapFactoryOptions.inPreferredConfig        = bitmapConfig;
    bitmapFactoryOptions.inScaled                 = false;
    bitmapFactoryOptions.inScreenDensity 	        = 0;
    bitmapFactoryOptions.inTargetDensity 	        = 0;
    bitmapFactoryOptions.inTempStorage 	          = null;
    bitmapFactoryOptions.mCancel                  = false;

    return ( bitmapFactoryOptions );
    }


  /*****************************************************
   *
   * Returns a bitmap options object for decoding bounds
   * only.
   *
   *****************************************************/
  static private BitmapFactory.Options getBoundsBitmapOptions()
    {
    BitmapFactory.Options bitmapFactoryOptions = getCommonBitmapOptions( Bitmap.Config.RGB_565 );

    bitmapFactoryOptions.inJustDecodeBounds       = true;
    bitmapFactoryOptions.inMutable                = false;
    bitmapFactoryOptions.inSampleSize             = 0;

    return ( bitmapFactoryOptions );
    }


  /*****************************************************
   *
   * Returns a bitmap options object for decoding.
   *
   *****************************************************/
  static private BitmapFactory.Options getFullBitmapOptions( Bitmap.Config bitmapConfig, int sampleSize )
    {
    BitmapFactory.Options bitmapFactoryOptions = getCommonBitmapOptions( bitmapConfig );

    bitmapFactoryOptions.inJustDecodeBounds       = false;
    bitmapFactoryOptions.inMutable                = true;
    bitmapFactoryOptions.inSampleSize             = sampleSize;

    return ( bitmapFactoryOptions );
    }


  /*****************************************************
   *
   * Returns the orientation for an image. Used to determine
   * how to rotate the image after loading so that it becomes
   * the right way up.
   *
   *****************************************************/
  static public int getRotationForImage( Context context, Uri uri )
    {
    if ( KiteSDK.DEBUG_IMAGE_LOADING )
      {
      Log.d( LOG_TAG, "getRotationForImage( context, uri = " + ( uri != null ? uri.toString() : "null" ) + " )" );
      }

    Cursor cursor = null;

    try
      {
      if ( KiteSDK.DEBUG_IMAGE_LOADING ) Log.d( LOG_TAG, "  URI scheme = " + uri.getScheme() );

      if ( uri.getScheme().equals( "content" ) )
        {
        ///// Content /////

        String[] projection = { MediaStore.Images.ImageColumns.ORIENTATION };

        cursor = context.getContentResolver().query( uri, projection, null, null, null );

        if ( cursor.moveToFirst() )
          {
          int rotation = cursor.getInt( 0 );

          if ( KiteSDK.DEBUG_IMAGE_LOADING ) Log.d( LOG_TAG, "  Rotation = " + rotation );

          return ( rotation );
          }
        }
      else if ( uri.getScheme().equals( "file" ) )
        {
        ///// File /////

        if ( KiteSDK.DEBUG_IMAGE_LOADING ) Log.d( LOG_TAG, "  URI path = " + uri.getPath() );

        ExifInterface exif = new ExifInterface( uri.getPath() );

        int rotation = degreesFromEXIFOrientation( exif.getAttributeInt( ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL ) );

        if ( KiteSDK.DEBUG_IMAGE_LOADING ) Log.d( LOG_TAG, "  Rotation = " + rotation );

        return ( rotation );
        }
      }
    catch ( IOException ioe )
      {
      Log.e( LOG_TAG, "Error checking exif", ioe );
      }
    finally
      {
      if ( cursor != null ) cursor.close();
      }

    return ( 0 );
    }


  /*****************************************************
   *
   * Returns TRUE is it should be treated as high priority
   *
   *****************************************************/
  public boolean getPriority()
    {
    return mIsHighPriority;
    }

  /*****************************************************
   *
   * Converts an EXIF orientation into degrees..
   *
   *****************************************************/
  static int degreesFromEXIFOrientation( int exifOrientation )
    {
    if ( KiteSDK.DEBUG_IMAGE_LOADING ) Log.d( LOG_TAG, "degreesFromEXIFOrientation( exifOrientation = " + exifOrientation + " )" );

    if ( exifOrientation == ExifInterface.ORIENTATION_ROTATE_90 )
      {
      return ( 90 );
      }
    else if ( exifOrientation == ExifInterface.ORIENTATION_ROTATE_180 )
      {
      return ( 180 );
      }
    else if ( exifOrientation == ExifInterface.ORIENTATION_ROTATE_270 )
      {
      return ( 270 );
      }

    return ( 0 );
    }


  ////////// Constructor(s) //////////

  ImageLoadRequest( Context context )
    {
    mApplicationContext = context.getApplicationContext();
    mBitmapConfig       = Bitmap.Config.ARGB_8888;
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets the image source.
   *
   *****************************************************/
  void setSource( ASource source )
    {
    // If the source has already been set, throw an exception
    if ( mSource != null ) throw ( new IllegalStateException( "An image source has already been set" ) );

    mSource = source;
    }


  /*****************************************************
   *
   * Sets the image source as a bitmap resource.
   *
   *****************************************************/
  void setSource( int bitmapResourceId )
    {
    setSource( new BitmapResourceSource( bitmapResourceId ) );
    }


  /*****************************************************
   *
   * Sets the image target.
   *
   *****************************************************/
  void setTarget( ATarget target )
    {
    // If the target has already been set, throw an exception
    if ( mTarget != null ) throw ( new IllegalStateException( "An image target has already been set" ) );

    mTarget = target;
    }


  /*****************************************************
   *
   * Executes the request.
   *
   *****************************************************/
  public void execute()
    {
    // Check we have all we need

    if ( mOnlyLoadBounds )
      {
      if ( mImageSizeConsumer == null ) throw ( new IllegalStateException( "No image size consumer has been specified" ) );
      }
    else
      {
      if ( mSource == null ) throw ( new IllegalStateException( "No image source has been specified" ) );
      if ( mTarget == null ) throw ( new IllegalStateException( "No image target has been specified" ) );
      }



    // Request an image load. If a bitmap is returned immediately, deliver it
    // to the target.

    mBitmap = mSource.load();

    if ( mBitmap != null )
      {
      mTarget.onImageAvailable( mBitmap );
      }
    }


  /*****************************************************
   *
   * Called by the request processor on a background
   * thread.
   *
   *****************************************************/
  boolean processInBackground()
    {
    Bitmap bitmap = null;

    try
      {
      // First decode the bitmap to get its size

      mOriginalSize = mSource.getSize( mApplicationContext );

      //BitmapFactory.Options bitmapFactoryOptions = getBoundsBitmapOptions();

      //mSource.load( mApplicationContext, bitmapFactoryOptions );

      //mOriginalWidth  = bitmapFactoryOptions.outWidth;
      //mOriginalHeight = bitmapFactoryOptions.outHeight;

      if ( KiteSDK.DEBUG_IMAGE_LOADING ) Log.d( LOG_TAG, "Original bitmap size = " + mOriginalSize.width + " x " + mOriginalSize.height );


      // If we only need to get the size, return now
      if ( mOnlyLoadBounds ) return ( true );


      // If resizing has been requested, sub-sample the bitmap to just larger
      // than the resize dimensions.

      int sampleSize = sampleSizeForResize( mOriginalSize.width, mOriginalSize.height, mResizeWidth, mResizeHeight );

      if ( KiteSDK.DEBUG_IMAGE_LOADING ) Log.d( LOG_TAG, "Sample size for scaling to " + mResizeWidth + " x " + mResizeHeight + " = " + sampleSize );


      // Image loading *must* work. So even if colour space reduction or resizing hasn't
      // been specified, do it anyway if we run out of memory.

      try
        {
        bitmap = getBitmap( mBitmapConfig, sampleSize );

        if ( bitmap != null )
          {
          mBitmap = bitmap;

          return ( true );
          }
        }
      catch ( OutOfMemoryError oome )
        {
        if ( KiteSDK.DEBUG_IMAGE_LOADING ) Log.d( LOG_TAG, "Caught out of memory error" );

        // Fall through
        }


      // We ran out of memory. If we were using a larger colour space, try again
      // using a lower one.

      if ( mBitmapConfig != Bitmap.Config.RGB_565 )
        {
        if ( KiteSDK.DEBUG_IMAGE_LOADING ) Log.d( LOG_TAG, "Dropping bitmap config -> RGB 565" );

        mBitmapConfig = Bitmap.Config.RGB_565;

        try
          {
          bitmap = getBitmap( mBitmapConfig, sampleSize );

          if ( bitmap != null )
            {
            mBitmap = bitmap;

            return ( true );
            }
          }
        catch ( OutOfMemoryError oome )
          {
          if ( KiteSDK.DEBUG_IMAGE_LOADING ) Log.d( LOG_TAG, "Got out of memory error" );

          // Fall through
          }
        }


      // We ran out of memory again. Try dropping the image size until we
      // succeed.

      while ( sampleSize >= 1 && sampleSize < MAX_SUB_SAMPLE_SIZE )
        {
        sampleSize <<= 1;  // * 2

        if ( KiteSDK.DEBUG_IMAGE_LOADING ) Log.d( LOG_TAG, "Increased sample size -> " + sampleSize );

        try
          {
          bitmap = getBitmap( mBitmapConfig, sampleSize );

          if ( bitmap != null )
            {
            mBitmap = bitmap;

            return ( true );
            }
          }
        catch ( OutOfMemoryError oome )
          {
          if ( KiteSDK.DEBUG_IMAGE_LOADING ) Log.d( LOG_TAG, "Got out of memory error" );

          // Fall through
          }
        }

      }
    catch ( Exception exception )
      {
      Log.d( LOG_TAG, "Unable to load image", exception );

      mException = exception;
      }


    return ( true );
    }


  /*****************************************************
   *
   * Loads, transforms, and resizes a bitmap.
   *
   *****************************************************/
  private Bitmap getBitmap( Bitmap.Config bitmapConfig, int sampleSize ) throws Exception
    {
    if ( KiteSDK.DEBUG_IMAGE_LOADING ) Log.d( LOG_TAG, "getBitmap( bitmapConfig = " + bitmapConfig + ", sampleSize = " + sampleSize );


    // Load the image, sub-sampling if specified

    BitmapFactory.Options bitmapFactoryOptions = getFullBitmapOptions( bitmapConfig, sampleSize );

    Bitmap bitmap = mSource.load( mApplicationContext, bitmapFactoryOptions );

    if ( KiteSDK.DEBUG_IMAGE_LOADING ) Log.d( LOG_TAG, "Bitmap loaded: " + bitmap.getWidth() + " x " + bitmap.getHeight() + ", config = " + bitmap.getConfig() );


    // Apply any pre-resize transformation

    if ( mPreResizeTransformer != null )
      {
      if ( KiteSDK.DEBUG_IMAGE_LOADING ) Log.d( LOG_TAG, "Applying pre-resize transformer: " + mPreResizeTransformer );

      bitmap = mPreResizeTransformer.getTransformedBitmap( bitmap );

      if ( KiteSDK.DEBUG_IMAGE_LOADING ) Log.d( LOG_TAG, "Bitmap transformed: " + bitmap.getWidth() + " x " + bitmap.getHeight() );
      }


    // Perform any scaling

    if ( mResizeWidth > 0 && mResizeHeight > 0 )
      {
      bitmap = ImageAgent.scaleBitmap( bitmap, mResizeWidth, mResizeHeight, mOnlyScaleDown );

      if ( KiteSDK.DEBUG_IMAGE_LOADING ) Log.d( LOG_TAG, "Bitmap scaled: " + bitmap.getWidth() + " x " + bitmap.getHeight() );
      }


    // Apply any post-resize transformation

    if ( mPostResizeTransformer != null )
      {
      if ( KiteSDK.DEBUG_IMAGE_LOADING ) Log.d( LOG_TAG, "Applying post-resize transformer: " + mPostResizeTransformer );

      bitmap = mPostResizeTransformer.getTransformedBitmap( bitmap );

      if ( KiteSDK.DEBUG_IMAGE_LOADING ) Log.d( LOG_TAG, "Bitmap transformed: " + bitmap.getWidth() + " x " + bitmap.getHeight() );
      }


    return ( bitmap );
    }


  /*****************************************************
   *
   * Called by the request processor on the UI
   * thread.
   *
   *****************************************************/
  void onProcessingComplete()
    {
    // Check for a size consumer
    if ( mImageSizeConsumer != null )
      {
      if ( mException == null ) mImageSizeConsumer.onImageSizeAvailable( mOriginalSize.width, mOriginalSize.height );
      else                      mImageSizeConsumer.onImageSizeUnavailable( mException );
      }

    // Check for a target
    if ( mTarget != null )
      {
      if      ( mBitmap    != null ) mTarget.onImageAvailable( mBitmap );
      else if ( mException != null ) mTarget.onImageUnavailable( mException );
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A size.
   *
   *****************************************************/
  private class Size
    {
    int width;
    int height;

    Size( int width, int height )
      {
      this.width  = width;
      this.height = height;
      }
    }


  /*****************************************************
   *
   * An image source.
   *
   *****************************************************/
  private abstract class ASource
    {
    /*****************************************************
     *
     * Called when the request is executed, to load the image.
     *
     *****************************************************/
    Bitmap load()
      {
      // The default implementation puts this request on the processing queue
      ImageRequestProcessor.getInstance( mApplicationContext ).process( ImageLoadRequest.this );

      return ( null );
      }


    /*****************************************************
     *
     * Returns the size of an image, keeping in consideration
     * any rotation that is applied.
     *
     *****************************************************/
    protected Size getRotatedImageSize( Context context, BitmapFactory.Options bitmapFactoryOptions, Uri uri )
      {
      int rotation = getRotationForImage( context, uri );

      int width;
      int height;

      if ( rotation == 90 || rotation == 270 )
        {
        width  = bitmapFactoryOptions.outHeight;
        height = bitmapFactoryOptions.outWidth;
        }
      else
        {
        width  = bitmapFactoryOptions.outWidth;
        height = bitmapFactoryOptions.outHeight;
        }

      return ( new Size( width, height ) );
      }


    /*****************************************************
     *
     * Performs any rotation to the image.
     *
     *****************************************************/
    protected Bitmap rotate( Bitmap bitmap, int rotation )
      {
      // Perform any rotation specified by the EXIF data

      if ( bitmap != null && rotation != 0 )
        {
        int targetWidth  = bitmap.getWidth();
        int targetHeight = bitmap.getHeight();


        // Transforming the bitmap with a rotational matrix

        Matrix matrix = new Matrix();

        matrix.preRotate( rotation );

        bitmap = Bitmap.createBitmap( bitmap, 0, 0, targetWidth, targetHeight, matrix, true );
        }

      return ( bitmap );
      }


    /*****************************************************
     *
     * Called to decode the image using the supplied options,
     * on a background thread.
     *
     *****************************************************/
    abstract Size getSize( Context context ) throws Exception;


    /*****************************************************
     *
     * Called to decode the image using the supplied options,
     * on a background thread.
     *
     *****************************************************/
    abstract Bitmap load( Context context, BitmapFactory.Options bitmapFactoryOptions ) throws Exception;
    }


  /*****************************************************
   *
   * A bitmap data image source.
   *
   *****************************************************/
  private class BitmapBytesSource extends ASource
    {
    private byte[]  mSourceBitmapBytes;


    BitmapBytesSource( byte[] bitmapBytes )
      {
      mSourceBitmapBytes = bitmapBytes;
      }


    @Override
    Size getSize( Context context )
      {
      BitmapFactory.Options bitmapFactoryOptions = getBoundsBitmapOptions();

      load( context, bitmapFactoryOptions );

      return ( new Size( bitmapFactoryOptions.outWidth, bitmapFactoryOptions.outHeight ) );
      }


    @Override
    Bitmap load( Context context, BitmapFactory.Options bitmapFactoryOptions )
      {
      return ( BitmapFactory.decodeByteArray( mSourceBitmapBytes, 0, mSourceBitmapBytes.length, bitmapFactoryOptions ) );
      }

    }


  /*****************************************************
   *
   * A bitmap image source.
   *
   *****************************************************/
  private class BitmapSource extends ASource
    {
    private Bitmap  mSourceBitmap;


    BitmapSource( Bitmap bitmap )
      {
      mSourceBitmap = bitmap;
      }


    @Override
    Bitmap load()
      {
      // For bitmap sources, we simply return the original bitmap. We don't want to
      // risk throwing memory exceptions, so we don't do any modifications.

      return ( mSourceBitmap );
      }


    @Override
    Size getSize( Context context )
      {
      return ( new Size( mSourceBitmap.getWidth(), mSourceBitmap.getHeight() ) );
      }


    @Override
    Bitmap load( Context context, BitmapFactory.Options bitmapFactoryOptions )
      {
      // Pretend we decoded a bitmap
      bitmapFactoryOptions.outWidth  = mSourceBitmap.getWidth();
      bitmapFactoryOptions.outHeight = mSourceBitmap.getHeight();

      return ( mSourceBitmap );
      }

    }


  /*****************************************************
   *
   * A file image source.
   *
   *****************************************************/
  private class FileSource extends ASource
    {
    private File  mSourceFile;


    FileSource( File file )
      {
      mSourceFile = file;
      }


    @Override
    Size getSize( Context context )
      {
      BitmapFactory.Options bitmapFactoryOptions = getBoundsBitmapOptions();

      BitmapFactory.decodeFile( mSourceFile.getPath(), bitmapFactoryOptions );

      return ( getRotatedImageSize( context, bitmapFactoryOptions, Uri.fromFile( mSourceFile ) ) );
      }


    @Override
    Bitmap load( Context context, BitmapFactory.Options bitmapFactoryOptions )
      {
      Bitmap bitmap = BitmapFactory.decodeFile( mSourceFile.getPath(), bitmapFactoryOptions );

      if ( bitmap != null )
        {
        bitmap = rotate( bitmap, getRotationForImage( context, Uri.fromFile( mSourceFile ) ) );
        }

      return ( bitmap );
      }

    }


  /*****************************************************
   *
   * A URL image source.
   *
   *****************************************************/
  private class URLSource extends ASource implements FileDownloader.ICallback
    {
    private URL                 mSourceURL;
    private Map<String, String> mHeaderMap;

    private String              mImageCategory;


    URLSource( URL url, Map<String, String>  headerMap, String imageCategory )
      {
      mSourceURL     = url;
      mHeaderMap     = headerMap;
      mImageCategory = imageCategory;
      }


    ////////// ASource Method(s) //////////

    @Override
    Bitmap load()
      {
      // Check if the URL is mapped to a resource

      if ( ! FORCE_FILE_DOWNLOAD )
        {
        Integer imageSourceResourceIdAsInteger = ImageAgent.getInstance( mApplicationContext ).getMappedResource( mSourceURL );

        if ( imageSourceResourceIdAsInteger != null )
          {
          // Replace this source with a bitmap resource source

          BitmapResourceSource newSource = new BitmapResourceSource( imageSourceResourceIdAsInteger );

          mSource = newSource;

          return ( newSource.load() );
          }
        }


      // See if the image needs to be downloaded first

      // Generate the directory and file that the image would be downloaded to

      Pair<String, String> directoryAndFilePath = ImageAgent.getInstance( mApplicationContext ).getImageCacheDirectoryAndFilePath( mImageCategory, mSourceURL );

      String imageDirectoryPath = directoryAndFilePath.first;
      String imageFilePath      = directoryAndFilePath.second;


      // See if we already have the image in cache

      File imageDirectory = new File( imageDirectoryPath );
      File imageFile      = new File( imageFilePath );

      if ( ( ! FORCE_FILE_DOWNLOAD ) && imageFile.exists() )
        {
        // Replace this source with a file source

        FileSource newSource = new FileSource( imageFile );

        mSource = newSource;

        return ( newSource.load() );

        }
      else
        {
        // Notify any target that the image will need to be downloaded
        if ( mTarget != null ) mTarget.onImageDownloading();

        // Make a request to download the image, and use us as the callback.
        FileDownloader.getInstance( mApplicationContext ).requestFileDownload( mSourceURL, mHeaderMap, imageDirectory, imageFile, this );
        }


      return ( null );
      }


    @Override
    Size getSize( Context context )
      {
      // An image from a URL should have been downloaded first and then changed to a file load,
      // so throw an exception if we are called.

      throw ( new IllegalStateException( "Cannot decode an image from a URL - it should have been downloaded first" ) );
      }


    @Override
    Bitmap load( Context context, BitmapFactory.Options bitmapFactoryOptions )
      {
      // An image from a URL should have been downloaded first and then changed to a file load,
      // so throw an exception if we are called.

      throw ( new IllegalStateException( "Cannot decode an image from a URL - it should have been downloaded first" ) );
      }


    ////////// FileDownloader.ICallback Method(s) //////////

    @Override
    public void onDownloadSuccess( URL sourceURL, File targetDirectory, File targetFile )
      {
      // Replace this source with a file source

      FileSource newSource = new FileSource( targetFile );

      mSource = newSource;


      // Call the load for the new (file) source. File sources shouldn't
      // return a bitmap immediately, but check anyway just in case we
      // change something in the future (e.g. add caching).

      Bitmap bitmap = newSource.load();

      if ( bitmap != null && mTarget != null )
        {
        mTarget.onImageAvailable( bitmap );
        }
      }


    @Override
    public void onDownloadFailure( URL sourceURL, Exception exception )
      {
      if ( mTarget != null ) mTarget.onImageUnavailable( exception );
      }

    }


  /*****************************************************
   *
   * A URI image source.
   *
   *****************************************************/
  private class URISource extends ASource
    {
    private Uri mSourceURI;


    URISource( Uri uri )
      {
      mSourceURI = uri;
      }


    @Override
    Size getSize( Context context ) throws FileNotFoundException
      {
      BitmapFactory.Options bitmapFactoryOptions = getBoundsBitmapOptions();

      load ( context, bitmapFactoryOptions );

      return ( getRotatedImageSize( context, bitmapFactoryOptions, mSourceURI ) );
      }


    @Override
    Bitmap load( Context context, BitmapFactory.Options bitmapFactoryOptions ) throws FileNotFoundException
      {
      BufferedInputStream bis = new BufferedInputStream( context.getContentResolver().openInputStream( mSourceURI ) );

      Bitmap bitmap = BitmapFactory.decodeStream( bis, null, bitmapFactoryOptions );

      if ( bitmap != null )
        {
        bitmap = rotate( bitmap, getRotationForImage( context, mSourceURI ) );
        }

      return ( bitmap );
      }

    }


  /*****************************************************
   *
   * A bitmap resource image source.
   *
   *****************************************************/
  private class BitmapResourceSource extends ASource
    {
    private int  mSourceResourceId;


    BitmapResourceSource( int resourceId )
      {
      mSourceResourceId = resourceId;
      }


    @Override
    Size getSize( Context context )
      {
      BitmapFactory.Options bitmapFactoryOptions = getBoundsBitmapOptions();

      load( context, getBoundsBitmapOptions() );

      return ( new Size( bitmapFactoryOptions.outWidth, bitmapFactoryOptions.outHeight ) );
      }


    @Override
    Bitmap load( Context context, BitmapFactory.Options bitmapFactoryOptions )
      {
      return ( BitmapFactory.decodeResource( context.getResources(), mSourceResourceId, bitmapFactoryOptions ) );
      }

    }


  /*****************************************************
   *
   * An image target.
   *
   *****************************************************/
  private abstract class ATarget
    {
    abstract void onImageDownloading();
    abstract void onImageAvailable( Bitmap bitmap );
    abstract void onImageUnavailable( Exception exception );
    }


  /*****************************************************
   *
   * An image view target.
   *
   *****************************************************/
  private class ImageViewTarget extends ATarget
    {
    private ImageView  mImageView;
    private Object     mKeyTag;


    ImageViewTarget( ImageView imageView, Object keyTag )
      {
      mImageView = imageView;
      mKeyTag    = keyTag;
      }


    @Override
    void onImageDownloading()
      {
      // Ignore
      }

    @Override
    void onImageAvailable( Bitmap bitmap )
      {
      // Check if a key tag is required to match

      if ( mKeyTag == null || mKeyTag.equals( mImageView.getTag() ) )
        {
        if ( KiteSDK.DEBUG_IMAGE_LOADING )
          {
          Log.d( LOG_TAG, "Delivering bitmap : " + bitmap + ( bitmap != null ? " ( " + bitmap.getWidth() + " x " + bitmap.getHeight() + " )" : "" ) + " -> " + mImageView );
          }

        mImageView.setImageBitmap( bitmap );
        }
      }

    @Override
    void onImageUnavailable( Exception exception )
      {
      // Ignore
      }
    }


  /*****************************************************
   *
   * A menu item target.
   *
   *****************************************************/
  private class MenuItemTarget extends ATarget
    {
    private MenuItem  mMenuItem;


    MenuItemTarget( MenuItem menuItem )
      {
      mMenuItem = menuItem;
      }


    @Override
    void onImageDownloading()
      {
      // Ignore
      }

    @Override
    void onImageAvailable( Bitmap bitmap )
      {
      if ( KiteSDK.DEBUG_IMAGE_LOADING )
        {
        Log.d( LOG_TAG, "Delivering bitmap : " + bitmap + ( bitmap != null ? " ( " + bitmap.getWidth() + " x " + bitmap.getHeight() + " )" : "" ) + " -> " + mMenuItem );
        }

      mMenuItem.setIcon( new BitmapDrawable( bitmap ) );
      }

    @Override
    void onImageUnavailable( Exception exception )
      {
      // Ignore
      }
    }


  /*****************************************************
   *
   * An image consumer target.
   *
   *****************************************************/
  private class ImageConsumerTarget extends ATarget
    {
    private IImageConsumer  mImageConsumer;
    private Object          mKey;


    ImageConsumerTarget( IImageConsumer imageConsumer, Object key )
      {
      mImageConsumer = imageConsumer;
      mKey           = key;
      }


    @Override
    void onImageDownloading()
      {
      mImageConsumer.onImageDownloading( mKey );
      }

    @Override
    void onImageAvailable( Bitmap bitmap )
      {
      if ( KiteSDK.DEBUG_IMAGE_LOADING )
        {
        Log.d( LOG_TAG, "Delivering bitmap : " + bitmap + ( bitmap != null ? " ( " + bitmap.getWidth() + " x " + bitmap.getHeight() + " )" : "" ) + " -> " + mImageConsumer );
        }

      mImageConsumer.onImageAvailable( mKey, bitmap );
      }

    @Override
    void onImageUnavailable( Exception exception )
      {
      mImageConsumer.onImageUnavailable( mKey, exception );
      }
    }


  /*****************************************************
   *
   * A request executor.
   *
   *****************************************************/
  interface IExecutor
    {
    public void execute( ImageLoadRequest imageLoadRequest );
    }


  /*****************************************************
   *
   * A request builder.
   *
   *****************************************************/
  public class Builder
    {
    private IExecutor mExecutor;


    ////////// Constructor(s) //////////

    Builder( IExecutor executor )
      {
      mExecutor = executor;
      }


    Builder()
      {
      this( null );
      }


    ////////// Method(s) //////////

    /*****************************************************
     *
     * Sets the source of the image as bitmap data.
     *
     *****************************************************/
    public Builder load( byte[] bitmapBytes )
      {
      setSource( new BitmapBytesSource( bitmapBytes ) );

      return ( this );
      }


    /*****************************************************
     *
     * Sets the source of the image as a bitmap.
     *
     *****************************************************/
    public Builder load( Bitmap bitmap )
      {
      setSource( new BitmapSource( bitmap ) );

      return ( this );
      }


    /*****************************************************
     *
     * Sets the source of the image as a file.
     *
     *****************************************************/
    public Builder load( File file )
      {
      setSource( new FileSource( file ) );

      return ( this );
      }


    /*****************************************************
     *
     * Sets the source of the image as a URL.
     *
     *****************************************************/
    public Builder load( URL url, String imageCategory )
      {
      return load( url, null, imageCategory );
      }


    /*****************************************************
     *
     * Sets the source of the image as a URL.
     *
     *****************************************************/
    public Builder loadURL( String urlString, String imageCategory ) throws MalformedURLException
      {
      return loadURL( urlString , null, imageCategory );
      }


    /*****************************************************
     *
     * Sets the source of the image as a URL with headers
     *
     *****************************************************/
    public Builder load( URL url, Map<String, String>  headerMap, String imageCategory )
      {
      // If there is a mapping between the URL and a resource id that
      // we should use - change the source.

      Integer mappedBitmapResourceIdAsInteger;

      if ( ! FORCE_FILE_DOWNLOAD &&
           ( mappedBitmapResourceIdAsInteger = ImageAgent.getInstance( mApplicationContext ).getMappedResource( url ) ) != null )
        {
        setSource( new BitmapResourceSource( mappedBitmapResourceIdAsInteger ) );
        }
      else
        {
        setSource( new URLSource( url, headerMap, imageCategory ) );
        }


      return ( this );
      }


    /*****************************************************
     *
     * Sets the source of the image as a URL with headers
     *
     *****************************************************/
    public Builder loadURL( String urlString, Map<String, String>  headerMap, String imageCategory ) throws MalformedURLException
      {
      return ( load( new URL( urlString ), headerMap, imageCategory ) );
      }


    /*****************************************************
     *
     * Sets the source of the image as a URI.
     *
     *****************************************************/
    public Builder load( Uri uri )
      {
      // If there is a mapping between the URL and a resource id that
      // we should use - change the source.

      Integer mappedBitmapResourceIdAsInteger;

      if ( !FORCE_FILE_DOWNLOAD &&
              ( mappedBitmapResourceIdAsInteger = ImageAgent.getInstance( mApplicationContext ).getMappedResource( uri ) ) != null )
        {
        setSource( new BitmapResourceSource( mappedBitmapResourceIdAsInteger ) );
        }
      else
        {
        setSource( new URISource( uri ) );
        }


      return ( this );
      }


    /*****************************************************
     *
     * Sets the source of the image as a bitmap resource.
     *
     *****************************************************/
    public Builder load( int bitmapResourceId )
      {
      setSource( new BitmapResourceSource( bitmapResourceId ) );

      return ( this );
      }


    /*****************************************************
     *
     * Sets the source of the image as an asset.
     *
     *****************************************************/
    public Builder load( Asset asset )
      {
      // Get the Asset Helper to set the source for us
      AssetHelper.setSource( asset, this );

      return ( this );
      }


    /*****************************************************
     *
     * Sets the source of the image as an asset fragment.
     *
     *****************************************************/
    public Builder load( AssetFragment assetFragment )
      {
      // Get the Asset Helper to set the source for us
      AssetHelper.setSource( assetFragment.getAsset(), this );

      return ( cropBeforeResizeTo( assetFragment.getProportionalRectangle() ) );
      }


    /*****************************************************
     *
     * Specifies that only the bounds should be loaded.
     *
     *****************************************************/
    public Builder onlyLoadBounds()
      {
      mOnlyLoadBounds = true;

      return ( this );
      }


    /*****************************************************
     *
     * Sets the source of the image as an asset, and specifies
     * that the bounds should be loaded only.
     *
     *****************************************************/
    public Builder loadSizeOf( Asset asset )
      {
      // Get the Asset Helper to set the source for us
      AssetHelper.setSource( asset, this );

      return ( onlyLoadBounds() );
      }


    /*****************************************************
     *
     * Transforms an image before it is resized.
     *
     *****************************************************/
    public Builder transformBeforeResize( IImageTransformer transformer )
      {
      mPreResizeTransformer = transformer;

      return ( this );
      }


    /*****************************************************
     *
     * Crops an image before it is resized.
     *
     *****************************************************/
    public Builder cropBeforeResizeTo( RectF proportionalCropRectangle )
      {
      return ( transformBeforeResize( new CropImageTransformer( proportionalCropRectangle ) ) );
      }


    /*****************************************************
     *
     * Sets the resize size. The aspect ratio is maintained.
     *
     *****************************************************/
    public Builder resize( int width, int height )
      {
      mResizeWidth  = width;
      mResizeHeight = height;

      return ( this );
      }


    /*****************************************************
     *
     * Sets the resize width using resource dimensions.
     * The aspect ratio is maintained.
     *
     *****************************************************/
    public Builder resizeDimen( int widthResourceId, int heightResourceId )
      {
      Resources resources = mApplicationContext.getResources();

      return ( resize( resources.getDimensionPixelSize( widthResourceId ), resources.getDimensionPixelSize( heightResourceId ) ) );
      }


    /*****************************************************
     *
     * Sets the resize width from the view if it has a size.
     * Otherwise scales to the default width.
     *
     * The aspect ratio is maintained.
     *
     *****************************************************/
    public Builder resizeFor( View view, int defaultWidth, int defaultHeight )
      {
      // Try and get a width for the image view. If it's too small (usually because
      // it hasn't been set yet) - use the default values.

      int width  = view.getWidth();
      int height = view.getHeight();

      if ( width  < 1 ) width  = defaultWidth;
      if ( height < 1 ) height = defaultHeight;

      return ( resize( width, height ) );
      }


    /*****************************************************
     *
     * Sets the resize width from the view if it has a size.
     * Otherwise doesn't scale.
     *
     * The aspect ratio is maintained.
     *
     *****************************************************/
    public Builder resizeForIfSized( View view )
      {
      if ( view == null ) return ( this );


      // Try and get a width for the image view. If it's too small (usually because
      // it hasn't been set yet) - don't resize at all.

      int width  = view.getWidth();
      int height = view.getHeight();

      if ( width < 1 || height < 1 ) return ( this );


      return ( resize( width, height ) );
      }


    /*****************************************************
     *
     * Sets the resize width from the image view.
     * The aspect ratio is maintained.
     *
     *****************************************************/
    public Builder resizeForDimen( View view, int defaultWidthResourceId, int defaultHeightResourceId )
      {
      Resources resources = mApplicationContext.getResources();

      return ( resizeFor( view, resources.getDimensionPixelSize( defaultWidthResourceId ), resources.getDimensionPixelSize( defaultHeightResourceId ) ) );
      }


    /*****************************************************
     *
     * Qualifies any resize to only scale down.
     *
     *****************************************************/
    public Builder onlyScaleDown()
      {
      mOnlyScaleDown = true;

      return ( this );
      }


    /*****************************************************
     *
     * Reduces the colour space of the loaded bitmap to
     * save memory.
     *
     *****************************************************/
    public Builder reduceColourSpace()
      {
      mBitmapConfig = Bitmap.Config.RGB_565;

      return ( this );
      }


    /*****************************************************
     *
     * Transforms an image after it is resized.
     *
     *****************************************************/
    public Builder transformAfterResize( IImageTransformer transformer )
      {
      mPostResizeTransformer = transformer;

      return ( this );
      }


    /*****************************************************
     *
     * Sets priority
     *
     * @param isHighPriority has the following effects:
     *    TRUE - loads images almost instantaneously but
     *           is resource hungry , useful for editor reloading.
     *           Slows down scrolling views dramatically , leading to a
     *           laggy experience
     *
     *    FALSE (default) - best for loading multiple images especially
     *                     for scrolling views , images load slower but
     *                     it provides a fluent experience
     *
     *
     *****************************************************/
    public Builder setHighPriority( boolean isHighPriority )
      {
      mIsHighPriority = isHighPriority;

      return ( this );
      }


    /*****************************************************
     *
     * Sets the target of the image.
     *
     *****************************************************/
    public ImageLoadRequest into( IImageConsumer imageConsumer, Object key )
      {
      setTarget( new ImageConsumerTarget( imageConsumer, key ) );

      return ( create() );
      }


    /*****************************************************
     *
     * Sets the target of the image as an image view.
     *
     *****************************************************/
    public ImageLoadRequest into( ImageView imageView, Object keyTag )
      {
      // If a key tag is supplied, clear the image first, and use the
      // tag as a key: the image view will only accept an image if its
      // tag matches the key tag.

      if ( keyTag != null )
        {
        imageView.setTag( keyTag );
        imageView.setImageDrawable( null );
        }

      setTarget( new ImageViewTarget( imageView, keyTag ) );

      return ( create() );
      }


    /*****************************************************
     *
     * Sets the target of the image as an image view.
     *
     *****************************************************/
    public ImageLoadRequest into( ImageView imageView )
      {
      return ( into( imageView, null ) );
      }


    /*****************************************************
     *
     * Sets the target of the image.
     *
     *****************************************************/
    public ImageLoadRequest into( MenuItem menuItem )
      {
      setTarget( new MenuItemTarget( menuItem ) );

      return ( create() );
      }


    /*****************************************************
     *
     * Sets the image size consumer.
     *
     *****************************************************/
    public ImageLoadRequest into( IImageSizeConsumer imageSizeConsumer )
      {
      mImageSizeConsumer = imageSizeConsumer;

      return ( create() );
      }


    /*****************************************************
     *
     * Creates the request and optionally executes it.
     *
     *****************************************************/
    private ImageLoadRequest create()
      {
      if ( mExecutor != null ) mExecutor.execute( ImageLoadRequest.this );
      else                     execute();

      return ( ImageLoadRequest.this );
      }

    }


  /*****************************************************
   *
   * An image crop transformer.
   *
   *****************************************************/
  static public class CropImageTransformer implements IImageTransformer
    {
    private RectF  mProportionalCropRectangle;


    public CropImageTransformer( RectF proportionalCropRectangle )
      {
      mProportionalCropRectangle = proportionalCropRectangle;
      }


    @Override
    public Bitmap getTransformedBitmap( Bitmap originalBitmap )
      {
      return ( ImageAgent.crop( originalBitmap, mProportionalCropRectangle ) );
      }
    }
  }

