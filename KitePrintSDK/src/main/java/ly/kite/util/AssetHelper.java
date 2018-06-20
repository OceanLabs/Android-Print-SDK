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

package ly.kite.util;


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
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import ly.kite.KiteSDK;
import ly.kite.image.IImageConsumer;
import ly.kite.image.ImageAgent;
import ly.kite.image.ImageLoadRequest;
import ly.kite.ordering.ImageSpec;
import ly.kite.util.Asset.Type;
import ly.kite.util.Asset.MIMEType;


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

  @SuppressWarnings("unused")
  static private final String LOG_TAG                       = "AssetHelper";

  static private final String BASKET_DIRECTORY              = "basket";

  static private final int    READ_BUFFER_SIZE_IN_BYTES     = 8192;  // 8 KB
  static private final int    TRANSFER_BUFFER_SIZE_IN_BYTES = 8192;  // 8 KB


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Clears any asset image files for a particular image
   * category.
   *
   *****************************************************/
  static private void removeDirectory( String assetDirectoryString )
    {
    File imageDirectory = new File( assetDirectoryString );


    // Go through all the files and delete them

    File[] imageFiles = imageDirectory.listFiles();

    if ( imageFiles != null )
      {
      for ( File imageFile : imageDirectory.listFiles() )
        {
        imageFile.delete();
        }
      }


    // Delete the directory itself
    imageDirectory.delete();
    }


  /*****************************************************
   *
   * Clears any asset image files for a particular image
   * category.
   *
   *****************************************************/
  static private void clearCacheAssetsForCategory( Context context, String imageCategory )
    {
    // Get the image cache directory
    String imageCacheDirectoryPath = ImageAgent.getInstance( context ).getImageCacheDirectoryForCategory( imageCategory );

    removeDirectory( imageCacheDirectoryPath );
    }


  /*****************************************************
   *
   * Clears any asset image files for a particular image
   * category, excluding those supplied.
   *
   * Used mostly to clear any cached product images
   *
   *
   *****************************************************/
  static private void clearCacheAssetsForCategory( Context context, String imageCategory, List<URL> keepImageURLList )
    {
    // Convert all the URL strings into cache file names, and store them in a set

    HashSet<String> keepImageFileNameSet = new HashSet<>();

    ImageAgent imageAgent = ImageAgent.getInstance( context );

    for ( URL keepImageURL : keepImageURLList )
      {
      if ( keepImageURL != null )
        {
        String keepImageFileName = imageAgent.getImageCacheFileName( keepImageURL );

        if ( KiteSDK.DEBUG_PRODUCT_ASSET_EXPIRY ) Log.d( LOG_TAG, "Keeping image file: " + keepImageFileName );

        keepImageFileNameSet.add( keepImageFileName );
        }
      }


    // Go through all the files in the cache directory, and remove anything that's not in the set
    // we just created.

    String imageCacheDirectoryPath = imageAgent.getImageCacheDirectoryForCategory( imageCategory );

    File imageCacheDirectory = new File( imageCacheDirectoryPath );

    File[] fileList = imageCacheDirectory.listFiles();

    if ( fileList != null )
      {
      for ( File file : fileList )
        {
        // Skip anything starting with '.'

        String fileName = file.getName();

        if ( fileName == null || ( fileName.length() > 0 && fileName.charAt( 0 ) == '.' ) )
          {
          if ( KiteSDK.DEBUG_PRODUCT_ASSET_EXPIRY ) Log.d( LOG_TAG, "Ignoring file: " + fileName );

          continue;
          }

        if ( KiteSDK.DEBUG_PRODUCT_ASSET_EXPIRY ) Log.d( LOG_TAG, "Checking file: " + fileName );


        // If this file is not in the keep set - delete it

        if ( keepImageFileNameSet.contains( fileName ) )
          {
          if ( KiteSDK.DEBUG_PRODUCT_ASSET_EXPIRY ) Log.d( LOG_TAG, "Keeping file: " + fileName );
          }
        else
          {
          if ( KiteSDK.DEBUG_PRODUCT_ASSET_EXPIRY ) Log.d( LOG_TAG, "Deleting file: " + fileName );

          file.delete();
          }
        }
      }

    }


  /*****************************************************
   *
   * Clears any cached product image files, excluding those
   * supplied.
   *
   *****************************************************/
  static public void clearProductAssets( Context context, List<URL> keepImageURLList )
    {
    clearCacheAssetsForCategory( context, KiteSDK.IMAGE_CATEGORY_PRODUCT_ITEM, keepImageURLList );
    }


  /*****************************************************
   *
   * Clears any session asset image files.
   *
   *****************************************************/
  static public void clearSessionAssets( Context context )
    {
    clearCacheAssetsForCategory( context, KiteSDK.IMAGE_CATEGORY_SESSION_ASSET );
    }


  /*****************************************************
   *
   * Returns the directory for a basket id.
   *
   *****************************************************/
  static private String basketDirectoryString( Context context, long basketId )
    {
    return ( context.getFilesDir() + File.separator + BASKET_DIRECTORY + File.separator + String.valueOf( basketId ) );
    }


  /*****************************************************
   *
   * Clears any basket asset image files.
   *
   *****************************************************/
  static public void clearBasketAssets( Context context, long basketId )
    {
    removeDirectory( basketDirectoryString( context, basketId ) );
    }


  /*****************************************************
   *
   * Moves asset image files between baskets.
   *
   *****************************************************/
  static public void moveBasket( Context context, long oldBasketId, long newBasketId )
    {
    File oldBasketDirectory = new File( basketDirectoryString( context, oldBasketId ) );
    File newBasketDirectory = new File( basketDirectoryString( context, newBasketId ) );

    oldBasketDirectory.renameTo( newBasketDirectory );
    }


  /*****************************************************
   *
   * Ensures that the directory exists for the supplied
   * file path.
   *
   *****************************************************/
  static public void ensureDirectoryExists( String filePath )
    {
    // Determine the directory

    File file = new File( filePath );

    File directory = file.getParentFile();


    // Make sure the directory exists
    directory.mkdirs();
    }


  /*****************************************************
   *
   * Generates the file path of an asset file, and
   * ensures that the directory exists.
   *
   *****************************************************/
  static public String reserveFileAsset( Context context, String directoryPath, MIMEType mimeType )
    {
    File directory = new File( directoryPath );

    directory.mkdirs();

    String filePath = directoryPath + File.separator + ImageAgent.toSafeString( UUID.randomUUID().toString() ) + ( mimeType != null ? mimeType.primaryFileSuffix() : "" );

    return ( filePath );
    }


  /*****************************************************
   *
   * Generates the file path of an asset file, and
   * ensures that the directory exists.
   *
   *****************************************************/
  static public String reserveCacheFileAsset( Context context, String imageCategory, MIMEType mimeType )
    {
    // Generate a random file name within the cache
    Pair<String, String> imageDirectoryAndFilePath = ImageAgent.getInstance( context ).getImageCacheDirectoryAndFilePath( imageCategory, UUID.randomUUID().toString() );

    File imageDirectory = new File( imageDirectoryAndFilePath.first );

    boolean directoryCreated = imageDirectory.mkdirs();

    return ( imageDirectoryAndFilePath.second + ( mimeType != null ? mimeType.primaryFileSuffix() : "" ) );
    }


  /*****************************************************
   *
   * Generates the file path of a session asset file, and
   * ensures that the directory exists.
   *
   *****************************************************/
  static public String reserveSessionAsset( Context context, MIMEType mimeType )
    {
    return ( reserveCacheFileAsset( context, KiteSDK.IMAGE_CATEGORY_SESSION_ASSET, mimeType ) );
    }


  /*****************************************************
   *
   * Generates the file path of a basket asset file, and
   * ensures that the directory exists.
   *
   *****************************************************/
  static public String reserveBasketAsset( Context context, long basketId, Asset sourceAsset )
    {
    return ( reserveFileAsset( context, basketDirectoryString( context, basketId ), getMimeType( context, sourceAsset ) ) );
    }


  /*****************************************************
   *
   * Creates a placeholder asset on the filesystem.
   *
   *****************************************************/
  static public Asset createAsSessionAsset( Context context, Asset.MIMEType mimeType )
    {
    return ( new Asset( reserveSessionAsset( context, mimeType ) ) );
    }


  /*****************************************************
   *
   * Creates a new asset from a bitmap, but writes it out
   * to a file. The file path is automatically generated.
   *
   *****************************************************/
  static public Asset createAsSessionAsset( Context context, byte[] imageBytes, Asset.MIMEType mimeType )
    {
    return ( createAsSessionAsset( imageBytes, reserveSessionAsset( context, mimeType ) ) );
    }


  /*****************************************************
   *
   * Transfers data from an input stream to an output stream.
   *
   *****************************************************/
  static public void transferBytes( InputStream inputStream, OutputStream outputStream ) throws IOException
    {
    byte[] transferBuffer = new byte[ TRANSFER_BUFFER_SIZE_IN_BYTES ];

    int byteCount;

    while ( ( byteCount = inputStream.read( transferBuffer ) ) >= 0 )
      {
      outputStream.write( transferBuffer, 0, byteCount );
      }
    }


  /*****************************************************
   *
   * Creates a new asset from a bitmap, but writes it out
   * to a file. The file path is automatically generated.
   *
   *****************************************************/
  static public Asset createAsSessionAsset( Context context, int bitmapResourceId, Asset.MIMEType mimeType )
    {
    String filePath = reserveSessionAsset( context, mimeType );


    // Encode the bitmap directly to the filesystem, to avoid using more memory than necessary.

    InputStream is = null;
    FileOutputStream fos = null;

    try
      {
      is = context.getResources().openRawResource( bitmapResourceId );
      fos = new FileOutputStream( filePath );

      transferBytes( is, fos );

      return ( new Asset( filePath ) );
      }
    catch ( Exception e )
      {
      Log.e( LOG_TAG, "Unable to copy resource to file", e );

      return ( null );
      }
    finally
      {
      if ( is != null )
        {
        try
          {
          is.close();
          }
        catch ( IOException ioe )
          {
          // Do nothing
          }
        }
      if ( fos != null )
        {
        try
          {
          fos.close();
          }
        catch ( IOException ioe )
          {
          // Do nothing
          }
        }

      }
    }


  /*****************************************************
   *
   * Creates a new asset from a bitmap, but writes it out
   * to a file. The file path is automatically generated.
   *
   *****************************************************/
  static public void replaceAsset( Bitmap bitmap, Asset targetAsset )
    {
    if ( targetAsset.getType() != Type.IMAGE_FILE )
      {
      throw ( new IllegalArgumentException( "Can only replace a file asset" ) );
      }

    // Make sure the directory exists
    ensureDirectoryExists( targetAsset.getImageFilePath() );

    saveToFile( bitmap, targetAsset.getImageFilePath() );
    }


  /*****************************************************
   *
   * Creates a new asset from a bitmap, but writes it out
   * to a file. The file path is automatically generated.
   *
   *****************************************************/
  static public Asset createAsSessionAsset( Context context, Bitmap bitmap )
    {
    String filePath = reserveSessionAsset( context, MIMEType.JPEG );

    return ( saveToFile( bitmap, filePath ) );
    }


  /*****************************************************
   *
   * Creates a new asset from a bitmap, but writes it out
   * to a file. The file path is automatically generated.
   *
   *****************************************************/
  static private Asset saveToFile( Bitmap bitmap, String filePath )
    {
    // Encode the bitmap directly to the filesystem, to avoid using more memory than necessary.

    FileOutputStream fos = null;
    BufferedOutputStream bos = null;

    try
      {
      fos = new FileOutputStream( filePath );
      bos = new BufferedOutputStream( fos );

      bitmap.compress( Bitmap.CompressFormat.JPEG, Asset.BITMAP_TO_JPEG_QUALITY, bos );

      return ( new Asset( filePath ) );
      }
    catch ( Exception e )
      {
      Log.e( LOG_TAG, "Unable to encode bitmap to JPEG", e );

      return ( null );
      }
    finally
      {
      if ( bos != null )
        {
        try
          {
          bos.close();
          }
        catch ( IOException ioe )
          {
          // Do nothing
          }
        }
      if ( fos != null )
        {
        try
          {
          fos.close();
          }
        catch ( IOException ioe )
          {
          // Do nothing
          }
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
  static public Asset parcelableAsset( Context context, Asset originalAsset )
    {
    Asset.Type type = originalAsset.getType();

    if ( type.isParcelable() ) return ( originalAsset );


    switch ( type )
      {
      case BITMAP:

        return ( createAsSessionAsset( context, originalAsset.getBitmap() ) );

      case IMAGE_BYTES:

        return ( createAsSessionAsset( context, originalAsset.getImageBytes(), originalAsset.getMIMEType() ) );
      }


    throw ( new IllegalStateException( "Unable to create parcelable asset from type: " + type ) );
    }


  /*****************************************************
   *
   * Creates a new asset from a bitmap, but writes it out
   * to a file.
   *
   *****************************************************/
  static public Asset createAsSessionAsset( byte[] imageBytes, String filePath )
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
   * Creates a basket asset from a source asset.
   *
   * @return A basket asset. This may be the source asset if
   *         it was already in the correct basket.
   *
   *****************************************************/
  static public Asset createAsBasketAsset( Context context, long basketId, Asset sourceAsset )
    {
    // It is perfectly valid to supply a null asset, in which case we return null back.
    if ( sourceAsset == null ) return ( null );

    // If the asset is already in the correct basket, we can simply return the source asset.
    if ( sourceAsset.getType() == Type.IMAGE_FILE && sourceAsset.getImageFilePath().startsWith( basketDirectoryString( context, basketId ) ) )
      {
      return ( sourceAsset );
      }


    // Create a new basket asset

    String basketFilePath = reserveBasketAsset( context, basketId, sourceAsset );

    File basketAssetFile = new File( basketFilePath );


    InputStream  sourceStream = null;
    OutputStream targetStream = null;

    try
      {
      sourceStream = getInputStream( context, sourceAsset );
      targetStream = new FileOutputStream( basketAssetFile );

      transferBytes( sourceStream, targetStream );

      Asset result = new Asset(basketAssetFile);
      //add preview image if available
      if(sourceAsset.hasPreviewImage())
        {
          result.setPreviewBitmap(sourceAsset.getPreviewBitmap());
        }
      return result;
      }
    catch ( IOException ioe )
      {
      Log.e( LOG_TAG, "Unable to copy asset to basket", ioe );

      return ( null );
      }
    finally
      {
      if ( sourceStream != null )
        {
        try
          {
          sourceStream.close();
          }
        catch ( IOException ioe )
          {
          Log.e( LOG_TAG, "Unable to close session stream", ioe );
          }
        }

      if ( targetStream != null )
        {
        try
          {
          targetStream.close();
          }
        catch ( IOException ioe )
          {
          Log.e( LOG_TAG, "Unable to close basket stream", ioe );
          }
        }
      }
    }


  /*****************************************************
   *
   * Creates a basket asset.
   *
   *****************************************************/
  static public Asset createExistingBasketAsset( Context context, long basketId, String imageFileName )
    {
    String imageFilePath = basketDirectoryString( context, basketId ) + File.separator + imageFileName;

    return ( new Asset( imageFilePath ) );
    }


  /*****************************************************
   *
   * Creates a list of basket asset fragments from session
   * assets.
   *
   *****************************************************/
  static public List<ImageSpec> createAsBasketAssets( Context context, long basketId, List<ImageSpec> imageSpecList )
    {
    if ( imageSpecList == null ) return ( new ArrayList<>( 0 ) );

    List<ImageSpec> basketImageSpecList = new ArrayList<>( imageSpecList.size() );

    for ( ImageSpec imageSpec : imageSpecList )
      {
      // Some products, such as photobooks, use null placeholders for blank pages.
      // Make sure we cah cope with both null image specs and null assets.

      Asset sourceAsset;

      if ( imageSpec != null && ( sourceAsset = imageSpec.getAsset() ) != null )
        {
        Asset basketAsset = createAsBasketAsset( context, basketId, sourceAsset );

        basketImageSpecList.add( imageSpec.createCopyWithReplacedAsset( basketAsset ) );
        }
      else
        {
        basketImageSpecList.add( null );
        }
      }

    return ( basketImageSpecList );
    }


  /*****************************************************
   *
   * Returns an input stream for the asset.
   *
   *****************************************************/
  static private InputStream getInputStream( Context context, Asset asset ) throws IOException
    {
    Asset.Type type = asset.getType();

    InputStream inputStream = null;

    switch ( type )
      {
      case IMAGE_URI:

        inputStream = context.getContentResolver().openInputStream( asset.getImageURI() );

        break;


      case BITMAP_RESOURCE_ID:

        inputStream = context.getResources().openRawResource( asset.getBitmapResourceId() );

        break;


      case BITMAP:

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Bitmap bitmap = asset.getBitmap();

        bitmap.compress( Bitmap.CompressFormat.JPEG, Asset.BITMAP_TO_JPEG_QUALITY, baos );

        baos.close();

        inputStream = new ByteArrayInputStream( baos.toByteArray() );

        break;


      case IMAGE_BYTES:

        inputStream = new ByteArrayInputStream( asset.getImageBytes() );

        break;


      case IMAGE_FILE:

        File file = new File( asset.getImageFilePath() );

        inputStream = new FileInputStream( file );

        break;


      case REMOTE_URL:

          HttpURLConnection urlConnection = ( HttpURLConnection ) asset.getRemoteURL().openConnection();
          Map<String, String> headerMap = asset.getURLHeaderMap();

          if( headerMap != null )
          {
              for ( Map.Entry<String, String> entry : headerMap.entrySet() )
              {
                  urlConnection.setRequestProperty( entry.getKey(), entry.getValue() );
              }
          }

          inputStream = urlConnection.getInputStream();

        break;


      default:
        throw( new IllegalStateException( "Input stream for asset type not supported : " + type ) );
      }


    return ( new BufferedInputStream( inputStream ) );
    }


  /*****************************************************
   *
   * Returns the underlying assets from an image spec list.
   *
   *****************************************************/
  static public List<Asset> sourceAssetListFrom( List<ImageSpec> imageSpecList )
    {
    if ( imageSpecList == null ) return ( new ArrayList<>( 0 ) );

    List<Asset> assetList = new ArrayList<>( imageSpecList.size() );

    for ( ImageSpec imageSpec : imageSpecList )
      {
      assetList.add( imageSpec.getAsset() );
      }

    return ( assetList );
    }


  /*****************************************************
   *
   * Returns the MIME type for the asset.
   *
   *****************************************************/
  static public MIMEType getMimeType( Context context, Asset asset )
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

        String imagePath = asset.getImageFilePath().toLowerCase( Locale.UK );

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
  static public void requestImageBytes( Context context, Asset asset, IImageBytesConsumer imageBytesConsumer )
    {
    switch ( asset.getType() )
      {
      case IMAGE_URI:
      case IMAGE_FILE:
      case BITMAP_RESOURCE_ID:
      case BITMAP:

        new GetBytesTask( context, asset, imageBytesConsumer ).execute();

        return;


      case IMAGE_BYTES:

        imageBytesConsumer.onAssetBytes( asset, asset.getImageBytes() );

        return;


      case REMOTE_URL:

        // Get the image loader to download the image

        BitmapToBytesConvertorTask convertorTask = new BitmapToBytesConvertorTask( asset, imageBytesConsumer );

        ImageAgent.with( context )
                .load( asset )
                .into( convertorTask, asset );
        //ImageAgent.getInstance( context ).requestImage( IMAGE_CATEGORY_SESSION_ASSET, , asset.getRemoteURL(), convertorTask );

        return;
      }


    throw ( new UnsupportedOperationException( "Cannot get image bytes from unknown asset type: " + asset.getType() ) );
    }


  /*****************************************************
   *
   * Returns the bytes from an input stream, or an exception
   * if there was an error.
   *
   *****************************************************/
  private static Object getBytes( BufferedInputStream bis ) throws Exception
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


  /*****************************************************
   *
   * Sets the image source for a request.
   *
   *****************************************************/
  static public void setSource( Asset asset, ImageLoadRequest.Builder imageRequestBuilder )
    {
    switch ( asset.getType() )
      {
      case IMAGE_FILE:

        imageRequestBuilder.load( new File( asset.getImageFilePath() ) );

        return;


      case BITMAP_RESOURCE_ID:

        imageRequestBuilder.load( asset.getBitmapResourceId() );

        return;


      case IMAGE_URI:

        imageRequestBuilder.load( asset.getImageURI() );

        return;


      case REMOTE_URL:

        imageRequestBuilder.load( asset.getRemoteURL(), asset.getURLHeaderMap(), KiteSDK.IMAGE_CATEGORY_SESSION_ASSET );

        return;


      case BITMAP:

        imageRequestBuilder.load( asset.getBitmap() );

        return;


      case IMAGE_BYTES:

        imageRequestBuilder.load( asset.getImageBytes() );

        return;
      }


    throw ( new UnsupportedOperationException( "Cannot set source for unknown asset type: " + asset.getType() ) );
    }


  /*****************************************************
   *
   * Ensures that all the assets in the supplied list, are
   * parcelable. Any that aren't are converted to file-backed
   * assets and replaced.
   *
   *****************************************************/
  static public ArrayList<Asset> toParcelableList( Context context, ArrayList<Asset> assetArrayList )
    {
    if ( assetArrayList == null ) assetArrayList = new ArrayList<>( 0 );


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
      BufferedInputStream bis = null;

      Type type = mAsset.getType();

      try
        {
        switch ( type )
          {
          case IMAGE_URI:

            bis = new BufferedInputStream( mContext.getContentResolver().openInputStream( mAsset.getImageURI() ) );

            return ( getBytes( bis ) );


          case BITMAP_RESOURCE_ID:

            bis = new BufferedInputStream( mContext.getResources().openRawResource( mAsset.getBitmapResourceId() ) );

            return ( getBytes( bis ) );


          case BITMAP:

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Bitmap bitmap = mAsset.getBitmap();

            bitmap.compress( Bitmap.CompressFormat.JPEG, Asset.BITMAP_TO_JPEG_QUALITY, baos );

            baos.close();

            return ( baos.toByteArray() );


          case IMAGE_FILE:

            File file = new File( mAsset.getImageFilePath() );

            bis = new BufferedInputStream( new FileInputStream( file ) );

            return ( getBytes( bis ) );
          }

        throw ( new IllegalStateException( "Invalid asset type: " + type ) );
        }
      catch ( Exception exception )
        {
        Log.e( LOG_TAG, "Unable to get image bytes", exception );

        return ( exception );
        }
      finally
        {
        if ( bis != null )
          {
          try
            {
            bis.close();
            }
          catch ( Exception exception )
            {
            Log.e( LOG_TAG, "Unable to close input stream", exception );
            }
          }
        }
      }


    @Override
    protected void onPostExecute( Object resultObject )
      {
      if ( resultObject == null )
        {
        // Do nothing
        }
      else if ( resultObject instanceof byte[] )
        {
        mImageBytesConsumer.onAssetBytes( mAsset, (byte[]) resultObject );
        }
      else if ( resultObject instanceof Exception )
        {
        mImageBytesConsumer.onAssetError( mAsset, (Exception)resultObject );
        }
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
    private IImageBytesConsumer  mImageBytesConsumer;


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
     * Called when an image could not be loaded.
     *
     *****************************************************/
    @Override
    public void onImageUnavailable( Object key, Exception exception )
      {
      // TODO
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
      if ( resultObject == null )
        {
        // Do nothing
        }
      else if ( resultObject instanceof byte[] )
        {
        mImageBytesConsumer.onAssetBytes( mAsset, (byte[]) resultObject );
        }
      else if ( resultObject instanceof Exception )
        {
        mImageBytesConsumer.onAssetError( mAsset, (Exception)resultObject );
        }
      }

    }

  }

