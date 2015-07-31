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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;


///// Class Declaration /////

/*****************************************************
 *
 * This singleton class manages (downloads, saves, and caches)
 * images.
 *
 * Images originate from a network server, and are specified
 * using a URL. Then there are two levels of 'caching':
 *   - Images may be stored in the cache directory on the
 *   devices. This allows them to be cleared by clearing
 *   the cache in the app manager.
 *   - Images may also be stored in memory.
 *
 *****************************************************/
public class ImageLoader
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "ImageLoader";

  private static final int     DOWNLOAD_BUFFER_SIZE_IN_BYTES = 8192;  // 8 KB


  ////////// Static Variable(s) //////////

  private static ImageLoader sImageManager;


  ////////// Member Variable(s) //////////

  private Context                                         mContext;
  private File                                            mCacheDirectory;

  // In-memory image cache
  // TODO: Re-enable in-memory caching, and change this into a LRU-MRU chain
  // TODO: with a memory size limit.
  private Hashtable<String,Bitmap>                        mImageTable;

  // Images that are currently being processed
  private HashMap<String,ArrayList<CallbackInfo>>         mInProgressTable;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns an instance of the image manager.
   *
   *****************************************************/
  public static ImageLoader getInstance( Context context )
    {
    if ( sImageManager == null )
      {
      sImageManager = new ImageLoader( context );
      }

    return ( sImageManager );
    }


  /*****************************************************
   *
   * Converts the supplied string to a 'safe' string for
   * use in file / directory names.
   *
   *****************************************************/
  public static String toSafeString( String sourceString )
    {
    int length = sourceString.length();

    char[] targetCharArray = new char[ length ];

    for ( int index = 0; index < length; index ++ )
      {
      char sourceChar = sourceString.charAt( index );

      if ( ( sourceChar >= '0' && sourceChar <= '9' ) ||
           ( sourceChar >= 'A' && sourceChar <= 'Z' ) ||
           ( sourceChar >= 'a' && sourceChar <= 'z' ) )
        {
        // Digits 0-9 and letters A-Z / a-z stay the same
        targetCharArray[ index ] = sourceChar;
        }
      else
        {
        // Everything else gets converted to underscore
        targetCharArray[ index ] = '_';
        }
      }

    return ( new String( targetCharArray ) );
    }


//  /*****************************************************
//   *
//   * Delivers an image to a consumer, using the supplied
//   * handler.
//   *
//   *****************************************************/
//  public static void postImageToConsumer( Handler callbackHandler, IImageConsumer imageConsumer, Object key, Bitmap bitmap )
//    {
//    callbackHandler.post( new ImageAvailableCaller( imageConsumer, key, bitmap ) );
//    }


  ////////// Constructor(s) //////////

  private ImageLoader( Context context )
    {
    mContext         = context;
    mCacheDirectory  = context.getCacheDir();

    mImageTable      = new Hashtable<>();
    mInProgressTable = new HashMap<>();
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns an image directory path.
   *
   *****************************************************/
  public String getImageDirectoryPath( String imageClassString )
    {
    return ( mCacheDirectory.getPath() + File.separator + toSafeString( imageClassString ) );
    }


  /*****************************************************
   *
   * Returns an image directory path and file path.
   *
   *****************************************************/
  public Pair<String,String> getImageDirectoryAndFilePath( String imageClassString, String imageIdentifier )
    {
    // Construct the directory and file paths. The file path is: "<cache-directory>/<image-class-string>/<image-url-string>"
    // The image class string and image URL string are first converted into 'safe' strings.
    String imageDirectoryPath = getImageDirectoryPath( imageClassString );
    String imageFilePath      = imageDirectoryPath + File.separator + toSafeString( imageIdentifier );

    return ( new Pair<String,String>( imageDirectoryPath, imageFilePath ) );
    }


  /*****************************************************
   *
   * Requests an image. The image will be returned through
   * the RemoteImageConsumer interface either immediately,
   * if the image is already held in memory, or at a later
   * time once it has been downloaded / loaded into memory.
   *
   * Must be called on the UI thread.
   *
   *****************************************************/
  public void requestRemoteImage( String imageClassString, Object key, URL imageURL, IImageConsumer imageConsumer )
    {
    String imageURLString = imageURL.toString();


    // If we already have the image in memory, return it immediately

    Bitmap bitmap = mImageTable.get( imageURLString );

    if ( bitmap != null )
      {
      // We don't need to use the handler since we should have been called on the UI thread.
      imageConsumer.onImageAvailable( imageURL, bitmap );

      return;
      }


    // If the image is already being processed - add the consumer to the list of those wanting the
    // image.

    ArrayList<CallbackInfo> callbackInfoList;

    CallbackInfo callbackInfo = new CallbackInfo( imageConsumer, key );

    callbackInfoList = mInProgressTable.get( imageURLString );

    if ( callbackInfoList != null )
      {
      callbackInfoList.add( callbackInfo );

      return;
      }


    // The image isn't already being processed, so create a new in-progress entry for it.

    callbackInfoList = new ArrayList<>();

    callbackInfoList.add( callbackInfo );

    mInProgressTable.put( imageURLString, callbackInfoList );


    // Start a new processor in the background for this image. Note that we don't impose any thread limit
    // at this point. It may be that we have to introduce a size-limited pool of processors in the future
    // if this gets too silly.

    new LoadImageTask( imageClassString, key, imageURL, imageURLString, callbackInfoList ).execute();
    }


  /*****************************************************
   *
   * Requests an image from a remote URL.
   *
   *****************************************************/
  public void requestRemoteImage( String imageClassString, URL imageURL, Handler callbackHandler, IImageConsumer imageConsumer )
    {
    requestRemoteImage( imageClassString, imageURL, imageURL, imageConsumer );
    }


  /*****************************************************
   *
   * Requests an image from a remote URL. This must be
   * called on the UI thread.
   *
   *****************************************************/
  public void requestRemoteImage( String imageClassString, URL imageURL, IImageConsumer imageConsumer )
    {
    requestRemoteImage( imageClassString, imageURL, imageURL, imageConsumer );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A remote image consumer and its callback handler.
   *
   *****************************************************/
  private class CallbackInfo
    {
    IImageConsumer remoteImageConsumer;
    Object         key;


    CallbackInfo( IImageConsumer remoteImageConsumer, Object key )
      {
      this.remoteImageConsumer = remoteImageConsumer;
      this.key                 = key;
      }
    }


  /*****************************************************
   *
   * An image processor (downloader / loader).
   *
   *****************************************************/
  private class LoadImageTask extends AsyncTask<Void,CallbackInfo,Bitmap>
    {
    private String                   mImageClassString;
    private Object                   mKey;
    private URL                      mImageURL;
    private String                   mImageURLString;
    private ArrayList<CallbackInfo>  mCallbackInfoList;


    LoadImageTask( String imageClassString, Object key, URL imageURL, String imageURLString, ArrayList<CallbackInfo> callbackInfoList )
      {
      mImageClassString = imageClassString;
      mKey              = key;
      mImageURL         = imageURL;
      mImageURLString   = imageURLString;
      mCallbackInfoList = callbackInfoList;
      }


    /*****************************************************
     *
     * The entry point for the processor.
     *
     *****************************************************/
    @Override
    protected Bitmap doInBackground( Void... params )
      {
      Pair<String,String> directoryAndFilePath = getImageDirectoryAndFilePath( mImageClassString, mImageURLString );

      String imageDirectoryPath = directoryAndFilePath.first;
      String imageFilePath      = directoryAndFilePath.second;


      boolean imageWasFoundLocally = false;
      Bitmap  bitmap               = null;

      try
        {
        // If we don't have the image stored locally - download it now

        File imageDirectory = new File( imageDirectoryPath );
        File imageFile      = new File( imageFilePath );

        imageWasFoundLocally = imageFile.exists();

        if ( ! imageWasFoundLocally )
          {
          // If we fail to download the file, don't continue. The finally block, however,
          // ensures that we clean up the in-progress entry.
          if ( ! downloadTo( imageDirectory, imageFile ) ) return ( null );
          }


        // Load the image from the file and store it in the memory cache

        bitmap = BitmapFactory.decodeFile( imageFilePath );

        // TODO: Re-enable in-memory caching when we implement aging
        //mImageTable.put( mImageURLString, bitmap );
        }
      finally
        {
        }


      return ( bitmap );
      }


    /*****************************************************
     *
     * Downloads a file from the remote URL to the image file.
     *
     * @return true, if the file was downloaded successfully.
     * @return false, otherwise.
     *
     *****************************************************/
    private boolean downloadTo( File imageDirectory, File imageFile )
      {
      // Notify each of the consumers that the image is being downloaded
      for ( CallbackInfo callbackInfo : mCallbackInfoList )
        {
        publishProgress( callbackInfo );
        }


      // Make sure the directory exists
      imageDirectory.mkdirs();


      // Download the image

      InputStream  inputStream  = null;
      OutputStream outputStream = null;

      try
        {
        Log.i( LOG_TAG, "Downloading: " + mImageURLString + " -> " + imageFile.getPath() );

        inputStream  = mImageURL.openStream();
        outputStream = new FileOutputStream( imageFile );

        byte[] downloadBuffer = new byte[ DOWNLOAD_BUFFER_SIZE_IN_BYTES ];

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


    /*****************************************************
     *
     * Called on the UI thread when the image needs to be
     * downloaded.
     *
     *****************************************************/
    @Override
    protected void onProgressUpdate( CallbackInfo... callbackInfo )
      {
      callbackInfo[0].remoteImageConsumer.onImageDownloading( callbackInfo[0].key );
      }


    /*****************************************************
     *
     * Called on the UI thread when the image processing task
     * has completed.
     *
     *****************************************************/
    @Override
    protected void onPostExecute( Bitmap resultBitmap )
      {
      // Remove the in-progress entry
      mInProgressTable.remove( mImageURLString );

      if ( resultBitmap != null )
        {
        // Deliver the image to all the consumers
        for ( CallbackInfo callbackInfo : mCallbackInfoList )
          {
          callbackInfo.remoteImageConsumer.onImageAvailable( mKey, resultBitmap );
          }
        }
      }

    }


  /*****************************************************
   *
   * Notifies consumers that their image is being downloaded.
   *
   *****************************************************/
  private class DownloadingCallbackCaller implements Runnable
    {
    private IImageConsumer  mRemoteImageConsumer;
    private Object          mKey;


    DownloadingCallbackCaller( IImageConsumer remoteImageConsumer, Object key )
      {
      mRemoteImageConsumer = remoteImageConsumer;
      mKey                 = key;
      }


    @Override
    public void run()
      {
      mRemoteImageConsumer.onImageDownloading( mKey );
      }
    }


  /*****************************************************
   *
   * A callback caller for images.
   *
   *****************************************************/
  private static class ImageAvailableCaller implements Runnable
    {
    private IImageConsumer mRemoteImageConsumer;
    private Object         mKey;
    private Bitmap         mBitmap;


    ImageAvailableCaller( IImageConsumer remoteImageConsumer, Object key, Bitmap bitmap )
      {
      mRemoteImageConsumer = remoteImageConsumer;
      mKey                 = key;
      mBitmap              = bitmap;
      }


    @Override
    public void run()
      {
      mRemoteImageConsumer.onImageAvailable( mKey, mBitmap );
      }
    }

  }
