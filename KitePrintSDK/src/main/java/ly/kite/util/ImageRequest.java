/*****************************************************
 *
 * ImageRequest.java
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

package ly.kite.util;


///// Import(s) /////

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;


///// Class Declaration /////

/*****************************************************
 *
 * This class is a request for an image to be loaded.
 *
 *****************************************************/
public class ImageRequest
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG              = "ImageRequest";

  static private final boolean DEBUGGING_IS_ENABLED = false;

  static private final int     MAX_SUB_SAMPLE_SIZE  = Integer.MAX_VALUE;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Context            mApplicationContext;
  private boolean            mForceFileDownload;

  private ASource            mSource;
  private ATarget            mTarget;

  private IImageTransformer  mPreResizeTransformer;

  private int                mResizeWidth;

  private boolean            mOnlyScaleDown;
  private Bitmap.Config      mBitmapConfig;

  private Bitmap             mBitmap;
  private Exception          mException;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns a scale size to bring a bitmap width down to
   * a resize width.
   *
   *****************************************************/
  static int sampleSizeForResize( int originalWidth, int resizeWidth )
    {
    int sampleSize = 1;

    if ( resizeWidth >= 0 )
      {
      int width     = originalWidth;
      int nextWidth = width >>> 1;  // / 2

      while ( nextWidth > 0 && nextWidth >= resizeWidth )
        {
        width        = nextWidth;
        sampleSize <<= 1;            //  * 2
        nextWidth    = width >>> 1;  //  / 2
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
    if ( DEBUGGING_IS_ENABLED )
      {
      Log.d( LOG_TAG, "getRotationForImage( context, uri = " + ( uri != null ? uri.toString() : "null" ) + " )" );
      }

    Cursor cursor = null;

    try
      {
      if ( DEBUGGING_IS_ENABLED ) Log.d( LOG_TAG, "  URI scheme = " + uri.getScheme() );

      if ( uri.getScheme().equals( "content" ) )
        {
        ///// Content /////

        String[] projection = { MediaStore.Images.ImageColumns.ORIENTATION };

        cursor = context.getContentResolver().query( uri, projection, null, null, null );

        if ( cursor.moveToFirst() )
          {
          int rotation = cursor.getInt( 0 );

          if ( DEBUGGING_IS_ENABLED ) Log.d( LOG_TAG, "  Rotation = " + rotation );

          return ( rotation );
          }
        }
      else if ( uri.getScheme().equals( "file" ) )
        {
        ///// File /////

        if ( DEBUGGING_IS_ENABLED ) Log.d( LOG_TAG, "  URI path = " + uri.getPath() );

        ExifInterface exif = new ExifInterface( uri.getPath() );

        int rotation = degreesFromEXIFOrientation( exif.getAttributeInt( ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL ) );

        if ( DEBUGGING_IS_ENABLED ) Log.d( LOG_TAG, "  Rotation = " + rotation );

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
   * Converts an EXIF orientation into degrees..
   *
   *****************************************************/
  static int degreesFromEXIFOrientation( int exifOrientation )
    {
    if ( DEBUGGING_IS_ENABLED ) Log.d( LOG_TAG, "degreesFromEXIFOrientation( exifOrientation = " + exifOrientation + " )" );

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

  ImageRequest( Context context, boolean forceFileDownload )
    {
    mApplicationContext = context.getApplicationContext();
    mForceFileDownload  = forceFileDownload;

    mBitmapConfig       = Bitmap.Config.ARGB_8888;
    }


  ImageRequest( Context context )
    {
    this( context, false );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns true if files should always be downloaded, false
   * otherwise.
   *
   *****************************************************/
  boolean forceFileDownload()
    {
    return ( mForceFileDownload );
    }


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
  void execute()
    {
    // Ensure that both source and target have been set

    if ( mSource == null ) throw ( new IllegalStateException( "No image source has been specified" ) );

    if ( mTarget == null ) throw ( new IllegalStateException( "No image target has been specified" ) );


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

      BitmapFactory.Options bitmapFactoryOptions = getBoundsBitmapOptions();

      int originalWidth  = bitmapFactoryOptions.outWidth;
      int originalHeight = bitmapFactoryOptions.outHeight;


      // If resizing has been requested, sub-sample the bitmap to just larger
      // than the resize dimensions.

      int sampleSize = sampleSizeForResize( originalWidth, mResizeWidth );


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
        // Fall through
        }


      // We ran out of memory. If we were using a larger colour space, try again
      // using a lower one.

      if ( mBitmapConfig != Bitmap.Config.RGB_565 )
        {
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
          // Fall through
          }
        }


      // We ran out of memory again. Try dropping the image size until we
      // succeed.

      while ( sampleSize >= 1 && sampleSize < MAX_SUB_SAMPLE_SIZE )
        {
        sampleSize <<= 1;  // * 2

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
          // Fall through
          }
        }

      }
    catch ( Exception exception )
      {
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
    // Load the image, sub-sampling if specified

    BitmapFactory.Options bitmapFactoryOptions = getFullBitmapOptions( bitmapConfig, sampleSize );

    Bitmap bitmap = mSource.load( mApplicationContext, bitmapFactoryOptions );


    // Do any pre-resize transformation

    if ( mPreResizeTransformer != null )
      {
      bitmap = mPreResizeTransformer.getTransformedBitmap( bitmap );
      }


    // Do any scaling

    if ( mResizeWidth > 0 )
      {
      int bitmapWidth = bitmap.getWidth();

      if ( mResizeWidth < bitmapWidth || ( mResizeWidth > bitmapWidth && ! mOnlyScaleDown ) )
        {
        bitmap = ImageAgent.scaleBitmap( bitmap, mResizeWidth );
        }
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
    if      ( mBitmap    != null ) mTarget.onImageAvailable( mBitmap );
    else if ( mException != null ) mTarget.onImageUnavailable( mException );
    }


  ////////// Inner Class(es) //////////

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
      ImageRequestProcessor.getInstance( mApplicationContext ).process( ImageRequest.this );

      return ( null );
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
        // Perform the rotation by using a matrix to transform the bitmap

        Matrix matrix = new Matrix();

        matrix.preRotate( rotation );

        bitmap = Bitmap.createBitmap( bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true );
        }

      return ( bitmap );
      }


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
    private URL     mSourceURL;
    private String  mImageCategory;


    URLSource( URL url, String imageCategory )
      {
      mSourceURL     = url;
      mImageCategory = imageCategory;
      }


    ////////// ASource Method(s) //////////

    @Override
    Bitmap load()
      {
      // Check if the URL is mapped to a resource

      if ( ! mForceFileDownload )
        {
        Integer imageSourceResourceIdAsInteger = ImageAgent.getInstance( mApplicationContext ).getMappedResource( mSourceURL );

        if ( imageSourceResourceIdAsInteger != null )
          {
          // Replace this source with a bitmap resource source

          BitmapResourceSource newSource = new BitmapResourceSource( imageSourceResourceIdAsInteger );

          ImageRequest.this.mSource = newSource;

          return ( newSource.load() );
          }
        }


      // See if the image needs to be downloaded first

      // Generate the directory and file that the image would be downloaded to

      Pair<String, String> directoryAndFilePath = ImageAgent.getInstance( mApplicationContext ).getImageDirectoryAndFilePath( mImageCategory, mSourceURL.toString() );

      String imageDirectoryPath = directoryAndFilePath.first;
      String imageFilePath      = directoryAndFilePath.second;


      // See if we already have the image in cache

      File imageDirectory = new File( imageDirectoryPath );
      File imageFile      = new File( imageFilePath );

      if ( ( ! mForceFileDownload ) && imageFile.exists() )
        {
        // Replace this source with a file source

        FileSource newSource = new FileSource( imageFile );

        ImageRequest.this.mSource = newSource;

        return ( newSource.load() );
        }
      else
        {
        // Notify the target that the image will need to be downloaded
        ImageRequest.this.mTarget.onImageDownloading();

        // Make a request to download the image, and use us as the callback.
        FileDownloader.getInstance( mApplicationContext ).requestFileDownload( mSourceURL, imageDirectory, imageFile, this );
        }


      return ( null );
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

      ImageRequest.this.mSource = newSource;


      // Call the load for the new (file) source. File sources shouldn't
      // return a bitmap immediately, but check anyway just in case we
      // change something in the future (e.g. add caching).

      Bitmap bitmap = newSource.load();

      if ( bitmap != null )
        {
        ImageRequest.this.mTarget.onImageAvailable( bitmap );
        }
      }


    @Override
    public void onDownloadFailure( URL sourceURL, Exception exception )
      {
      ImageRequest.this.mTarget.onImageUnavailable( exception );
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


    ImageViewTarget( ImageView imageView )
      {
      mImageView = imageView;
      }


    @Override
    void onImageDownloading()
      {
      // Ignore
      }

    @Override
    void onImageAvailable( Bitmap bitmap )
      {
      mImageView.setImageBitmap( bitmap );
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
   * A request builder.
   *
   *****************************************************/
  static public class Builder
    {
    private Context            mContext;
    private ImageRequest       mImageRequest;


    ////////// Constructor(s) //////////

    Builder( Context context, boolean forceFileDownload )
      {
      mContext      = context;
      mImageRequest = new ImageRequest( context, forceFileDownload );
      }


    Builder( Context context )
      {
      this( context, false );
      }


    ////////// Method(s) //////////

    /*****************************************************
     *
     * Sets the source of the image as bitmap data.
     *
     *****************************************************/
    public Builder load( byte[] bitmapBytes )
      {
      mImageRequest.setSource( mImageRequest.new BitmapBytesSource( bitmapBytes ) );

      return ( this );
      }


    /*****************************************************
     *
     * Sets the source of the image as a bitmap.
     *
     *****************************************************/
    public Builder load( Bitmap bitmap )
      {
      mImageRequest.setSource( mImageRequest.new BitmapSource( bitmap ) );

      return ( this );
      }


    /*****************************************************
     *
     * Sets the source of the image as a file.
     *
     *****************************************************/
    public Builder load( File file )
      {
      mImageRequest.setSource( mImageRequest.new FileSource( file ) );

      return ( this );
      }


    /*****************************************************
     *
     * Sets the source of the image as a URL.
     *
     *****************************************************/
    public Builder load( URL url, String imageCategory )
      {
      // If there is a mapping between the URL and a resource id that
      // we should use - change the source.

      Integer mappedBitmapResourceIdAsInteger;

      if ( ! mImageRequest.forceFileDownload() &&
           ( mappedBitmapResourceIdAsInteger = ImageAgent.getInstance( mContext ).getMappedResource( url ) ) != null )
        {
        mImageRequest.setSource( mImageRequest.new BitmapResourceSource( mappedBitmapResourceIdAsInteger ) );
        }
      else
        {
        mImageRequest.setSource( mImageRequest.new URLSource( url, imageCategory ) );
        }


      return ( this );
      }


    /*****************************************************
     *
     * Sets the source of the image as a URL.
     *
     *****************************************************/
    public Builder loadURL( String urlString, String imageCategory ) throws MalformedURLException
      {
      return ( load( new URL( urlString ), imageCategory ) );
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

      if ( ! mImageRequest.forceFileDownload() &&
           ( mappedBitmapResourceIdAsInteger = ImageAgent.getInstance( mContext ).getMappedResource( uri ) ) != null )
        {
        mImageRequest.setSource( mImageRequest.new BitmapResourceSource( mappedBitmapResourceIdAsInteger ) );
        }
      else
        {
        mImageRequest.setSource( mImageRequest.new URISource( uri ) );
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
      mImageRequest.setSource( mImageRequest.new BitmapResourceSource( bitmapResourceId ) );

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
     * Transforms an image before it is resized.
     *
     *****************************************************/
    public Builder transformBeforeResize( IImageTransformer transformer )
      {
      mImageRequest.mPreResizeTransformer = transformer;

      return ( this );
      }


    /*****************************************************
     *
     * Sets the resize width. The aspect ratio is maintained.
     *
     *****************************************************/
    public Builder resize( int width )
      {
      mImageRequest.mResizeWidth = width;

      return ( this );
      }


    /*****************************************************
     *
     * Sets the resize width using resource dimensions.
     * The aspect ratio is maintained.
     *
     *****************************************************/
    public Builder resizeDimen( int widthResourceId )
      {
      Resources resources = mContext.getResources();

      return ( resize( resources.getDimensionPixelSize( widthResourceId ) ) );
      }


    /*****************************************************
     *
     * Sets the resize width from the image view.
     * The aspect ratio is maintained.
     *
     *****************************************************/
    public Builder resizeFor( View view, int defaultWidth )
      {
      // Try and get a width for the image view. If it's too small (usually because
      // it hasn't been set yet) - use the default values.

      int width = view.getWidth();

      if ( width < 1 ) width = defaultWidth;

      return ( resize( width ) );
      }


    /*****************************************************
     *
     * Sets the resize width from the image view.
     * The aspect ratio is maintained.
     *
     *****************************************************/
    public Builder resizeForDimen( View view, int defaultWidthResourceId )
      {
      Resources resources = mContext.getResources();

      return ( resizeFor( view, resources.getDimensionPixelSize( defaultWidthResourceId ) ) );
      }


    /*****************************************************
     *
     * Qualifies any resize to only scale down.
     *
     *****************************************************/
    public Builder onlyScaleDown()
      {
      mImageRequest.mOnlyScaleDown = true;

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
      mImageRequest.mBitmapConfig = Bitmap.Config.RGB_565;

      return ( this );
      }


    /*****************************************************
     *
     * Sets the target of the image.
     *
     *****************************************************/
    public void into( IImageConsumer imageConsumer, Object key )
      {
      mImageRequest.setTarget( mImageRequest.new ImageConsumerTarget( imageConsumer, key ) );

      createAndExecute();
      }


    /*****************************************************
     *
     * Sets the target of the image.
     *
     *****************************************************/
    public void into( ImageView imageView )
      {
      mImageRequest.setTarget( mImageRequest.new ImageViewTarget( imageView ) );

      createAndExecute();
      }


    /*****************************************************
     *
     * Creates the request and initiates it.
     *
     *****************************************************/
    private void createAndExecute()
      {
      mImageRequest.execute();
      }

    }

  }

