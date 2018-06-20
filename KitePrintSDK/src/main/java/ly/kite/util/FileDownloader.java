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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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
  static private final String  LOG_TAG                  = "FileDownloader";

  static private final boolean DEBUGGING_ENABLED        = false;

  static private final int     BUFFER_SIZE_IN_BYTES     = 8192;  // 8 KB

  static private final int     MAX_CONCURRENT_DOWNLOADS = 5;


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
   * Downloads a file from a remote URL to a local file
   *
   * @return null, if the file was downloaded successfully
   * @return Exception, if an exception was thrown
   *
   *****************************************************/
  static public Exception download( URL sourceURL, Map<String, String> headerMap, File targetDirectory, File targetFile )
    {
    // Make sure the directory exists
    targetDirectory.mkdirs();


    // Retrieve the image

    InputStream      inputStream      = null;
    FileOutputStream fileOutputStream = null;

    // Use temp file as initial download target as elsewhere i.e. ImageAgent.requestImage we
    // assume file existence means completed download and this download likely occurs on a different
    // thread.

    File tempTargetFile = null;

    try
      {
      Log.i( LOG_TAG, "Downloading: " + sourceURL.toString() + " -> " + targetFile.getPath() );

      tempTargetFile = File.createTempFile( targetFile.getName(), ".tmp" );

      HttpURLConnection urlConnection = ( HttpURLConnection ) sourceURL.openConnection();

      if( headerMap != null )
        {
        for ( Map.Entry<String, String> entry : headerMap.entrySet() )
          {
          urlConnection.setRequestProperty( entry.getKey(), entry.getValue() );
          }
        }

      inputStream = urlConnection.getInputStream();

      fileOutputStream = new FileOutputStream( tempTargetFile );

      byte[] downloadBuffer = new byte[ BUFFER_SIZE_IN_BYTES ];

      int numberOfBytesRead;
      int totalBytesRead = 0;

      while ( ( numberOfBytesRead = inputStream.read( downloadBuffer ) ) >= 0 )
        {
        fileOutputStream.write( downloadBuffer, 0, numberOfBytesRead );

        totalBytesRead += numberOfBytesRead;
        }

      if ( DEBUGGING_ENABLED )
        {
        Log.d( LOG_TAG, "Downloaded " + totalBytesRead + " bytes" );
        Log.d( LOG_TAG, "Renaming " + tempTargetFile.getAbsolutePath() + " -> " + targetFile.getAbsolutePath() );
        }

      tempTargetFile.renameTo( targetFile );

      return ( null );
      }
    catch ( IOException ioe )
      {
      Log.e( LOG_TAG, "Unable to download to file", ioe );

      // Clean up any damaged files

      if ( tempTargetFile != null ) tempTargetFile.delete();

      targetFile.delete();


      return ( ioe );
      }
    finally
      {
      // Make sure the streams are closed before we finish

      if ( fileOutputStream != null )
        {
        try
          {
          fileOutputStream.close();
          }
        catch ( IOException ioe )
          {
          return ( ioe );
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
          return ( ioe );
          }
        }
      }
    }


  ////////// Constructor(s) //////////

  private FileDownloader( Context context )
    {
    mContext                 = context;
    mThreadPoolExecutor      = Executors.newFixedThreadPool( MAX_CONCURRENT_DOWNLOADS );
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
    for ( DownloaderTask downloaderTask : mInProgressDownloadTasks.values() )
      {
      downloaderTask.cancel( true );
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
  public void requestFileDownload( URL sourceURL, Map<String, String>  headerMap, File targetDirectory, File targetFile, boolean forceDownload, ICallback callback )
    {
    if ( mInProgressDownloadTasks.get( sourceURL ) == null )
      {
      // No in-progress task downloading this file, let's kick one off

      DownloaderTask downloaderTask = new DownloaderTask( sourceURL, headerMap, targetDirectory, targetFile, forceDownload, callback );

      mInProgressDownloadTasks.put( sourceURL, downloaderTask );

      downloaderTask.executeOnExecutor( mThreadPoolExecutor );
      }
    else
      {
      // A download is already in progress for this file so just add to the list of callbacks that
      // will be notified upon completion

      mInProgressDownloadTasks.get( sourceURL ).addCallback( callback );
      }
    }


  /*****************************************************
   *
   * Requests a file to be downloaded.
   *
   * Must be called on the UI thread.
   *
   *****************************************************/
  public void requestFileDownload( URL sourceURL, Map<String, String>  headerMap, File targetDirectory, File targetFile, ICallback callback )
    {
    requestFileDownload( sourceURL, headerMap, targetDirectory, targetFile, false, callback );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A downloaded image consumer.
   *
   *****************************************************/
  public interface ICallback
    {
    public void onDownloadSuccess( URL sourceURL, File targetDirectory, File targetFile );
    public void onDownloadFailure( URL sourceURL, Exception exception );
    }



  /*****************************************************
   *
   * The downloader task.
   *
   *****************************************************/
  private class DownloaderTask extends AsyncTask< Void, Void, Exception >
    {
    private final URL                  mSourceURL;
    private final Map<String, String>  mHeaderMap;
    private final File                 mTargetDirectory;
    private final File                 mTargetFile;
    private final boolean              mForceDownload;
    private final List<ICallback>      mCallbacks;


    public DownloaderTask( URL sourceURL, Map<String, String>  headerMap, File targetDirectory, File targetFile, boolean forceDownload, ICallback callback )
      {
      mSourceURL       = sourceURL;
      mHeaderMap       = headerMap;
      mTargetDirectory = targetDirectory;
      mTargetFile      = targetFile;
      mForceDownload   = forceDownload;

      mCallbacks = new ArrayList<>();
      mCallbacks.add( callback );
      }


    public void addCallback( ICallback callback )
      {
      mCallbacks.add( callback );
      }


    /*****************************************************
     *
     * Entry point for background thread.
     *
     *****************************************************/
    @Override
    protected Exception doInBackground( Void... params )
      {
      if (  mForceDownload || ( ! mTargetFile.exists() ) )
        {
        return ( download( mSourceURL, mHeaderMap, mTargetDirectory, mTargetFile ) );
        }

      return ( null );
      }


    @Override
    protected void onPostExecute( Exception resultException )
      {
      if ( resultException == null )
        {
        for ( ICallback callback : mCallbacks ) callback.onDownloadSuccess( mSourceURL, mTargetDirectory, mTargetFile );
        }
      else
        {
        for ( ICallback callback : mCallbacks ) callback.onDownloadFailure( mSourceURL, resultException );
        }

      mInProgressDownloadTasks.remove( mSourceURL );
      }
    }


  }

