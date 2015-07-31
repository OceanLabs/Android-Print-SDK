/*****************************************************
 *
 * AssetHelper.java
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

package ly.kite.product;


///// Import(s) /////

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import ly.kite.util.IImageConsumer;
import ly.kite.util.ImageLoader;
import ly.kite.product.Asset.Type;
import ly.kite.product.Asset.MIMEType;


///// Class Declaration /////

/*****************************************************
 *
 * This class provides various helper methods for the
 * creation and loading of assets.
 *
 *****************************************************/
public class AssetHelper
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                   = "AssetHelper";

  public  static final String  IMAGE_CLASS_STRING_ASSET  = "asset";

  private static final int     READ_BUFFER_SIZE_IN_BYTES = 8192;  // 8 KB


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Clears any cached asset image files.
   *
   *****************************************************/
  public static void clearCachedImages( Context context )
    {
    // Get the image cache directory

    String imageCacheDirectoryPath = ImageLoader.getInstance( context ).getImageDirectoryPath( IMAGE_CLASS_STRING_ASSET );

    File imageCacheDirectory = new File( imageCacheDirectoryPath );


    // Go through all the files and delete them

    File[] imageFiles = imageCacheDirectory.listFiles();

    if ( imageFiles != null )
      {
      for ( File imageFile : imageCacheDirectory.listFiles() )
        {
        imageFile.delete();
        }
      }
    }


  /*****************************************************
   *
   * Creates a new asset from a bitmap, but writes it out
   * to a file. The file path is automatically generated.
   *
   *****************************************************/
  public static Asset createAsCachedFile( Context context, byte[] imageBytes, Asset.MIMEType mimeType )
    {
    // Generate a random file name within the cache
    Pair<String,String> imageDirectoryAndFilePath = ImageLoader.getInstance( context ).getImageDirectoryAndFilePath( IMAGE_CLASS_STRING_ASSET, UUID.randomUUID().toString() );

    File imageDirectory = new File( imageDirectoryAndFilePath.first );

    imageDirectory.mkdirs();

    return ( createAsCachedFile( imageBytes, imageDirectoryAndFilePath.second + mimeType.primaryFileSuffix() ) );
    }


  /*****************************************************
   *
   * Creates a new asset from a bitmap, but writes it out
   * to a file. The file path is automatically generated.
   *
   *****************************************************/
  public static Asset createAsCachedFile( Context context, Bitmap bitmap )
    {
    // Generate a random file name within the cache
    Pair<String,String> imageDirectoryAndFilePath = ImageLoader.getInstance( context ).getImageDirectoryAndFilePath( IMAGE_CLASS_STRING_ASSET, UUID.randomUUID().toString() );


    // Ensure that the directory exists

    File imageDirectory = new File( imageDirectoryAndFilePath.first );

    imageDirectory.mkdirs();


    ByteArrayOutputStream baos = new ByteArrayOutputStream();;

    try
      {
      bitmap.compress( Bitmap.CompressFormat.JPEG, Asset.BITMAP_TO_JPEG_QUALITY, baos );

      return ( createAsCachedFile( baos.toByteArray(), imageDirectoryAndFilePath.second + MIMEType.JPEG.primaryFileSuffix() ) );
      }
    catch ( Exception e )
      {
      Log.e( LOG_TAG, "Unable to encode bitmap to JPEG", e );

      return ( null );
      }
    finally
      {
      try
        {
        baos.close();
        }
      catch ( IOException ioe )
        {
        // Do nothing
        }
      }
    }


  /*****************************************************
   *
   * Returns a parcelable version of the supplied asset. If
   * the asset is already parcelable, then the same one is
   * returned. Otherwise a new file-backed asset is created
   * from the old one.
   *
   *****************************************************/
  public static Asset parcelableAsset( Context context, Asset originalAsset )
    {
    Asset.Type type = originalAsset.getType();

    if ( type.isParcelable() ) return ( originalAsset );


    switch ( type )
      {
      case BITMAP:

        return ( createAsCachedFile( context, originalAsset.getBitmap() ) );

      case IMAGE_BYTES:

        return ( createAsCachedFile( context, originalAsset.getImageBytes(), originalAsset.getMIMEType() ) );
      }


    throw ( new IllegalStateException( "Unable to create parcelable asset from type: " + type ) );
    }


  /*****************************************************
   *
   * Creates a new asset from a bitmap, but writes it out
   * to a file.
   *
   *****************************************************/
  public static Asset createAsCachedFile( byte[] imageBytes, String filePath )
    {
    // Write the bitmap to the file

    FileOutputStream fos = null;

    try
      {
      fos = new FileOutputStream( filePath );

      fos.write( imageBytes );

      return ( new Asset( filePath ) );
      }
    catch ( Exception exception )
      {
      Log.e( LOG_TAG, "Unable to write bitmap to file" );
      }
    finally
      {
      if ( fos != null )
        {
        try
          {
          fos.close();
          }
        catch ( IOException e )
          {
          e.printStackTrace();
          }
        }
      }

    return ( null );
    }


  /*****************************************************
   *
   * Returns the MIME type for the asset.
   *
   *****************************************************/
  public static MIMEType getMimeType( Context context, Asset asset )
    {
    Type type = asset.getType();

    switch ( type )
      {
      case IMAGE_URI:

        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap     = MimeTypeMap.getSingleton();

        return ( MIMEType.fromString( contentResolver.getType( asset.getImageURI() ) ) );


      case BITMAP_RESOURCE_ID:

        // Decode the bounds of the bitmap resource to get the MIME type

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource( context.getResources(), asset.getBitmapResourceId(), options );

        if ( options.outMimeType != null )
          {
          return ( MIMEType.fromString( options.outMimeType ) );
          }

        return ( MIMEType.JPEG );


      case BITMAP:

        // We return the MIME type that we will supply when the image bytes are requested - JPEG.
        // This makes sense since the images are probably photos.

        return ( MIMEType.JPEG );


      case IMAGE_BYTES:
        return ( asset.getMIMEType() );


      case IMAGE_FILE:

        String imagePath = asset.getImageFilePath().toLowerCase();

        if ( imagePath.endsWith( Asset.JPEG_FILE_SUFFIX_PRIMARY ) || imagePath.endsWith( Asset.JPEG_FILE_SUFFIX_SECONDARY ) )
          {
          return ( MIMEType.JPEG );
          }
        else if ( imagePath.endsWith( Asset.PNG_FILE_SUFFIX ) )
          {
          return ( MIMEType.PNG );
          }

        throw new IllegalStateException( "Currently only JPEG & PNG assets are supported" );


      case REMOTE_URL:

        return ( asset.getMIMEType() );


      default:
        // Fall through
      }

    throw ( new IllegalStateException( "Invalid asset type: " + type ) );
    }


  /*****************************************************
   *
   * Makes a request for this asset to be returned
   * asynchronously as encoded data. The encoding should
   * match the MIME type returned by {@link #getMimeType}.
   *
   *****************************************************/
  public static void requestImageBytes( Context context, Asset asset, IImageBytesConsumer imageBytesConsumer )
    {
    ( new GetBytesTask( context, asset, imageBytesConsumer ) ).execute();
    }


  /*****************************************************
   *
   * Returns the bytes from an input stream, or an exception
   * if there was an error.
   *
   *****************************************************/
  private static Object getBytesOrError( BufferedInputStream bis )
    {
    try
      {
      ByteArrayOutputStream baos = new ByteArrayOutputStream( bis.available() );

      byte[] buffer = new byte[ READ_BUFFER_SIZE_IN_BYTES ];

      int byteCount = -1;

      while ( ( byteCount = bis.read( buffer ) ) >= 0 )
        {
        baos.write( buffer, 0, byteCount );
        }

      return ( baos.toByteArray() );
      }
    catch ( IOException ioe )
      {
      return ( ioe );
      }
    finally
      {
      try
        {
        bis.close();
        }
      catch ( IOException ioe )
        {/* Already returning something so just ignore this one */}
      }
    }


  /*****************************************************
   *
   * Requests an image bitmap from an asset.
   *
   * Must be called on the UI thread.
   *
   *****************************************************/
  public static void requestImage( Context context, Asset asset, IImageConsumer imageConsumer )
    {
    switch ( asset.getType() )
      {
      case IMAGE_URI:
      case IMAGE_BYTES:
      case IMAGE_FILE:
      case BITMAP_RESOURCE_ID:

        // Get the image bytes the convert them into a Bitmap

        requestImageBytes( context, asset, new BytesToBitmapConvertor( imageConsumer ) );

        return;

      case BITMAP:

        imageConsumer.onImageAvailable( asset, asset.getBitmap() );

        return;


      case REMOTE_URL:

        // Get the image loader to download the image
        ImageLoader.getInstance( context ).requestRemoteImage( IMAGE_CLASS_STRING_ASSET, asset, asset.getRemoteURL(), imageConsumer );

        return;
      }

    throw ( new UnsupportedOperationException( "Cannot get image from unknown asset type: " + asset.getType() ) );
    }


  /*****************************************************
   *
   * Ensures that all the assets in the supplied list, are
   * parcelable. Any that aren't are converted to file-backed
   * assets and replaced.
   *
   *****************************************************/
  public static ArrayList<Asset> toParcelableList( Context context, ArrayList<Asset> assetArrayList )
    {
    // Scan through the list

    for ( int assetIndex = 0; assetIndex < assetArrayList.size(); assetIndex ++ )
      {
      Asset originalAsset = assetArrayList.get( assetIndex );

      if ( ! originalAsset.getType().isParcelable() )
        {
        // Replace the asset with a parcelable one

        Asset replacementAsset = parcelableAsset( context, originalAsset );

        assetArrayList.set( assetIndex, replacementAsset );
        }
      }


    return ( assetArrayList );
    }


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A callback interface used to return the image bytes
   * for an asset.
   *
   *****************************************************/
  public interface IImageBytesConsumer
    {
    void onAssetBytes( Asset asset, byte[] byteArray );

    void onAssetError( Asset asset, Exception exception );
    }


  /*****************************************************
   *
   * Returns encoded image data for an asset.
   *
   *****************************************************/
  private static class GetBytesTask extends AsyncTask<Void, Void, Object>
    {
    private Context              mContext;
    private Asset                mAsset;
    private IImageBytesConsumer  mImageBytesConsumer;


    GetBytesTask( Context context, Asset asset, IImageBytesConsumer imageBytesConsumer )
      {
      mContext            = context;
      mAsset              = asset;
      mImageBytesConsumer = imageBytesConsumer;
      }


    @Override
    protected Object doInBackground( Void... voids )
      {
      Type type = mAsset.getType();

      BufferedInputStream bis;

      switch ( type )
        {
        case IMAGE_URI:

          try
            {
            bis = new BufferedInputStream( mContext.getContentResolver().openInputStream( mAsset.getImageURI() ) );

            return ( getBytesOrError( bis ) );
            }
          catch ( FileNotFoundException fnfe )
            {
            return ( fnfe );
            }


        case BITMAP_RESOURCE_ID:
          bis = new BufferedInputStream( mContext.getResources().openRawResource( mAsset.getBitmapResourceId() ) );
          return getBytesOrError( bis );


        case BITMAP:

          // Start the convertor task to convert the bitmap into JPEG data
          new BitmapToBytesConvertorTask( mAsset, mImageBytesConsumer ).execute( mAsset.getBitmap() );

          return ( null );


        case IMAGE_BYTES:
          return ( mAsset.getImageBytes() );


        case IMAGE_FILE:

          try
            {
            File file = new File( mAsset.getImageFilePath() );

            bis = new BufferedInputStream( new FileInputStream( file ) );

            return ( getBytesOrError( bis ) );
            }
          catch ( FileNotFoundException fnfe )
            {
            return ( fnfe );
            }


        case REMOTE_URL:

          // We need to use the ImageLoader to download the image at the remote URL

          ImageLoader.getInstance( mContext ).requestRemoteImage( IMAGE_CLASS_STRING_ASSET, mAsset.getRemoteURL(), new BitmapToBytesConvertorTask( mAsset, mImageBytesConsumer ) );

          return (null);


        default:
        }

      throw (new IllegalStateException( "Invalid asset type: " + type ));
      }

    @Override
    protected void onPostExecute( Object resultObject )
      {
      // We have been returned one of the following:
      //   - A byte array
      //   - An exception
      //   - null => the image is being downloaded from elsewhere
      //             so nothing is returned here.

      if ( resultObject == null )
        {
        // Do nothing here
        }
      else if ( resultObject instanceof Exception )
        {
        mImageBytesConsumer.onAssetError( mAsset, (Exception) resultObject );
        }
      else if ( resultObject instanceof byte[] )
        {
        mImageBytesConsumer.onAssetBytes( mAsset, (byte[]) resultObject );
        }
      }

    }


  /*****************************************************
   *
   * Consumes image bytes, converts them into a bitmap,
   * and then delivers them to an image consumer.
   *
   * TODO: Make this an async task that decodes the bitmap
   * TODO: on a background thread.
   *
   *****************************************************/
  private static class BytesToBitmapConvertor implements IImageBytesConsumer
    {
    private IImageConsumer  mImageConsumer;


    BytesToBitmapConvertor( IImageConsumer imageConsumer )
      {
      mImageConsumer   = imageConsumer;
      }


    @Override
    public void onAssetBytes( Asset asset, byte[] byteArray )
      {
      try
        {
        // Decode the bytes into a bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray( byteArray, 0, byteArray.length );


        // Deliver the bitmap to the image consumer
        mImageConsumer.onImageAvailable( asset, bitmap );
        }
      catch ( Exception exception )
        {
        Log.e( LOG_TAG, "Unable to decode asset bytes", exception );
        }
      }


    @Override
    public void onAssetError( Asset asset, Exception exception )
      {
      Log.e( LOG_TAG, "Unable to get bytes for asset", exception );
      }
    }


  /*****************************************************
   *
   * Consumes a bitmap, encodes it into image bytes,
   * and then delivers them to an image bytes consumer.
   *
   *****************************************************/
  private static class BitmapToBytesConvertorTask extends AsyncTask<Bitmap,Void,Object> implements IImageConsumer
    {
    private Asset                mAsset;
    private IImageBytesConsumer mImageBytesConsumer;


    BitmapToBytesConvertorTask( Asset asset, IImageBytesConsumer imageBytesConsumer )
      {
      mAsset              = asset;
      mImageBytesConsumer = imageBytesConsumer;
      }


    @Override
    public void onImageDownloading( Object key )
      {
      // We don't care where it comes from so ignore
      // this callback.
      }


    /*****************************************************
     *
     * Called when the remote image is available.
     *
     *****************************************************/
    @Override
    public void onImageAvailable( Object key, Bitmap bitmap )
      {
      // The key should be the asset
      if ( key != mAsset )
        {
        Log.e( LOG_TAG, "Received image for wrong asset" );

        return;
        }


      // Once the image downloader has supplied an image bitmap, we need
      // to encode it into JPEG data. We are currently on the UI thread,
      // so we want to start a background thread to do the encoding since
      // we don't know how long it will take.

      execute( bitmap );
      }


    /*****************************************************
     *
     * Encodes a bitmap into JPEG data on a background
     * thread.
     *
     *****************************************************/
    @Override
    protected Object doInBackground( Bitmap... bitmaps )
      {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      try
        {
        bitmaps[ 0 ].compress( Bitmap.CompressFormat.JPEG, Asset.BITMAP_TO_JPEG_QUALITY, baos );

        return ( baos.toByteArray() );
        }
      catch ( Exception exception )
        {
        Log.e( LOG_TAG, "Unable to encode bitmap to JPEG", exception );

        return ( exception );
        }
      finally
        {
        if ( baos != null )
          {
          try
            {
            baos.close();
            }
          catch ( IOException ioe )
            {
            Log.e( LOG_TAG, "Unable to close byte array output stream", ioe );
            }
          }
        }
      }


    /*****************************************************
     *
     * Called on the UI thread when the background task
     * has completed.
     *
     *****************************************************/
    @Override
    protected void onPostExecute( Object resultObject )
      {
      if ( resultObject instanceof byte[] )
        {
        mImageBytesConsumer.onAssetBytes( mAsset, (byte[]) resultObject );
        }

      // We didn't get the result we wanted
      mImageBytesConsumer.onAssetError( mAsset, (Exception)resultObject );
      }


    }

  }

