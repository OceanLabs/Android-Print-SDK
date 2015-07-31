/*****************************************************
 *
 * Asset.java
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
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
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;

import ly.kite.util.ImageLoader;


///// Class Declaration /////

/*****************************************************
 *
 * This class represents an image asset that may be supplied
 * to the SDK for eventual printing.
 *
 * Image assets can be supplied from various sources, such
 * as bitmaps, resources, or files.
 *
 *****************************************************/
public class Asset implements Parcelable
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                       = "Asset";

  private static final int BITMAP_TO_JPEG_QUALITY = 80;

  public  static final String  JPEG_FILE_SUFFIX_PRIMARY      = ".jpg";
  public  static final String  JPEG_FILE_SUFFIX_SECONDARY    = ".jpeg";
  public  static final String  PNG_FILE_SUFFIX               = ".png";

  public  static final String  IMAGE_CLASS_STRING_ASSET      = "asset";


  ////////// Static Variable(s) //////////

  public static final Parcelable.Creator<Asset> CREATOR = new Parcelable.Creator<Asset>()
    {
    public Asset createFromParcel( Parcel in )
      {
      return ( new Asset( in ) );
      }

    public Asset[] newArray( int size )
      {
      return ( new Asset[ size ] );
      }
    };


  // TODO: Remove
  //private static final long serialVersionUID = 0L;


  ////////// Member Variable(s) //////////

  private AssetType  mType;
  private Uri        mImageUri;
  private URL        mRemoteURL;
  private int        mBitmapResourceId;
  private Bitmap     mBitmap;
  private String     mImagePath;
  private byte[]     mImageBytes;
  private MIMEType   mMIMEType;

  private boolean    mHasBeenUploaded;

  // The next two are only valid once an asset has been mHasBeenUploaded to the server
  private long       mId;
  private URL        mPreviewURL;


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
  public static Asset createAsCachedFile( Context context, Bitmap bitmap )
    {
    // Generate a random file name within the cache
    Pair<String,String> imageDirectoryAndFilePath = ImageLoader.getInstance( context ).getImageDirectoryAndFilePath( IMAGE_CLASS_STRING_ASSET, UUID.randomUUID().toString() );

    File imageDirectory = new File( imageDirectoryAndFilePath.first );

    imageDirectory.mkdirs();

    return ( createAsCachedFile( bitmap, imageDirectoryAndFilePath.second + JPEG_FILE_SUFFIX_PRIMARY ) );
    }


  /*****************************************************
   *
   * Creates a new asset from a bitmap, but writes it out
   * to a file.
   *
   *****************************************************/
  public static Asset createAsCachedFile( Bitmap bitmap, String filePath )
    {
    // Write the bitmap to the file

    FileOutputStream fos = null;

    try
      {
      fos = new FileOutputStream( filePath );

      bitmap.compress( Bitmap.CompressFormat.JPEG, BITMAP_TO_JPEG_QUALITY, fos );

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


  ////////// Constructor(s) //////////

  /*****************************************************
   *
   * Constructs an asset from a content URI.
   *
   *****************************************************/
  public Asset( Uri uri )
    {
    // Check that we support the scheme

    if ( ! uri.getScheme().equalsIgnoreCase( "content" ) /*&& !uri.getScheme().equalsIgnoreCase("http") && !uri.getScheme().equalsIgnoreCase("https")*/ )
      {
      throw new IllegalArgumentException( "Only uris with content schemes are currently supported, your scheme " + uri.getScheme() + " is not" );
      }


    mType     = AssetType.IMAGE_URI;
    mImageUri = uri;
    }


  /*****************************************************
   *
   * Constructs an asset from a remote URL.
   *
   *****************************************************/
  public Asset( URL url )
    {
    // Check that we support the protocol

    if ( ! url.getProtocol().equalsIgnoreCase( "http" ) && !url.getProtocol().equalsIgnoreCase( "https" ) )
      {
      throw new IllegalArgumentException( "Currently only support http and https URL schemes" );
      }


    // Check that we support the file type

    String file = url.getFile().toLowerCase();

    if ( file.endsWith( JPEG_FILE_SUFFIX_PRIMARY ) || file.endsWith( JPEG_FILE_SUFFIX_SECONDARY ) )
      {
      mMIMEType = MIMEType.JPEG;
      }
    else if ( file.endsWith( PNG_FILE_SUFFIX ) )
      {
      mMIMEType = MIMEType.PNG;
      }
    else
      {
      throw new IllegalArgumentException( "Currently only support URL's the identify the mime mType by ending with a supported file extension i.e. '.jpeg', '.jpg' or '.png' thus '" + file + "' is not valid." );
      }


    mType      = AssetType.REMOTE_URL;
    mRemoteURL = url;
    }


  /*****************************************************
   *
   * Constructs an asset from an image file.
   *
   *****************************************************/
  public Asset( String imagePath )
    {
    // Check that we support the file type

    String path = imagePath.toLowerCase();

    if ( ! path.endsWith( JPEG_FILE_SUFFIX_PRIMARY   ) &&
         ! path.endsWith( JPEG_FILE_SUFFIX_SECONDARY ) &&
         ! path.endsWith( PNG_FILE_SUFFIX ) )
      {
      throw new IllegalArgumentException( "Currently only JPEG & PNG assets are supported" );
      }


    mType      = AssetType.IMAGE_PATH;
    mImagePath = imagePath;
    }


  /*****************************************************
   *
   * Constructs an asset from a resource.
   *
   *****************************************************/
  public Asset( int bitmapResourceId )
    {
    mType             = AssetType.BITMAP_RESOURCE_ID;
    mBitmapResourceId = bitmapResourceId;
    }


  /*****************************************************
   *
   * Constructs an asset from a bitmap.
   *
   *****************************************************/
  public Asset( Bitmap bitmap )
    {
    mType   = AssetType.BITMAP;
    mBitmap = bitmap;
    }


  /*****************************************************
   *
   * Constructs an asset from a image data.
   *
   *****************************************************/
  public Asset( byte[] imageBytes, MIMEType mimeType )
    {
    mType       = AssetType.IMAGE_BYTES;
    mImageBytes = imageBytes;
    mMIMEType   = mimeType;
    }


  /*****************************************************
   *
   * Constructs an asset from a parcel.
   *
   *****************************************************/
  private Asset( Parcel sourceParcel )
    {
    mType             = AssetType.valueOf( sourceParcel.readString() );
    mImageUri         = (Uri)sourceParcel.readValue( Uri.class.getClassLoader() );
    mRemoteURL        = (URL)sourceParcel.readSerializable();
    mBitmapResourceId = sourceParcel.readInt();
    mImagePath        = sourceParcel.readString();
    mMIMEType         = MIMEType.fromString( sourceParcel.readString() );
    mHasBeenUploaded  = (Boolean)sourceParcel.readValue( Boolean.class.getClassLoader() );
    mId               = sourceParcel.readLong();
    mPreviewURL       = (URL)sourceParcel.readSerializable();
    }


  ////////// Parcelable Method(s) //////////

  @Override
  public int describeContents()
    {
    return ( 0 );
    }


  @Override
  public void writeToParcel( Parcel targetParcel, int flags )
    {
    // Make sure we can be parcelled
    if ( ! mType.isParcelable() ) throw ( new IllegalStateException( mType.name() + " asset cannot be parcelled" ) );

    targetParcel.writeString( mType.name() );
    targetParcel.writeValue( mImageUri );
    targetParcel.writeSerializable( mRemoteURL );
    targetParcel.writeInt( mBitmapResourceId );
    targetParcel.writeString( mImagePath );
    targetParcel.writeString( mMIMEType != null ? mMIMEType.getMIMETypeString() : null );
    targetParcel.writeValue( mHasBeenUploaded );
    targetParcel.writeLong( mId );
    targetParcel.writeSerializable( mPreviewURL );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns the asset type.
   *
   *****************************************************/
  public AssetType getType()
    {
    return ( mType );
    }


  /*****************************************************
   *
   * Returns the bitmap resource id.
   *
   * @throw IllegalStateException if the asset was not
   *        constructed from a bitmap resource.
   *
   *****************************************************/
  public int getBitmapResourceId()
    {
    if ( mType != AssetType.BITMAP_RESOURCE_ID ) throw ( new IllegalStateException( "The bitmap resource id has been requested, but the asset type is: " + mType ) );

    return ( mBitmapResourceId );
    }


  /*****************************************************
   *
   * Returns the bitmap.
   *
   * @throw IllegalStateException if the asset was not
   *        constructed from a bitmap.
   *
   *****************************************************/
  public Bitmap getBitmap()
    {
    if ( mType != AssetType.BITMAP ) throw ( new IllegalStateException( "The bitmap has been requested, but the asset type is: " + mType ) );

    return ( mBitmap );
    }


  /*****************************************************
   *
   * Returns the remote URL.
   *
   * @throw IllegalStateException if the asset was not
   *        constructed from a remote URL.
   *
   *****************************************************/
  public URL getRemoteURL()
    {
    if ( mType != AssetType.REMOTE_URL ) throw ( new IllegalStateException( "The remote URL has been requested, but the asset type is: " + mType ) );

    return ( mRemoteURL );
    }


  /*****************************************************
   *
   * Saves the results of uploading.
   *
   *****************************************************/
  void markAsUploaded( long assetId, URL previewURL )
    {
    mHasBeenUploaded = true;
    mId              = assetId;
    mPreviewURL      = previewURL;
    }


  /*****************************************************
   *
   * Returns true if the asset has been uploaded.
   *
   *****************************************************/
  public boolean hasBeenUploaded()
    {
    return ( mHasBeenUploaded );
    }


  /*****************************************************
   *
   * Returns the id following upload.
   *
   *****************************************************/
  public long getId()
    {
    if ( ! mHasBeenUploaded ) throw ( new IllegalStateException( "The id cannot be returned if the asset has not been uploaded" ) );

    return ( mId );
    }


  /*****************************************************
   *
   * Returns the preview URL following upload.
   *
   *****************************************************/
  public URL getPreviewURL()
    {
    if ( ! mHasBeenUploaded ) throw ( new IllegalStateException( "The preview URL cannot be returned if the asset has not been uploaded" ) );

    return ( mPreviewURL );
    }


  /*****************************************************
   *
   * Returns the MIME type for the asset.
   *
   *****************************************************/
  public MIMEType getMimeType( Context context )
    {
    switch ( mType )
      {
      case IMAGE_URI:

        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap     mimeTypeMap     = MimeTypeMap.getSingleton();

        return ( MIMEType.fromString( contentResolver.getType( mImageUri ) ) );


      case BITMAP_RESOURCE_ID:

        // Decode the bounds of the bitmap resource to get the MIME type

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource( context.getResources(), mBitmapResourceId, options );

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
        return ( mMIMEType );


      case IMAGE_PATH:

        String imagePath = mImagePath.toLowerCase();

        if ( imagePath.endsWith( JPEG_FILE_SUFFIX_PRIMARY ) || imagePath.endsWith( JPEG_FILE_SUFFIX_SECONDARY ) )
          {
          return ( MIMEType.JPEG );
          }
        else if ( imagePath.endsWith( PNG_FILE_SUFFIX ) )
          {
          return ( MIMEType.PNG );
          }

        throw new IllegalStateException( "Currently only JPEG & PNG assets are supported" );


      case REMOTE_URL:
        return ( mMIMEType );


      default:
        // Fall through
      }

    throw ( new IllegalStateException( "Invalid asset type: " + mType ) );
    }


  // We no longer provide the length because it is inefficient to do this separately from
  // retrieving the bytes.

  // TODO: Remove
//  public void getBytesLength( final Context c, final AssetGetBytesLengthListener listener )
//    {
//
//    AsyncTask<Void, Void, Object> t = new AsyncTask<Void, Void, Object>()
//    {
//    @Override
//    protected Object doInBackground( Void... voids )
//      {
//      switch ( mType )
//        {
//        case BITMAP_RESOURCE_ID:
//        {
//        InputStream is = c.getResources().openRawResource( mBitmapResourceId );
//        try
//          {
//          int avail = is.available();
//          is.close();
//          return Long.valueOf( avail );
//          }
//        catch ( IOException e )
//          {
//          return e;
//          }
//        }
//        case IMAGE_BYTES:
//          return Long.valueOf( mImageBytes.length );
//        case IMAGE_URI:
//        {
//        InputStream is = null;
//        try
//          {
//          is = c.getContentResolver().openInputStream( mImageUri );
//          long avail = is.available();
//          return Long.valueOf( avail );
//          }
//        catch ( Exception ex )
//          {
//          return ex;
//          }
//        finally
//          {
//          try
//            {
//            is.close();
//            }
//          catch ( Exception ex )
//            {/* Ignore as we're already returning something */}
//          }
//        }
//        case IMAGE_PATH:
//        {
//        File file = new File( mImagePath );
//        return Long.valueOf( file.length() );
//        }
//        case REMOTE_URL:
//          return Long.valueOf( 0 );
//        default:
//          throw new IllegalStateException( "should never arrive here" );
//        }
//      }
//
//    @Override
//    protected void onPostExecute( Object o )
//      {
//      if ( o instanceof Exception )
//        {
//        listener.onError( Asset.this, (Exception) o );
//        }
//      else
//        {
//        listener.onBytesLength( Asset.this, ((Long) o).longValue() );
//        }
//      }
//    };
//
//    t.execute();
//    }

  private Object readBytesOrError( InputStream is )
    {
    try
      {
      ByteArrayOutputStream baos = new ByteArrayOutputStream( is.available() );
      byte[] buffer = new byte[ 8192 ];
      int numRead = -1;
      while ( (numRead = is.read( buffer )) != -1 )
        {
        baos.write( buffer, 0, numRead );
        }

      return baos.toByteArray();
      }
    catch ( IOException e )
      {
      return e;
      }
    finally
      {
      try
        {
        is.close();
        }
      catch ( IOException ex )
        {/* Already returning something so just ignore this one */}
      }
    }

  public void getBytes( final Context c, final AssetGetBytesListener listener )
    {
    AsyncTask<Void, Void, Object> t = new AsyncTask<Void, Void, Object>()
    {
    @Override
    protected Object doInBackground( Void... voids )
      {
      switch ( mType )
        {
        case BITMAP_RESOURCE_ID:
        {
        BufferedInputStream is = new BufferedInputStream( c.getResources().openRawResource( mBitmapResourceId ) );
        return readBytesOrError( is );
        }
        case IMAGE_BYTES:
          return mImageBytes;
        case IMAGE_URI:
        {
        try
          {
          BufferedInputStream is = new BufferedInputStream( c.getContentResolver().openInputStream( mImageUri ) );
          return readBytesOrError( is );
          }
        catch ( FileNotFoundException ex )
          {
          return ex;
          }
        }
        case IMAGE_PATH:
        {
        try
          {
          File file = new File( mImagePath );
          BufferedInputStream is = new BufferedInputStream( new FileInputStream( file ) );
          return readBytesOrError( is );
          }
        catch ( FileNotFoundException ex )
          {
          return ex;
          }
        }
        case REMOTE_URL:
          throw new UnsupportedOperationException( "Getting the bytes of a remote url is not supported!" );
        default:
          throw new IllegalStateException( "should never arrive here" );
        }
      }

    @Override
    protected void onPostExecute( Object o )
      {
      if ( o instanceof Exception )
        {
        listener.onError( Asset.this, (Exception) o );
        }
      else
        {
        listener.onBytes( Asset.this, (byte[]) o );
        }
      }
    };

    t.execute();
    }

  @Override
  public boolean equals( Object o )
    {
    if ( !(o instanceof Asset) )
      {
      return false;
      }

    Asset a = (Asset) o;
    if ( a == this )
      {
      return true;
      }

    if ( a.mMIMEType != this.mMIMEType || a.mType != this.mType )
      {
      return false;
      }

    switch ( this.mType )
      {
      case REMOTE_URL:
        return a.mRemoteURL.equals( this.mRemoteURL );
      case IMAGE_URI:
        return a.mImageUri.equals( this.mImageUri );
      case IMAGE_PATH:
        return a.mImagePath.equals( this.mImagePath );
      case BITMAP_RESOURCE_ID:
        return a.mBitmapResourceId == this.mBitmapResourceId;
      case IMAGE_BYTES:
        return Arrays.equals( a.mImageBytes, this.mImageBytes );
      }

    throw ( new IllegalStateException( "Invalid asset type: " + mType ) );
    }

  @Override
  public int hashCode()
    {
    switch ( this.mType )
      {
      case REMOTE_URL:
        return this.mRemoteURL.hashCode();
      case IMAGE_URI:
        return this.mImageUri.hashCode();
      case IMAGE_PATH:
        return this.mImagePath.hashCode();
      case BITMAP_RESOURCE_ID:
        return mBitmapResourceId;
      case IMAGE_BYTES:
        return Arrays.hashCode( this.mImageBytes );
      }

    throw ( new IllegalStateException( "Invalid asset type: " + mType ) );
    }


  // TODO: Remove
//  private void writeObject( java.io.ObjectOutputStream out ) throws IOException
//    {
//    out.writeObject( mImageUri == null ? null : mImageUri.toString() );
//    out.writeObject( mRemoteURL );
//    out.writeInt( mBitmapResourceId );
//    out.writeObject( mImagePath );
//    out.writeInt( mImageBytes != null ? mImageBytes.length : 0 );
//    if ( mImageBytes != null && mImageBytes.length > 0 )
//      {
//      out.write( mImageBytes );
//      }
//
//    out.writeObject( mMIMEType == null ? null : mMIMEType.getMIMETypeString() );
//    out.writeInt( mType.ordinal() );
//    out.writeBoolean( mHasBeenUploaded );
//    out.writeLong( mId );
//    out.writeObject( mPreviewURL );
//    }
//
//  private void readObject( java.io.ObjectInputStream in ) throws IOException, ClassNotFoundException
//    {
//    String imageUriString = (String) in.readObject();
//    if ( imageUriString != null )
//      {
//      mImageUri = Uri.parse( imageUriString );
//      }
//
//    mRemoteURL = (URL) in.readObject();
//    mBitmapResourceId = in.readInt();
//    mImagePath = (String) in.readObject();
//    int numImageBytes = in.readInt();
//    if ( numImageBytes > 0 )
//      {
//      this.mImageBytes = new byte[ numImageBytes ];
//      in.read( this.mImageBytes );
//      }
//
//    String mimeTypeString = (String) in.readObject();
//    if ( mimeTypeString != null )
//      {
//      mMIMEType = MimeType.fromString( mimeTypeString );
//      }
//    mType = AssetType.values()[ in.readInt() ];
//    mHasBeenUploaded = in.readBoolean();
//    mId = in.readLong();
//    mPreviewURL = (URL) in.readObject();
//    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * An MIME type.
   *
   *****************************************************/
  public static enum MIMEType
    {
    JPEG( "image/jpeg" ),
    PNG ( "image/png" );


    private final String  mMIMETypeString;


    MIMEType( String mimeTypeString )
      {
      mMIMETypeString = mimeTypeString;
      }


    public String getMIMETypeString()
      {
      return ( mMIMETypeString );
      }

    public static MIMEType fromString( String mimeType )
      {
      if ( mimeType == null ) return ( null );

      if ( mimeType.equalsIgnoreCase( JPEG.mMIMETypeString ) )
        {
        return JPEG;
        }
      else if ( mimeType.equalsIgnoreCase( PNG.mMIMETypeString ) )
        {
        return PNG;
        }

      throw new UnsupportedOperationException( "Requested mimetype " + mimeType + " is not supported" );
      }
    }


  /*****************************************************
   *
   * The type of asset.
   *
   *****************************************************/
  public enum AssetType
    {
    IMAGE_URI          ( true ),
    BITMAP_RESOURCE_ID ( true ),
    BITMAP             ( false ),
    IMAGE_BYTES        ( false ),
    IMAGE_PATH         ( true ),
    REMOTE_URL         ( true );


    private boolean mIsParcelable;


    private AssetType( boolean isParcelable )
      {
      mIsParcelable = isParcelable;
      }


    /*****************************************************
     *
     * Returns true if the asset type is safe to be parcelled.
     *
     *****************************************************/
    public boolean isParcelable()
      {
      return ( mIsParcelable );
      }
    }


  }
