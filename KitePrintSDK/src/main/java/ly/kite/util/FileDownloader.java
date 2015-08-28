/*****************************************************
 *
 * FileDownloader.java
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
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.LinkedList;

/*****************************************************
 *
 * This class downloads images.
 *
 *****************************************************/
public class FileDownloader
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG              = "FileDownloader";

  private static final int     BUFFER_SIZE_IN_BYTES = 8192;  // 8 KB


  ////////// Static Variable(s) //////////

  private static FileDownloader sImageDownloader;


  ////////// Member Variable(s) //////////

  private Context              mContext;

  private LinkedList<Request>  mRequestQueue;

  private DownloaderTask       mDownloaderTask;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns an instance of the image manager.
   *
   *****************************************************/
  static public FileDownloader getInstance( Context context )
    {
    if ( sImageDownloader == null )
      {
      sImageDownloader = new FileDownloader( context );
      }

    return ( sImageDownloader );
    }


  /*****************************************************
   *
   * Either downloads a file from the remote URL, or loads
   * one from resources, and saves it to the output file.
   *
   * @return true, if the file was downloaded successfully.
   * @return false, otherwise.
   *
   *****************************************************/
  static public boolean download( URL sourceURL, File targetDirectory, File targetFile )
    {
    // Make sure the directory exists
    targetDirectory.mkdirs();


    // Retrieve the image

    InputStream inputStream  = null;
    OutputStream outputStream = null;

    try
      {
      Log.i( LOG_TAG, "Downloading: " + sourceURL.toString() + " -> " + targetFile.getPath() );

      inputStream = sourceURL.openStream();

      outputStream = new FileOutputStream( targetFile );

      byte[] downloadBuffer = new byte[ BUFFER_SIZE_IN_BYTES ];

      int numberOfBytesRead;

      while ( ( numberOfBytesRead = inputStream.read( downloadBuffer ) ) >= 0 )
        {
        outputStream.write( downloadBuffer, 0, numberOfBytesRead );
        }

      outputStream.close();
      inputStream.close();

      return ( true );
      }
    catch ( IOException ioe )
      {
      Log.e( LOG_TAG, "Unable to download to file", ioe );
      }
    finally
      {
      if ( outputStream != null )
        {
        try
          {
          outputStream.close();
          }
        catch ( IOException ioe )
          {
          // Ignore
          }
        }

      if ( inputStream != null )
        {
        try
          {
          inputStream.close();
          }
        catch ( IOException ioe )
          {
          // Ignore
          }
        }
      }

    return ( false );
    }


  ////////// Constructor(s) //////////

  private FileDownloader( Context context )
    {
    mContext      = context;
    mRequestQueue = new LinkedList<>();
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Requests a file to be downloaded.
   *
   * Must be called on the UI thread.
   *
   *****************************************************/
  private void requestFileDownload( Request request )
    {
    synchronized ( mRequestQueue )
      {
      // Add the request to the queue
      mRequestQueue.add( request );

      // If the downloader task is already running - do nothing more
      if ( mDownloaderTask != null ) return;
      }


    // Create and start a new downloader task

    mDownloaderTask = new DownloaderTask();

    mDownloaderTask.execute();
    }


  /*****************************************************
   *
   * Requests a file to be downloaded.
   *
   * Must be called on the UI thread.
   *
   *****************************************************/
  public void requestFileDownload( URL sourceURL, File targetDirectory, File targetFile, ICallback callback )
    {
    requestFileDownload( new Request( sourceURL, targetDirectory, targetFile, callback ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A downloaded image consumer.
   *
   *****************************************************/
  public interface ICallback
    {
    public void onFileDownloaded( URL sourceURL, File targetDirectory, File targetFile );
    }


  /*****************************************************
   *
   * An download request.
   *
   *****************************************************/
  private class Request
    {
    URL        sourceURL;
    File       targetDirectory;
    File       targetFile;
    ICallback  callback;


    private Request( URL sourceURL, File targetDirectory, File targetFile, ICallback callback  )
      {
      this.sourceURL       = sourceURL;
      this.targetDirectory = targetDirectory;
      this.targetFile      = targetFile;
      this.callback        = callback;
      }
    }


  /*****************************************************
   *
   * The downloader task.
   *
   *****************************************************/
  private class DownloaderTask extends AsyncTask<Void,Request,Void>
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
            mDownloaderTask = null;

            return ( null );
            }
          }


        // It's possible that the file has already been downloaded by a previous request, so check
        // whether it exists before we try to download it again.

        if ( request.targetFile.exists() ||
             download( request.sourceURL, request.targetDirectory, request.targetFile ) )
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
    protected void onProgressUpdate( Request... requests )
      {
      Request request = requests[ 0 ];

      // Notify the callback that the file was downloaded
      request.callback.onFileDownloaded( request.sourceURL, request.targetDirectory, request.targetFile );
      }

    }

  }

