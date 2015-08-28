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
  private static final String  LOG_TAG = "ImageLoader";


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
      mRequestQueue.add( request );

      // If the downloader task is already running - do nothing more
      if ( mLoaderTask != null ) return;
      }


    // Create and start a new downloader task

    mLoaderTask = new LoaderTask();

    mLoaderTask.execute();
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
          // Determine what type of request this is, and load the bitmap

          Bitmap bitmap = null;

          if ( request.sourceFile != null )
            {
            ///// File /////

            bitmap = BitmapFactory.decodeFile( request.sourceFile.getPath() );
            }
          else if ( request.sourceResourceId != 0 )
            {
            ///// Resource Id /////

            bitmap = BitmapFactory.decodeResource( mContext.getResources(), request.sourceResourceId );
            }
          else if ( request.sourceURI != null )
            {
            ///// URI /////

            BufferedInputStream bis = new BufferedInputStream( mContext.getContentResolver().openInputStream( request.sourceURI ) );

            bitmap = BitmapFactory.decodeStream( bis );
            }
          else if ( request.sourceBitmap != null )
            {
            ///// Bitmap /////

            bitmap = request.sourceBitmap;

            // There's no point in keeping a reference to the source bitmap if it gets transformed / scaled
            request.sourceBitmap = null;
            }
          else if ( request.sourceBytes != null )
            {
            ///// Bytes /////

            bitmap = BitmapFactory.decodeByteArray( request.sourceBytes, 0, request.sourceBytes.length );

            // There's no point in keeping a reference to the source bytes
            request.sourceBytes = null;
            }


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

