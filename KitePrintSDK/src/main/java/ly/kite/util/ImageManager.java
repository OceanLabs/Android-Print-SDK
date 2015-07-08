/*****************************************************
 *
 * ImageManager.java
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
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import ly.kite.print.Asset;

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
public class ImageManager
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "ImageManager";

  private static final int     DOWNLOAD_BUFFER_SIZE_IN_BYTES = 8192;  // 8 KB


  ////////// Static Variable(s) //////////

  private static ImageManager  sImageManager;


  ////////// Member Variable(s) //////////

  private Context                                         mContext;
  private File                                            mCacheDirectory;

  // In-memory image cache
  // TODO: Change this into a LRU-MRU chain with a memory size limit
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
  public static ImageManager getInstance( Context context )
    {
    if ( sImageManager == null )
      {
      sImageManager = new ImageManager( context );
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


  ////////// Constructor(s) //////////

  private ImageManager( Context context )
    {
    mContext         = context;
    mCacheDirectory  = context.getCacheDir();

    mImageTable      = new Hashtable<>();
    mInProgressTable = new HashMap<>();
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Requests an image. The image will be returned through
   * the RemoteImageConsumer interface either immediately,
   * if the image is already held in memory, or at a later
   * time once it has been downloaded / loaded into memory.
   *
   * This method should be called on the UI thread.
   *
   *****************************************************/
  public void getRemoteImage( String imageClassString, Object key, URL imageURL, Handler callbackHandler, IImageConsumer imageConsumer )
    {
    String imageURLString = imageURL.toString();


    // If we already have the image in memory, return it immediately

    Bitmap bitmap = mImageTable.get( imageURLString );

    if ( bitmap != null )
      {
      // We don't need to use the handler since we should have been called on the UI thread.
      imageConsumer.onImageImmediate( imageURL, bitmap );

      return;
      }


    // If the image is already being processed - add the consumer to the list of those wanting the
    // image. Otherwise create an in-progress entry for it.

    ArrayList<CallbackInfo> callbackInfoList;

    CallbackInfo callbackInfo = new CallbackInfo( callbackHandler, imageConsumer, key );

    synchronized ( mInProgressTable )
      {
      callbackInfoList = mInProgressTable.get( imageURLString );

      if ( callbackInfoList != null )
        {
        callbackInfoList.add( callbackInfo );

        return;
        }


      // Create a new in-progress entry

      callbackInfoList = new ArrayList<>();

      callbackInfoList.add( callbackInfo );

      mInProgressTable.put( imageURLString, callbackInfoList );
      }


    // Start a new processor in the background for this image. Note that we don't impose any thread limit
    // at this point. It may be that we have to introduce a size-limited pool of processors in the future
    // if this gets too silly.

    Processor processor = new Processor( imageClassString, key, imageURL, imageURLString, callbackInfoList );

    new Thread( processor ).start();
    }


  /*****************************************************
   *
   * Requests an image from a remote URL.
   *
   *****************************************************/
  public void getRemoteImage( String imageClassString, URL imageURL, Handler callbackHandler, IImageConsumer imageConsumer )
    {
    getRemoteImage( imageClassString, imageURL, imageURL, callbackHandler, imageConsumer );
    }


  /*****************************************************
   *
   * Requests an image from an asset.
   *
   *****************************************************/
  public void getImage( String imageClassString, Asset asset, Handler callbackHandler, IImageConsumer imageConsumer )
    {
    switch ( asset.getType() )
      {
      case IMAGE_URI:          break;

      case BITMAP_RESOURCE_ID:

        // For assets from resources - load and return the bitmap immediately
        // TODO: Load the bitmap on a different thread, and return it
        imageConsumer.onImageImmediate( asset, BitmapFactory.decodeResource( mContext.getResources(), asset.getBitmapResourceId() ) );
        return;

      case IMAGE_BYTES:        break;
      case IMAGE_PATH:         break;

      case REMOTE_URL:
        getRemoteImage( imageClassString, asset, asset.getRemoteURL(), callbackHandler, imageConsumer );
        return;
      }

    throw ( new UnsupportedOperationException( "Asset type not yet supported" ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A remote image consumer and its callback handler.
   *
   *****************************************************/
  private class CallbackInfo
    {
    Handler        callbackHandler;
    IImageConsumer remoteImageConsumer;
    Object         key;


    CallbackInfo( Handler callbackHandler, IImageConsumer remoteImageConsumer, Object key )
      {
      this.callbackHandler     = callbackHandler;
      this.remoteImageConsumer = remoteImageConsumer;
      this.key                 = key;
      }
    }


  /*****************************************************
   *
   * An image processor (downloader / loader).
   *
   *****************************************************/
  private class Processor implements Runnable
    {
    private String                   mImageClassString;
    private Object                   mKey;
    private URL                      mImageURL;
    private String                   mImageURLString;
    private ArrayList<CallbackInfo>  mCallbackInfoList;


    Processor( String imageClassString, Object key, URL imageURL, String imageURLString, ArrayList<CallbackInfo>  callbackInfoList )
      {
      mImageClassString = imageClassString;
      mKey              = key;
      mImageURL         = imageURL;
      mImageURLString   = imageURLString;
      mCallbackInfoList = callbackInfoList;
      }


    /*****************************************************
     *
     * The thread entry point for the processor.
     *
     *****************************************************/
    @Override
    public void run()
      {
      // Construct the directory and file paths. The file path is: "<cache-directory>/<image-class-string>/<image-url-string>"
      // The image class string and image URL string are first converted into 'safe' strings.
      String imageDirectoryPath = mCacheDirectory.getPath() + File.separator + toSafeString( mImageClassString );
      String imageFilePath      = imageDirectoryPath + File.separator + toSafeString( mImageURLString );


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
          if ( ! downloadTo( imageDirectory, imageFile ) ) return;
          }


        // Load the image from the file and store it in the memory cache

        bitmap = BitmapFactory.decodeFile( imageFilePath );

        mImageTable.put( mImageURLString, bitmap );
        }
      finally
        {
        // Remove the in-progress entry

        synchronized ( mInProgressTable )
          {
          mInProgressTable.remove( mImageURLString );
          }
        }


      // Notify all the consumers using their callback handlers

      for ( CallbackInfo callbackInfo : mCallbackInfoList )
        {
        callbackInfo.callbackHandler.post( new LoadedCallbackCaller( callbackInfo.remoteImageConsumer, mKey, bitmap ) );
        }
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
        callbackInfo.callbackHandler.post( new DownloadingCallbackCaller( callbackInfo.remoteImageConsumer, callbackInfo.key ) );
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
   * A callback caller for loaded images.
   *
   *****************************************************/
  private class LoadedCallbackCaller implements Runnable
    {
    private IImageConsumer mRemoteImageConsumer;
    private Object         mKey;
    private Bitmap         mBitmap;


    LoadedCallbackCaller( IImageConsumer remoteImageConsumer, Object key, Bitmap bitmap )
      {
      mRemoteImageConsumer = remoteImageConsumer;
      mKey                 = key;
      mBitmap              = bitmap;
      }


    @Override
    public void run()
      {
      mRemoteImageConsumer.onImageLoaded( mKey, mBitmap );
      }
    }

  }
