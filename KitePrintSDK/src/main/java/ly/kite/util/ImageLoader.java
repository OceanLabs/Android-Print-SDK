/*****************************************************
 *
 * ImageLoader.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2015 Kite Tech Ltd. https://www.kite.ly
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


///// Class Declaration /////

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/*****************************************************
 *
 * This class loads images from various sources.
 *
 *****************************************************/
public class ImageLoader
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG              = "ImageLoader";

  static private final boolean DEBUGGING_IS_ENABLED = false;

  static private final int     MAX_BITMAP_PIXELS    = 6000000;  // 6MP = 3000 x 2000


  ////////// Static Variable(s) //////////

  static private ImageLoader sImageLoader;


  ////////// Member Variable(s) //////////

  private Context              mContext;

  private LinkedList<Request>  mRequestQueue;

  private LoaderTask           mLoaderTask;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns an instance of the image manager.
   *
   *****************************************************/
  static public ImageLoader getInstance( Context context )
    {
    if ( sImageLoader == null )
      {
      sImageLoader = new ImageLoader( context );
      }

    return ( sImageLoader );
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
    bitmapFactoryOptions.inMutable                = false;
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
  static private BitmapFactory.Options getBoundsBitmapOptions( Bitmap.Config bitmapConfig )
    {
    BitmapFactory.Options bitmapFactoryOptions = getCommonBitmapOptions( bitmapConfig );

    bitmapFactoryOptions.inJustDecodeBounds       = true;
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
  static private int degreesFromEXIFOrientation( int exifOrientation )
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

  private ImageLoader( Context context )
    {
    mContext      = context;
    mRequestQueue = new LinkedList<>();
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Clears the request queue.
   *
   *****************************************************/
  public void clearPendingRequests()
    {
    synchronized ( mRequestQueue )
      {
      mRequestQueue.clear();
      }
    }


  /*****************************************************
   *
   * Requests an image to be loaded.
   *
   * Must be called on the UI thread.
   *
   *****************************************************/
  private void requestImageLoad( Request request )
    {
    synchronized ( mRequestQueue )
      {
      // Add the request to the queue
      mRequestQueue.addFirst( request );

      // If the downloader task is already running - do nothing more
      if ( mLoaderTask != null ) return;
      }


    // Create and start a new downloader task

    mLoaderTask = new LoaderTask();

    mLoaderTask.executeOnExecutor( AsyncTask.SERIAL_EXECUTOR );
    }


  /*****************************************************
   *
   * Requests an image to be loaded from a file.
   *
   * Must be called on the UI thread.
   *
   *****************************************************/
  public void requestImageLoad( Object key, File sourceFile, IImageTransformer imageTransformer, int scaledImageWidth, IImageConsumer imageConsumer )
    {
    requestImageLoad( new Request( key, sourceFile, imageTransformer, scaledImageWidth, imageConsumer ) );
    }


  /*****************************************************
   *
   * Requests an image to be loaded from a resource.
   *
   * Must be called on the UI thread.
   *
   *****************************************************/
  public void requestImageLoad( Object key, int sourceResourceId, IImageTransformer imageTransformer, int scaledImageWidth, IImageConsumer imageConsumer )
    {
    requestImageLoad( new Request( key, sourceResourceId, imageTransformer, scaledImageWidth, imageConsumer ) );
    }


  /*****************************************************
   *
   * Requests an image to be loaded from a URI.
   *
   * Must be called on the UI thread.
   *
   *****************************************************/
  public void requestImageLoad( Object key, Uri sourceURI, IImageTransformer imageTransformer, int scaledImageWidth, IImageConsumer imageConsumer )
    {
    requestImageLoad( new Request( key, sourceURI, imageTransformer, scaledImageWidth, imageConsumer ) );
    }


  /*****************************************************
   *
   * Requests an image to be loaded from an existing bitmap.
   *
   * Must be called on the UI thread.
   *
   *****************************************************/
  public void requestImageLoad( Object key, Bitmap sourceBitmap, IImageTransformer imageTransformer, int scaledImageWidth, IImageConsumer imageConsumer )
    {
    requestImageLoad( new Request( key, sourceBitmap, imageTransformer, scaledImageWidth, imageConsumer ) );
    }


  /*****************************************************
   *
   * Requests an image to be loaded from image data.
   *
   * Must be called on the UI thread.
   *
   *****************************************************/
  public void requestImageLoad( Object key, byte[] sourceBytes, IImageTransformer imageTransformer, int scaledImageWidth, IImageConsumer imageConsumer )
    {
    requestImageLoad( new Request( key, sourceBytes, imageTransformer, scaledImageWidth, imageConsumer ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * An image request.
   *
   *****************************************************/
  private class Request
    {
    Object             key;

    File               sourceFile;
    int                sourceResourceId;
    Uri                sourceURI;
    Bitmap             sourceBitmap;
    byte[]             sourceBytes;

    IImageTransformer  imageTransformer;
    int                scaledImageWidth;
    IImageConsumer     imageConsumer;

    Bitmap             bitmap;
    Exception          exception;


    private Request( Object key, IImageTransformer imageTransformer, int scaledImageWidth, IImageConsumer imageConsumer )
      {
      this.key              = key;
      this.imageTransformer = imageTransformer;
      this.scaledImageWidth = scaledImageWidth;
      this.imageConsumer    = imageConsumer;
      }

    Request( Object key, File sourceFile, IImageTransformer imageTransformer, int scaledImageWidth, IImageConsumer imageConsumer )
      {
      this( key, imageTransformer, scaledImageWidth, imageConsumer );

      this.sourceFile = sourceFile;
      }


    Request( Object key, int sourceResourceId, IImageTransformer imageTransformer, int scaledImageWidth, IImageConsumer imageConsumer )
      {
      this( key, imageTransformer, scaledImageWidth, imageConsumer );

      this.sourceResourceId = sourceResourceId;
      }


    Request( Object key, Uri sourceURI, IImageTransformer imageTransformer, int scaledImageWidth, IImageConsumer imageConsumer )
      {
      this( key, imageTransformer, scaledImageWidth, imageConsumer );

      this.sourceURI = sourceURI;
      }

    Request( Object key, Bitmap sourceBitmap, IImageTransformer imageTransformer, int scaledImageWidth, IImageConsumer imageConsumer )
      {
      this( key, imageTransformer, scaledImageWidth, imageConsumer );

      this.sourceBitmap = sourceBitmap;
      }

    Request( Object key, byte[] sourceBytes, IImageTransformer imageTransformer, int scaledImageWidth, IImageConsumer imageConsumer )
      {
      this( key, imageTransformer, scaledImageWidth, imageConsumer );

      this.sourceBytes = sourceBytes;
      }


    Bitmap loadBitmap( BitmapFactory.Options bitmapFactoryOptions ) throws Exception
      {
      if ( DEBUGGING_IS_ENABLED )
        {
        Log.d( LOG_TAG, "loadBitmap( bitmapFactoryOptions )" );
        Log.d( LOG_TAG, "  Sample size = " + bitmapFactoryOptions.inSampleSize );
        }

      Bitmap bitmap;

      int rotation = 0;

      if ( this.sourceFile != null )
        {
        ///// File /////

        if ( DEBUGGING_IS_ENABLED ) Log.d( LOG_TAG, "  Decoding file : " + this.sourceFile.getPath() );

        bitmap = BitmapFactory.decodeFile( this.sourceFile.getPath(), bitmapFactoryOptions );

        if ( bitmap != null )
          {
          rotation = getRotationForImage( mContext, Uri.fromFile( this.sourceFile ) );
          }
        }
      else if ( this.sourceResourceId != 0 )
        {
        ///// Resource Id /////

        if ( DEBUGGING_IS_ENABLED ) Log.d( LOG_TAG, "  Decoding resource id : " + this.sourceResourceId );

        bitmap = BitmapFactory.decodeResource( mContext.getResources(), this.sourceResourceId, bitmapFactoryOptions );
        }
      else if ( this.sourceURI != null )
        {
        ///// URI /////

        if ( DEBUGGING_IS_ENABLED ) Log.d( LOG_TAG, "  Decoding URI : " + this.sourceURI );

        BufferedInputStream bis = new BufferedInputStream( mContext.getContentResolver().openInputStream( this.sourceURI ) );

        bitmap = BitmapFactory.decodeStream( bis, null, bitmapFactoryOptions );

        if ( bitmap != null )
          {
          rotation = getRotationForImage( mContext, this.sourceURI );
          }
        }
      else if ( this.sourceBitmap != null )
        {
        ///// Bitmap /////

        bitmap = this.sourceBitmap;

        // Pretend we decoded a bitmap
        bitmapFactoryOptions.outWidth  = bitmap.getWidth();
        bitmapFactoryOptions.outHeight = bitmap.getHeight();

        // There's no point in keeping a reference to the source bitmap if it gets transformed / scaled
        this.sourceBitmap = null;
        }
      else if ( this.sourceBytes != null )
        {
        ///// Bytes /////

        bitmap = BitmapFactory.decodeByteArray( this.sourceBytes, 0, this.sourceBytes.length, bitmapFactoryOptions );

        // There's no point in keeping a reference to the source bytes
        this.sourceBytes = null;
        }
      else
        {
        throw ( new IllegalStateException( "No bitmap to decode" ) );
        }


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
    }


  /*****************************************************
   *
   * The loader task.
   *
   *****************************************************/
  private class LoaderTask extends AsyncTask<Void,Request,Void>
    {
    /*****************************************************
     *
     * Entry point for background thread.
     *
     *****************************************************/
    @Override
    protected Void doInBackground( Void... params )
      {
      // Keep going until we run out of requests

      while ( true )
        {
        Request request = null;

        synchronized ( mRequestQueue )
          {
          request = mRequestQueue.poll();

          if ( request == null )
            {
            mLoaderTask = null;

            return ( null );
            }
          }


        try
          {
          // In order to avoid running out of memory, we need to first check how big the bitmap
          // is. If it is larger that a size limit, we need to sub sample the decoded image to bring
          // it down.


          // Determine the bitmap size

          BitmapFactory.Options bitmapFactoryOptions = getBoundsBitmapOptions( Bitmap.Config.ARGB_8888 );

          request.loadBitmap( bitmapFactoryOptions );


          // Increase the sub-sampling (by powers of 2) until the number of bitmap
          // pixels is within the limit.

          int sampleSize   = 1;
          int width        = bitmapFactoryOptions.outWidth;
          int height       = bitmapFactoryOptions.outHeight;
          int bitmapPixels = width * height;

          while ( bitmapPixels > MAX_BITMAP_PIXELS )
            {
            sampleSize   <<= 1;   //  * 2
            width        >>>= 1;  //  / 2
            height       >>>= 1;  //  / 2

            bitmapPixels = width * height;
            }


          // Load the bitmap using the calculated sample size. If that fails, try dropping
          // the bitmap config to RGB_565 (i.e. from 4 bytes / pixel -> 2 bytes / pixel).

          Bitmap bitmap = null;

          try
            {
            bitmapFactoryOptions = getFullBitmapOptions( Bitmap.Config.ARGB_8888, sampleSize );
            bitmap               = request.loadBitmap( bitmapFactoryOptions );
            }
          catch ( OutOfMemoryError oome )
            {
            Log.e( LOG_TAG, "Unable to decode bitmap at ARGB_8888 - re-trying at RGB_565" );

            bitmapFactoryOptions = getFullBitmapOptions( Bitmap.Config.RGB_565, sampleSize );
            bitmap               = request.loadBitmap( bitmapFactoryOptions );
            }


          // Perform any transformation

          if ( request.imageTransformer != null )
            {
            bitmap = request.imageTransformer.getTransformedBitmap( bitmap );
            }


          // Perform any scaling

          if ( request.scaledImageWidth > 0 )
            {
            bitmap = ImageAgent.downscaleBitmap( bitmap, request.scaledImageWidth );
            }


          // Store the bitmap and publish the result

          request.bitmap = bitmap;

          publishProgress( request );
          }
        catch ( Exception exception )
          {
          Log.e( LOG_TAG, "Unable to load bitmap", exception );

          request.exception = exception;

          publishProgress( request );
          }

        }
      }


    /*****************************************************
     *
     * Called after each request is complete.
     *
     *****************************************************/
    @Override
    protected void onProgressUpdate( Request... requests )
      {
      Request request = requests[ 0 ];


      // Callback to the consumer with the result.

      if ( request.bitmap != null )
        {
        request.imageConsumer.onImageAvailable  ( request.key, request.bitmap );
        }
      else
        {
        request.imageConsumer.onImageUnavailable( request.key, request.exception );
        }
      }


    }

  }

