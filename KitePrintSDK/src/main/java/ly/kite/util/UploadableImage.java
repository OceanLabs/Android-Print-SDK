/*****************************************************
 *
 * UploadableImage.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2016 Kite Tech Ltd. https://www.kite.ly
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

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;
import java.util.List;

/*****************************************************
 *
 * This class holds an asset plus any upload details.
 *
 *****************************************************/
public class UploadableImage implements Parcelable
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "UploadableImage";


  ////////// Static Variable(s) //////////

  public static final Parcelable.Creator<UploadableImage> CREATOR = new Parcelable.Creator<UploadableImage>()
    {
    public UploadableImage createFromParcel( Parcel in )
      {
      return ( new UploadableImage( in ) );
      }

    public UploadableImage[] newArray( int size )
      {
      return ( new UploadableImage[ size ] );
      }
    };


  ////////// Member Variable(s) //////////

  private AssetFragment  mAssetFragment;

  private boolean        mHasBeenUploaded;
  private long           mUploadedAssetId;
  private URL            mPreviewURL;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns true if both the uploadable images are null,
   * or equal.
   *
   *****************************************************/
  static public boolean areBothNullOrEqual( UploadableImage uploadableImage1, UploadableImage uploadableImage2 )
    {
    if ( uploadableImage1 == null && uploadableImage2 == null ) return ( true );
    if ( uploadableImage1 == null || uploadableImage2 == null ) return ( false );

    return ( uploadableImage1.equals( uploadableImage2 ) );
    }


  /*****************************************************
   *
   * Returns true if both the uploadable image lists are
   * null, or equal.
   *
   *****************************************************/
  static public boolean areBothNullOrEqual( List<UploadableImage> list1, List<UploadableImage> list2 )
    {
    if ( list1 == null && list2 == null ) return ( true );
    if ( list1 == null || list2 == null ) return ( false );

    if ( list1.size() != list2.size() ) return ( false );


    int index = 0;

    for ( UploadableImage uploadableImage1 : list1 )
      {
      if ( ! UploadableImage.areBothNullOrEqual( uploadableImage1, list2.get( index ) ) ) return ( false );

      index ++;
      }


    return ( true );
    }


  ////////// Constructor(s) //////////

  public UploadableImage( AssetFragment assetFragment )
    {
    mAssetFragment = assetFragment;
    }


  public UploadableImage( Asset asset )
    {
    this ( new AssetFragment( asset ) );
    }


  UploadableImage( Parcel sourceParcel )
    {
    mAssetFragment    = sourceParcel.readParcelable( AssetFragment.class.getClassLoader() );
    mHasBeenUploaded  = (Boolean)sourceParcel.readValue( Boolean.class.getClassLoader() );
    mUploadedAssetId  = sourceParcel.readLong();
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
    targetParcel.writeParcelable( mAssetFragment, flags );
    targetParcel.writeValue( mHasBeenUploaded );
    targetParcel.writeLong( mUploadedAssetId );
    targetParcel.writeSerializable( mPreviewURL );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets the image to be an asset fragment.
   *
   *****************************************************/
  public void setImage( AssetFragment assetFragment )
    {
    mAssetFragment = assetFragment;
    }


  /*****************************************************
   *
   * Sets the image to be the whole area of an asset.
   *
   *****************************************************/
  public void setImage( Asset asset )
    {
    setImage( new AssetFragment( asset ) );
    }


  /*****************************************************
   *
   * Returns the asset fragment.
   *
   *****************************************************/
  public AssetFragment getAssetFragment()
    {
    return ( mAssetFragment );
    }


  /*****************************************************
   *
   * Returns the asset.
   *
   *****************************************************/
  public Asset getAsset()
    {
    return ( mAssetFragment.getAsset() );
    }


  /*****************************************************
   *
   * Returns the asset type.
   *
   *****************************************************/
  public Asset.Type getType()
    {
    return ( mAssetFragment.getAsset().getType() );
    }


  /*****************************************************
   *
   * Returns the asset remote URL.
   *
   *****************************************************/
  public URL getRemoteURL()
    {
    return ( mAssetFragment.getAsset().getRemoteURL() );
    }


  /*****************************************************
   *
   * Saves the results of uploading.
   *
   *****************************************************/
  public void markAsUploaded( long uploadedAssetId, URL previewURL )
    {
    mHasBeenUploaded = true;
    mUploadedAssetId = uploadedAssetId;
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
  public long getUploadedAssetId()
    {
    if ( ! mHasBeenUploaded ) throw ( new IllegalStateException( "The id cannot be returned if the asset fragment has not been uploaded" ) );

    return ( mUploadedAssetId );
    }


  /*****************************************************
   *
   * Returns the preview URL following upload.
   *
   *****************************************************/
  public URL getPreviewURL()
    {
    if ( ! mHasBeenUploaded ) throw ( new IllegalStateException( "The preview URL cannot be returned if the asset fragment has not been uploaded" ) );

    return ( mPreviewURL );
    }


  /*****************************************************
   *
   * Returns true if this image spec equals the supplied
   * image spec.
   *
   *****************************************************/
  @Override
  public boolean equals( Object otherObject )
    {
    if ( otherObject == null || ! ( otherObject instanceof UploadableImage ) )
      {
      return ( false );
      }

    UploadableImage otherUploadableImage = (UploadableImage) otherObject;


    if ( otherUploadableImage == this ) return ( true );


    return ( mAssetFragment.equals( otherUploadableImage.mAssetFragment ) &&
             mHasBeenUploaded == otherUploadableImage.mHasBeenUploaded &&
             mUploadedAssetId == otherUploadableImage.mUploadedAssetId &&
             NetUtils.areBothNullOrEqual( mPreviewURL, otherUploadableImage.mPreviewURL ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

