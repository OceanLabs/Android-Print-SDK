/*****************************************************
 *
 * ImageRequestProcessor.java
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

package ly.kite.image;


///// Import(s) /////

import java.util.LinkedList;

import android.content.Context;
import android.os.AsyncTask;


///// Class Declaration /////

/*****************************************************
 *
 * This class processes image requests.
 *
 *****************************************************/
public class ImageRequestProcessor
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG              = "ImageRequestProcessor";


  ////////// Static Variable(s) //////////

  static private ImageRequestProcessor sImageLoader;


  ////////// Member Variable(s) //////////

  private Context                   mApplicationContext;

  private LinkedList<ImageLoadRequest>  mRequestQueue;

  private LoaderTask                mLoaderTask;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns an instance of the image manager.
   *
   *****************************************************/
  static public ImageRequestProcessor getInstance( Context context )
    {
    if ( sImageLoader == null )
      {
      sImageLoader = new ImageRequestProcessor( context );
      }

    return ( sImageLoader );
    }


  ////////// Constructor(s) //////////

  private ImageRequestProcessor( Context context )
    {
    mApplicationContext = context.getApplicationContext();
    mRequestQueue       = new LinkedList<>();
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
   * Adds an image request to the process queue.
   *
   * Must be called on the UI thread.
   *
   *****************************************************/
  void process( ImageLoadRequest request )
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

    //For high priority don't queue the image response, directly fetch it
    if(request.getPriority())
      {
      mLoaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
      }
    else
      {
      mLoaderTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
      }

    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * The loader task.
   *
   *****************************************************/
  private class LoaderTask extends AsyncTask<Void,ImageLoadRequest,Void>
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
        ImageLoadRequest request = null;

        synchronized ( mRequestQueue )
          {
          request = mRequestQueue.poll();

          if ( request == null )
            {
            mLoaderTask = null;

            return ( null );
            }
          }


        // Process the request. If we get true back, do something with it on
        // the UI thread.

        if ( request.processInBackground() )
          {
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
    protected void onProgressUpdate( ImageLoadRequest... requests )
      {
      if ( requests != null )
        {
        for ( ImageLoadRequest request : requests )
          {
          request.onProcessingComplete();
          }
        }
      }

    }

  }

