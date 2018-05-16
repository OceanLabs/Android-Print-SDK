/*****************************************************
 *
 * ImageSpec.java
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

package ly.kite.ordering;


///// Import(s) /////

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.List;

import ly.kite.R;
import ly.kite.image.IImageConsumer;
import ly.kite.image.IImageTransformer;
import ly.kite.image.ImageAgent;
import ly.kite.image.ImageViewConsumer;
import ly.kite.util.Asset;
import ly.kite.util.AssetFragment;
import ly.kite.util.AssetHelper;
import ly.kite.util.StringUtils;
import ly.kite.widget.CheckableImageContainerFrame;


///// Class Declaration /////

/*****************************************************
 *
 * This class holds the details of an image for ordering
 * purposes: an asset fragment and a quantity.
 *
 * It replaces the AssetsAndQuantity class.
 *
 *****************************************************/
public class ImageSpec implements Parcelable
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG       = "ImageSpec";


  ////////// Static Variable(s) //////////

  static public final Parcelable.Creator<ImageSpec> CREATOR = new Parcelable.Creator<ImageSpec>()
    {
    public ImageSpec createFromParcel( Parcel in )
      {
      return ( new ImageSpec( in ) );
      }

    public ImageSpec[] newArray( int size )
      {
      return ( new ImageSpec[ size ] );
      }
    };


  ////////// Member Variable(s) //////////

  private AssetFragment  mAssetFragment;
  private String         mBorderText;
  private int            mQuantity;

  private String         mCroppedForProductId;

  private Asset          mThumbnailAsset;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Finds the index of an asset.
   *
   *****************************************************/
  static public int findAsset( List<ImageSpec> searchImageSpecList, Asset soughtAsset )
    {
    int candidateImageIndex = 0;

    for ( ImageSpec candidateImageSpec : searchImageSpecList )
      {
      if ( candidateImageSpec.getAssetFragment().getAsset().equals( soughtAsset ) ) return ( candidateImageIndex );

      candidateImageIndex ++;
      }

    return ( -1 );
    }


  /*****************************************************
   *
   * Returns true if the asset is in the list.
   *
   *****************************************************/
  static public boolean assetIsInList( List<ImageSpec> searchImageSpecList, Asset soughtAsset )
    {
    return ( findAsset( searchImageSpecList, soughtAsset ) >= 0 );
    }


  /*****************************************************
   *
   * Returns true if both the image specs are null, or equal.
   *
   *****************************************************/
  static public boolean areBothNullOrEqual( ImageSpec imageSpec1, ImageSpec imageSpec2 )
    {
    if ( imageSpec1 == null && imageSpec2 == null ) return ( true );
    if ( imageSpec1 == null || imageSpec2 == null ) return ( false );

    return ( imageSpec1.equals( imageSpec2 ) );
    }


  /*****************************************************
   *
   * Returns true if both the image spec lists are null, or equal.
   *
   *****************************************************/
  static public boolean areBothNullOrEqual( List<ImageSpec> list1, List<ImageSpec> list2 )
    {
    if ( list1 == null && list2 == null ) return ( true );
    if ( list1 == null || list2 == null ) return ( false );

    if ( list1.size() != list2.size() ) return ( false );


    int index = 0;

    for ( ImageSpec imageSpec1 : list1 )
      {
      if ( ! ImageSpec.areBothNullOrEqual( imageSpec1, list2.get( index ) ) ) return ( false );

      index ++;
      }


    return ( true );
    }


  ////////// Constructor(s) //////////

  public ImageSpec( AssetFragment assetFragment, String borderText, int quantity )
    {
    mAssetFragment = assetFragment;
    mBorderText    = borderText;
    mQuantity      = quantity;
    }


  public ImageSpec( AssetFragment assetFragment, int quantity )
    {
    this( assetFragment, null, quantity );
    }


  public ImageSpec( AssetFragment assetFragment )
    {
    this( assetFragment, 1 );
    }


  public ImageSpec( Asset asset )
    {
    this( new AssetFragment( asset ) );
    }


  public ImageSpec( Asset asset, RectF proportionalCropRectangle, int quantity )
    {
    this( new AssetFragment( asset, proportionalCropRectangle ), quantity );
    }


  public ImageSpec( Asset asset, RectF proportionalCropRectangle, String borderText, int quantity )
    {
    this( new AssetFragment( asset, proportionalCropRectangle ), borderText, quantity );
    }


  private ImageSpec( AssetFragment assetFragment, String borderText, int quantity, String croppedForProductId )
    {
    this( assetFragment, borderText, quantity );

    mCroppedForProductId = croppedForProductId;
    }


  private ImageSpec( Parcel sourceParcel )
    {
    mAssetFragment       = sourceParcel.readParcelable( AssetFragment.class.getClassLoader() );
    mBorderText          = sourceParcel.readString();
    mQuantity            = sourceParcel.readInt();
    mCroppedForProductId = sourceParcel.readString();
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
    targetParcel.writeString( mBorderText );
    targetParcel.writeInt( mQuantity );
    targetParcel.writeString( mCroppedForProductId );
    }


  ////////// Method(s) //////////

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
    return ( mAssetFragment != null ? mAssetFragment.getAsset() : null );
    }


  /*****************************************************
   *
   * Returns the border text.
   *
   *****************************************************/
  public String getBorderText()
    {
    return ( mBorderText );
    }


  /*****************************************************
   *
   * Returns the quantity.
   *
   *****************************************************/
  public int getQuantity()
    {
    return ( mQuantity );
    }


  /*****************************************************
   *
   * Sets the crop rectangle and the cropped for product
   * id.
   *
   *****************************************************/
  public void setProportionalCropRectangle( RectF proportionalCropRectangle, String croppedForProductId )
    {
    mAssetFragment.setProportionalRectangle( proportionalCropRectangle );

    setCroppedForProductId( croppedForProductId );
    }


  /*****************************************************
   *
   * Sets a new asset fragment.
   *
   *****************************************************/
  public void setImage( AssetFragment assetFragment, String croppedForProductId )
    {
    mAssetFragment = assetFragment;

    setCroppedForProductId( croppedForProductId );
    }


  /*****************************************************
   *
   * Sets the border text.
   *
   *****************************************************/
  public void setBorderText( String borderText )
    {
    mBorderText = borderText;
    }


  /*****************************************************
   *
   * Sets the cropped for product id.
   *
   *****************************************************/
  public void setCroppedForProductId( String croppedForProductId )
    {
    mCroppedForProductId = croppedForProductId;
    }


  /*****************************************************
   *
   * Returns the user journey type that the edited asset
   * was intended for.
   *
   *****************************************************/
  public String getCroppedForProductId()
    {
    return ( mCroppedForProductId );
    }


  /*****************************************************
   *
   * Decrements the quantity by 1 and returns the new value.
   * Will not decrement past 0.
   *
   *****************************************************/
  public int decrementQuantity()
    {
    if ( mQuantity > 0 ) mQuantity --;

    return ( mQuantity );
    }


  /*****************************************************
   *
   * Increments the quantity by 1 and returns the new value.
   *
   *****************************************************/
  public int incrementQuantity()
    {
    return ( ++ mQuantity );
    }


  /*****************************************************
   *
   * Clears a thumbnail image.
   *
   *****************************************************/
  public void clearThumbnail()
    {
    mThumbnailAsset = null;
    }


  /*****************************************************
   *
   * Loads a thumbnail image into an image consumer.
   *
   *****************************************************/
  public void loadThumbnail( Context context, CheckableImageContainerFrame checkableImageContainerFrame , boolean isPosterCollage)
    {
    // If we already have a thumbnail -  load it
    if ( mThumbnailAsset != null )
      {
      checkableImageContainerFrame.clearForNewImage( mThumbnailAsset );

      ImageAgent.with( context )
              .load( mThumbnailAsset )
              .into( checkableImageContainerFrame, mThumbnailAsset );
      }

    // If we have an asset fragment - load it, scaling, and saving the scaled-down
    // image as a thumbnail.
    if ( mAssetFragment != null )
      {
      checkableImageContainerFrame.clearForNewImage( mAssetFragment );

      int dimensId = R.dimen.image_default_thumbnail_size;
      if(!isPosterCollage)
        {
        dimensId = R.dimen.single_image_poster_size;
        }

      ImageAgent.with( context )
              .load( mAssetFragment )
              .setHighPriority( true  )
              .resizeForDimen( checkableImageContainerFrame, dimensId, dimensId )
              .onlyScaleDown()
              .reduceColourSpace()
              .transformAfterResize( new ThumbnailProxy( context ) )
              .into( checkableImageContainerFrame, mAssetFragment );
      }
    }


  /*****************************************************
   *
   * Creates a new image spec from this one, but with a
   * different asset. Used when assets are moved into the
   * basket.
   *
   *****************************************************/
  public ImageSpec createCopyWithReplacedAsset( Asset newAsset )
    {
    AssetFragment newAssetFragment = new AssetFragment( newAsset, mAssetFragment.getProportionalRectangle() );

    return ( new ImageSpec( newAssetFragment, mBorderText, mQuantity, mCroppedForProductId ) );
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
    if ( otherObject == null || ! ( otherObject instanceof ImageSpec ) )
      {
      return ( false );
      }

    ImageSpec otherImageSpec = (ImageSpec)otherObject;


    if ( otherImageSpec == this ) return ( true );


    return ( mAssetFragment.equals( otherImageSpec.mAssetFragment ) &&
             mQuantity == otherImageSpec.mQuantity &&
             StringUtils.areBothNullOrEqual( mCroppedForProductId, otherImageSpec.mCroppedForProductId ) );
    }

  /*****************************************************
   *
   * Sets the preview image
   *
   *****************************************************/
  public void setPreviewImage(Bitmap bitmap)
    {
      if(bitmap != null) {
        mAssetFragment.setAssetPreviewBitmap(bitmap);
      }
    }

  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * An image consumer proxy that is used for creating
   * thumbnail images.
   *
   *****************************************************/
  private class ThumbnailProxy implements IImageTransformer
    {
    private Context  mContext;


    ThumbnailProxy( Context context )
      {
      mContext = context;
      }


    @Override
    public Bitmap getTransformedBitmap( Bitmap bitmap )
      {
      // Create a session asset from the bitmap
      mThumbnailAsset = AssetHelper.createAsSessionAsset( mContext, bitmap );

      // We haven't transformed the bitmap, so return the original one back.
      return ( bitmap );
      }
    }

  }

