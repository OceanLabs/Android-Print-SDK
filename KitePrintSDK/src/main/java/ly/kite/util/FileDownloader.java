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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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

  private static final int MAX_CONCURRENT_DOWNLOADS = 5;


  ////////// Static Variable(s) //////////

  private static FileDownloader sImageDownloader;


  ////////// Member Variable(s) //////////

  private Context                     mContext;

  private Executor                    mThreadPoolExecutor;

  private Map<URL, DownloaderTask>    mInProgressDownloadTasks;


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
    FileOutputStream fileOutputStream = null;

    // use temp file as initial download target as elsewhere i.e. ImageAgent.requestImage we
    // assume file existence means completed download and this download likely occurs on a different
    // thread.
    File tempTargetFile = null;
    try
      {
      Log.i( LOG_TAG, "Downloading: " + sourceURL.toString() + " -> " + targetFile.getPath() );
      tempTargetFile = File.createTempFile( targetFile.getName(), ".tmp" );
      inputStream = sourceURL.openStream();

      fileOutputStream = new FileOutputStream( tempTargetFile );

      byte[] downloadBuffer = new byte[ BUFFER_SIZE_IN_BYTES ];

      int numberOfBytesRead;

      while ( ( numberOfBytesRead = inputStream.read( downloadBuffer ) ) >= 0 )
        {
        fileOutputStream.write( downloadBuffer, 0, numberOfBytesRead );
        }


      tempTargetFile.renameTo( targetFile );
      return ( true );
      }
    catch ( IOException ioe )
      {
      Log.e( LOG_TAG, "Unable to download to file", ioe );

      // Clean up any damaged files
      if ( tempTargetFile != null ) tempTargetFile.delete();
      targetFile.delete();

      return ( false );
      }
    finally
      {
      if ( fileOutputStream != null )
        {
        try
          {
          fileOutputStream.close();
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
    }


  ////////// Constructor(s) //////////

  private FileDownloader( Context context )
    {
    mContext      = context;
    mThreadPoolExecutor = Executors.newFixedThreadPool( MAX_CONCURRENT_DOWNLOADS );
    mInProgressDownloadTasks = new HashMap<>();
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Clears the request queue.
   *
   * Must be called on the UI thread.
   *
   *****************************************************/
  public void clearPendingRequests()
    {

    for ( DownloaderTask task : mInProgressDownloadTasks.values() )
      {
      task.cancel( true );
      }
    mInProgressDownloadTasks.clear();

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
        if ( mInProgressDownloadTasks.get( sourceURL ) == null )
          {
          // no in progress task downloading this file, lets kick one off
          DownloaderTask task = new DownloaderTask( sourceURL, targetDirectory, targetFile, callback );
          mInProgressDownloadTasks.put( sourceURL, task );
          task.executeOnExecutor( mThreadPoolExecutor );
          }
        else
          {
          // A download is already in progress for this file so just add to the list of callbacks that
          // will be notified upon completion
          mInProgressDownloadTasks.get( sourceURL ).addCallback( callback );
          }
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
   * The downloader task.
   *
   *****************************************************/
  private class DownloaderTask extends AsyncTask< Void, Void, Void >
    {

    private final URL mSourceURL;
    private final File mTargetDirectory;
    private final File mTargetFile;
    private final List<ICallback> mCallbacks;

    public DownloaderTask( URL sourceURL, File targetDirectory, File targetFile, ICallback callback )
      {
      mSourceURL = sourceURL;
      mTargetDirectory = targetDirectory;
      mTargetFile = targetFile;
      mCallbacks = new ArrayList<>();
      mCallbacks.add( callback );
      }

    public void addCallback( ICallback callback ) {
        mCallbacks.add( callback );
    }

    /*****************************************************
     *
     * Entry point for background thread.
     *
     *****************************************************/

    @Override
    protected Void doInBackground( Void... params )
      {
      if (  !mTargetFile.exists() )
        {
        download( mSourceURL, mTargetDirectory, mTargetFile );
        }

      return null;
      }

    @Override
    protected void onPostExecute(Void v)
      {
      for (ICallback callback : mCallbacks)
        {
        callback.onFileDownloaded( mSourceURL, mTargetDirectory, mTargetFile );
        }

        mInProgressDownloadTasks.remove( mSourceURL );
        }
    }


  }

