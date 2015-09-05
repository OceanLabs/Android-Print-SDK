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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
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
  private static final String  LOG_TAG           = "ImageLoader";

  private static final int     MAX_BITMAP_PIXELS = 6000000;  // 6MP = 3000 x 2000


  ////////// Static Variable(s) //////////

  private static ImageLoader sImageLoader;


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


    Bitmap decodeBitmap( BitmapFactory.Options bitmapFactoryOptions ) throws Exception
      {
      Bitmap bitmap;

      if ( this.sourceFile != null )
        {
        ///// File /////

        bitmap = BitmapFactory.decodeFile( this.sourceFile.getPath(), bitmapFactoryOptions );
        }
      else if ( this.sourceResourceId != 0 )
        {
        ///// Resource Id /////

        bitmap = BitmapFactory.decodeResource( mContext.getResources(), this.sourceResourceId, bitmapFactoryOptions );
        }
      else if ( this.sourceURI != null )
        {
        ///// URI /////

        BufferedInputStream bis = new BufferedInputStream( mContext.getContentResolver().openInputStream( this.sourceURI ) );

        bitmap = BitmapFactory.decodeStream( bis, null, bitmapFactoryOptions );
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

          BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options();

          bitmapFactoryOptions.inBitmap                 = null;
          bitmapFactoryOptions.inDensity                = 0;
          bitmapFactoryOptions.inDither                 = false;
          bitmapFactoryOptions.inJustDecodeBounds       = true;
          bitmapFactoryOptions.inMutable                = false;
          bitmapFactoryOptions.inPreferQualityOverSpeed = false;
          bitmapFactoryOptions.inPreferredConfig        = Bitmap.Config.ARGB_8888;
          bitmapFactoryOptions.inSampleSize             = 0;
          bitmapFactoryOptions.inScaled                 = false;
          bitmapFactoryOptions.inScreenDensity 	        = 0;
          bitmapFactoryOptions.inTargetDensity 	        = 0;
          bitmapFactoryOptions.inTempStorage 	          = null;
          bitmapFactoryOptions.mCancel                  = false;

          request.decodeBitmap( bitmapFactoryOptions );


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


          // Decode the bitmap using the calculated sample size

          bitmapFactoryOptions = new BitmapFactory.Options();

          bitmapFactoryOptions.inBitmap                 = null;
          bitmapFactoryOptions.inDensity                = 0;
          bitmapFactoryOptions.inDither                 = false;
          bitmapFactoryOptions.inJustDecodeBounds       = false;
          bitmapFactoryOptions.inMutable                = false;
          bitmapFactoryOptions.inPreferQualityOverSpeed = false;
          bitmapFactoryOptions.inPreferredConfig        = Bitmap.Config.ARGB_8888;
          bitmapFactoryOptions.inSampleSize             = sampleSize;
          bitmapFactoryOptions.inScaled                 = false;
          bitmapFactoryOptions.inScreenDensity 	        = 0;
          bitmapFactoryOptions.inTargetDensity 	        = 0;
          bitmapFactoryOptions.inTempStorage 	          = null;
          bitmapFactoryOptions.mCancel                  = false;

          Bitmap bitmap = request.decodeBitmap( bitmapFactoryOptions );


          // Perform any transformation

          if ( request.imageTransformer != null )
            {
            bitmap = request.imageTransformer.getTransformedBitmap( bitmap );
            }


          // Perform any scaling

          if ( request.scaledImageWidth > 0 )
            {
            bitmap = ImageDownscaler.scaleBitmap( bitmap, request.scaledImageWidth );
            }


          // Store the bitmap and publish the result

          request.bitmap = bitmap;

          publishProgress( request );
          }
        catch ( Exception exception )
          {
          Log.e( LOG_TAG, "Unable to load bitmap", exception );
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

      // Callback to the consumer with the bitmap
      request.imageConsumer.onImageAvailable( request.key, request.bitmap );
      }


    }

  }

