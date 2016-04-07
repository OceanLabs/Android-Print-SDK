/*****************************************************
 *
 * EditableImageContainerFrame.java
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

package ly.kite.widget;


///// Import(s) /////

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.net.URL;
import java.util.ArrayList;

import ly.kite.R;
import ly.kite.util.Asset;
import ly.kite.catalogue.Bleed;
import ly.kite.journey.AKiteActivity;
import ly.kite.image.IImageConsumer;
import ly.kite.image.ImageAgent;


///// Class Declaration /////

/*****************************************************
 *
 * This class is a container for an editable masked image.
 *
 *****************************************************/
public class EditableImageContainerFrame extends FrameLayout implements IImageConsumer
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG           = "EditableImageContain...";

  private static final boolean DEBUGGING_ENABLED = false;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private EditableMaskedImageView  mEditableMaskedImageView;
  private ProgressBar              mProgressSpinner;

  private Asset                    mImageAsset;

  private URL                      mMaskURL;
  private Bleed                    mMaskBleed;

  private ArrayList<URL>           mUnderImageURLList;
  private ArrayList<URL>           mOverImageURLList;

  private Object                   mExpectedImageKey;
  private Object                   mExpectedMaskKey;
  private Object[]                 mExpectedUnderImageKeys;
  private Object[]                 mExpectedOverImageKeys;

  private int                      mExpectedImageCount;

  private ICallback                mCallback;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public EditableImageContainerFrame( Context context )
    {
    super( context );

    initialise( context );
    }

  public EditableImageContainerFrame( Context context, AttributeSet attrs )
    {
    super( context, attrs );

    initialise( context );
    }

  public EditableImageContainerFrame( Context context, AttributeSet attrs, int defStyleAttr )
    {
    super( context, attrs, defStyleAttr );

    initialise( context );
    }

  @TargetApi( Build.VERSION_CODES.LOLLIPOP )
  public EditableImageContainerFrame( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes )
    {
    super( context, attrs, defStyleAttr, defStyleRes );

    initialise( context );
    }


  ////////// IImageConsumer Method(s) //////////

  /*****************************************************
   *
   * Called when the remote image is being downloaded.
   *
   *****************************************************/
  @Override
  public void onImageDownloading( Object key )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "onImageDownloading( key = " + key + " )" );

    if ( mProgressSpinner != null ) mProgressSpinner.setVisibility( View.VISIBLE );
    }


  /*****************************************************
   *
   * Called when the remote image has been loaded.
   *
   *****************************************************/
  @Override
  public void onImageAvailable( Object key, Bitmap bitmap )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "onImageAvailable( key = " + key + ", bitmap = " + bitmap + " )" );

    onLoadResult( key, bitmap );
    }


  /*****************************************************
   *
   * Called when the remote image could not be loaded.
   *
   *****************************************************/
  @Override
  public void onImageUnavailable( Object key, Exception exception )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "onImageUnavailable( key = " + key + ", exception = " + exception + " )" );

    onLoadResult( key, null );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Initialises the view.
   *
   *****************************************************/
  private void initialise( Context context )
    {
    LayoutInflater layoutInflater = LayoutInflater.from( context );

    View view = layoutInflater.inflate( R.layout.editable_image_container_frame, this, true );

    mEditableMaskedImageView = (EditableMaskedImageView)view.findViewById( R.id.editable_image_view );
    mProgressSpinner         = (ProgressBar)view.findViewById( R.id.progress_bar );
    }


  /*****************************************************
   *
   * Sets the callback.
   *
   *****************************************************/
  public void setCallback( ICallback callback )
    {
    mCallback = callback;
    }


  /*****************************************************
   *
   * Clears the image and mask.
   *
   *****************************************************/
  public void unloadAllImages()
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "unloadAllImages()" );

    mEditableMaskedImageView.clearImage();

    mEditableMaskedImageView.clearMask();

    mEditableMaskedImageView.clearUnderOverImages();
    }


  /*****************************************************
   *
   * Sets the image asset.
   *
   *****************************************************/
  public EditableImageContainerFrame setImage( Asset imageAsset )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "setImage( imageAsset = " + imageAsset + " )" );

    mImageAsset = imageAsset;

    return ( this );
    }


  /*****************************************************
   *
   * Removes the current image from memory, sets the new
   * one, and loads it.
   *
   *****************************************************/
  public void setAndLoadImage( Asset imageAsset )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "setAndLoadImage( imageAsset = " + imageAsset + " )" );

    // Unload the current image
    mEditableMaskedImageView.clearImage();

    // Set the new image
    setImage( imageAsset );


    // Load the new image

    if ( imageAsset != null )
      {
      mExpectedImageCount++;

      loadImage();
      }
    }


  /*****************************************************
   *
   * Sets the mask as a URL.
   *
   *****************************************************/
  public EditableImageContainerFrame setMask( URL maskURL, Bleed maskBleed )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "setMask( maskURL = " + maskURL + ", maskBleed = " + maskBleed + " )" );

    mMaskURL   = maskURL;
    mMaskBleed = maskBleed;

    return ( this );
    }


  /*****************************************************
   *
   * Sets the mask as a drawable resource.
   *
   *****************************************************/
  public EditableImageContainerFrame setMask( int resourceId, float aspectRatio )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "setMask( resourceId = " + resourceId + ", aspectRatio = " + aspectRatio + " )" );

    mEditableMaskedImageView.setMask( resourceId, aspectRatio );

    return ( this );
    }


  /*****************************************************
   *
   * Sets the under image URLs.
   *
   *****************************************************/
  public EditableImageContainerFrame setUnderImages( ArrayList<URL> underImageURLList )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "setUnderImages( underImageURLList )" );

    mUnderImageURLList = underImageURLList;

    if ( underImageURLList != null ) mExpectedUnderImageKeys = new Object[ underImageURLList.size() ];

    return ( this );
    }


  /*****************************************************
   *
   * Sets the over image URLs.
   *
   *****************************************************/
  public EditableImageContainerFrame setOverImages( ArrayList<URL> overImageURLList )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "setOverImages( overImageURLList )" );

    mOverImageURLList = overImageURLList;

    if ( overImageURLList != null ) mExpectedOverImageKeys = new Object[ overImageURLList.size() ];

    return ( this );
    }


  /*****************************************************
   *
   * Sets a translucent border minimum size in pixels.
   *
   *****************************************************/
  public EditableImageContainerFrame setTranslucentBorderPixels( int sizeInPixels )
    {
    mEditableMaskedImageView.setTranslucentBorderPixels( sizeInPixels );

    return ( this );
    }


  /*****************************************************
   *
   * Sets a border highlight.
   *
   *****************************************************/
  public EditableImageContainerFrame setBorderHighlight( EditableMaskedImageView.BorderHighlight highlight, int colour, int size )
    {
    mEditableMaskedImageView.setBorderHighlight( highlight, colour, size );

    return ( this );
    }


  /*****************************************************
   *
   * Sets a border highlight.
   *
   *****************************************************/
  public EditableImageContainerFrame setBorderHighlight( EditableMaskedImageView.BorderHighlight highlight, int size )
    {
    mEditableMaskedImageView.setBorderHighlight( highlight, size );

    return ( this );
    }


  /*****************************************************
   *
   * Sets corner images.
   *
   *****************************************************/
  public EditableImageContainerFrame setCornerOverlays( Bitmap topLeftImage, Bitmap topRightImage, Bitmap bottomLeftImage, Bitmap bottomRightImage )
    {
    mEditableMaskedImageView.setCornerOverlays( topLeftImage, topRightImage, bottomLeftImage, bottomRightImage );

    return ( this );
    }


  /*****************************************************
   *
   * Sets corner images.
   *
   *****************************************************/
  public EditableImageContainerFrame setCornerOverlays( int topLeftImageResourceId, int topRightImageResourceId, int bottomLeftImageResourceId, int bottomRightImageResourceId )
    {
    Resources resources = getContext().getResources();

    mEditableMaskedImageView.setCornerOverlays(
            BitmapFactory.decodeResource( resources, topLeftImageResourceId ),
            BitmapFactory.decodeResource( resources, topRightImageResourceId ),
            BitmapFactory.decodeResource( resources, bottomLeftImageResourceId ),
            BitmapFactory.decodeResource( resources, bottomRightImageResourceId ) );

    return ( this );
    }


  /*****************************************************
   *
   * Sets the anchor point for the edge of images.
   *
   *****************************************************/
  public EditableImageContainerFrame setAnchorPoint( float anchorPoint )
    {
    mEditableMaskedImageView.setAnchorPoint( anchorPoint );

    return ( this );
    }


  /*****************************************************
   *
   * Requests that the image be loaded asynchronously.
   *
   *****************************************************/
  private void loadImage()
    {
    mExpectedImageKey = mImageAsset;

    ImageAgent.with( getContext() )
            .load( mImageAsset )
            .reduceColourSpace()
            .resizeForIfSized( mEditableMaskedImageView )
            .onlyScaleDown()
            .into( this, mImageAsset );
    }


  /*****************************************************
   *
   * Requests that all the image be loaded asynchronously.
   *
   *****************************************************/
  public void loadAllImages()
    {
    mExpectedImageCount = 0;


    boolean loadImage;

    if ( loadImage = ( mImageAsset != null && mExpectedImageKey != mImageAsset ) )
      {
      mExpectedImageCount ++;
      }


    boolean loadMask;

    if ( loadMask = ( mMaskURL != null && mExpectedMaskKey != mMaskURL ) )
      {
      mExpectedImageCount ++;
      }


    boolean loadUnderImages;

    if ( loadUnderImages = ( mUnderImageURLList != null && mUnderImageURLList.size() > 0 ) )
      {
      mExpectedImageCount += mUnderImageURLList.size();
      }


    boolean loadOverImages;

    if ( loadOverImages = ( mOverImageURLList != null && mOverImageURLList.size() > 0 ) )
      {
      mExpectedImageCount += mOverImageURLList.size();
      }


    if ( loadImage )
      {
      loadImage();
      }


    if ( loadMask )
      {
      mExpectedMaskKey = mMaskURL;

      ImageAgent.with( getContext() )
              .load( mMaskURL, AKiteActivity.IMAGE_CLASS_STRING_PRODUCT_ITEM )
              .reduceColourSpace()
              .resizeForIfSized( mEditableMaskedImageView )
              .onlyScaleDown()
              .into( this, mMaskURL );
      }


    if ( loadUnderImages )
      {
      for ( int underImageIndex = 0; underImageIndex < mUnderImageURLList.size(); underImageIndex ++ )
        {
        URL underImageURL = mUnderImageURLList.get( underImageIndex );

        if ( underImageURL != null && mExpectedUnderImageKeys[ underImageIndex ] != underImageURL )
          {
          mExpectedUnderImageKeys[ underImageIndex ] = underImageURL;

          ImageAgent.with( getContext() )
                  .load( underImageURL, AKiteActivity.IMAGE_CLASS_STRING_PRODUCT_ITEM )
                  .reduceColourSpace()
                  .resizeForIfSized( mEditableMaskedImageView )
                  .onlyScaleDown()
                  .into( this, underImageURL );
          }
        }
      }


    if ( loadOverImages )
      {
      for ( int overImageIndex = 0; overImageIndex < mUnderImageURLList.size(); overImageIndex ++ )
        {
        URL overImageURL = mOverImageURLList.get( overImageIndex );

        if ( overImageURL != null && mExpectedOverImageKeys[ overImageIndex ] != overImageURL )
          {
          mExpectedOverImageKeys[ overImageIndex ] = overImageURL;

          ImageAgent.with( getContext() )
                  .load( overImageURL, AKiteActivity.IMAGE_CLASS_STRING_PRODUCT_ITEM )
                  .reduceColourSpace()
                  .resizeForIfSized( mEditableMaskedImageView )
                  .onlyScaleDown()
                  .into( this, overImageURL );
          }
        }
      }
    }


  /*****************************************************
   *
   * Called when an image finishes loading either successfully
   * or unsuccessfully.
   *
   *****************************************************/
  private void onLoadResult( Object key, Bitmap bitmap )
    {
    // Check for the image

    if ( key == mExpectedImageKey )
      {
      mExpectedImageCount --;

      mExpectedImageKey = null;

      if ( bitmap != null ) mEditableMaskedImageView.setImageBitmap( bitmap );
      }


    // Check for the mask

    if ( key == mExpectedMaskKey )
      {
      mExpectedImageCount --;

      mExpectedMaskKey = null;

      if ( bitmap != null ) mEditableMaskedImageView.setMask( bitmap, mMaskBleed );
      }


    // Check for an under image

    if ( mExpectedUnderImageKeys != null )
      {
      for ( int underImageIndex = 0; underImageIndex < mExpectedUnderImageKeys.length; underImageIndex ++ )
        {
        if ( key == mExpectedUnderImageKeys[ underImageIndex ] )
          {
          mExpectedImageCount --;

          mExpectedUnderImageKeys[ underImageIndex ] = null;

          if ( bitmap != null ) mEditableMaskedImageView.setUnderImage( underImageIndex, bitmap );
          }
        }
      }


    // Check for an over image

    if ( mExpectedOverImageKeys != null )
      {
      for ( int overImageIndex = 0; overImageIndex < mExpectedOverImageKeys.length; overImageIndex ++ )
        {
        if ( key == mExpectedOverImageKeys[ overImageIndex ] )
          {
          mExpectedImageCount --;

          mExpectedOverImageKeys[ overImageIndex ] = null;

          if ( bitmap != null ) mEditableMaskedImageView.setOverImage( overImageIndex, bitmap );
          }
        }
      }


    // See if everything we were expected has finished loading

    if ( mExpectedImageCount <= 0 && mCallback != null )
      {
      // Hide any progress spinner
      if ( mProgressSpinner != null )
        {
        mProgressSpinner.setVisibility( View.GONE );
        }

      // The result depends on whether the image and mask loaded OK. we check with the
      // container, because it may have been supplied a mask from a resource.

      if ( mEditableMaskedImageView.getImageBitmap()  != null &&
           mEditableMaskedImageView.getMaskDrawable() != null )
        {
        mCallback.onLoadComplete();
        }
      else
        {
        mCallback.onLoadError();
        }
      }
    }


  /*****************************************************
   *
   * Returns the masked image view.
   *
   *****************************************************/
  public EditableMaskedImageView getEditableImageView()
    {
    return ( mEditableMaskedImageView );
    }


  /*****************************************************
   *
   * Saves the state to a bundle. We only save the image
   * scale factor and position.
   *
   *****************************************************/
  public void saveState( Bundle outState )
    {
    if ( mEditableMaskedImageView != null ) mEditableMaskedImageView.saveState( outState );
    }


  /*****************************************************
   *
   * Restores the state to a bundle. We only try to restore
   * the image scale factor and position, and there is
   * no guarantee that they will be used.
   *
   *****************************************************/
  public void restoreState( Bundle inState )
    {
    if ( mEditableMaskedImageView != null ) mEditableMaskedImageView.restoreState( inState );
    }


  /*****************************************************
   *
   * Clears the state.
   *
   *****************************************************/
  public void clearState()
    {
    if ( mEditableMaskedImageView != null ) mEditableMaskedImageView.clearState();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A callback.
   *
   *****************************************************/
  public interface ICallback
    {
    public void onLoadComplete();
    public void onLoadError();
    }

  }

