/*****************************************************
 *
 * AImageContainerFrame.java
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
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
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.net.URL;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.catalogue.Asset;
import ly.kite.catalogue.AssetHelper;
import ly.kite.util.IImageConsumer;
import ly.kite.util.ImageAgent;


///// Class Declaration /////

/*****************************************************
 *
 * This widget is a frame layout that contains an image view.
 * The size of the image is set according to the aspect ratio
 * and the width of the frame.
 *
 * The widget is also an image consumer.
 *
 *****************************************************/
abstract public class AImageContainerFrame extends FrameLayout implements IImageConsumer, Animation.AnimationListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private   static final String  LOG_TAG                           = "AImageContainerFrame";

  public    static final float   DEFAULT_ASPECT_RATIO              = 1.389f;

  private   static final Object  ANY_KEY                           = new Object();

  private   static final long    FADE_IN_ANIMATION_DURATION_MILLIS = 300L;
  private   static final float   ALPHA_TRANSPARENT                 = 0.0f;
  private   static final float   ALPHA_OPAQUE                      = 1.0f;



  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private   int           mWidth;
  private   int           mHeight;

  protected ImageView     mImageView;
  protected ProgressBar   mProgressSpinner;

  private   float         mWidthToHeightMultiplier;

  private   float         mLeftPaddingProportion;
  private   float         mTopPaddingProportion;
  private   float         mRightPaddingProportion;
  private   float         mBottomPaddingProportion;

  private   boolean       mShowProgressSpinnerOnDownload;

  private   String        mRequestImageClass;
  private   Object        mRequestImageSource;

  private   Object        mExpectedKey;

  protected Animation     mFadeInAnimation;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public AImageContainerFrame( Context context )
    {
    super( context );

    initialise( context, null, 0 );
    }

  public AImageContainerFrame( Context context, AttributeSet attrs )
    {
    super( context, attrs );

    initialise( context, attrs, 0 );
    }

  public AImageContainerFrame( Context context, AttributeSet attrs, int defStyleAttr )
    {
    super( context, attrs, defStyleAttr );

    initialise( context, attrs, defStyleAttr );
    }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public AImageContainerFrame( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes )
    {
    super( context, attrs, defStyleAttr, defStyleRes );

    initialise( context, attrs, defStyleAttr );
    }


  ////////// View Method(s) //////////

  /*****************************************************
   *
   * Called to measure the view.
   *
   *****************************************************/
  @Override
  protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
    {
    int widthMode = MeasureSpec.getMode( widthMeasureSpec );
    int widthSize = MeasureSpec.getSize( widthMeasureSpec );


    // We only do this jiggery-pokery if the width is known and we were supplied
    // an aspect ratio.

    if ( ( widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY ) &&
         mWidthToHeightMultiplier >= KiteSDK.FLOAT_ZERO_THRESHOLD )
      {
      float imageWidth;
      float imageHeight;

      // If padding proportions have been supplied, our calculations are based on the following equations:
      //
      // Available width =   left padding                            + image width +   right padding
      //                 = ( image width * left padding proportion ) + image width + ( image width * right padding proportion )
      //                 = image width * ( left padding proportion + 1 + right padding proportion )
      //
      // Therefore: image width = available width / ( left padding proportion + 1 + right padding proportion )
      //
      if ( mLeftPaddingProportion   >= KiteSDK.FLOAT_ZERO_THRESHOLD ||
           mTopPaddingProportion    >= KiteSDK.FLOAT_ZERO_THRESHOLD ||
           mRightPaddingProportion  >= KiteSDK.FLOAT_ZERO_THRESHOLD ||
           mBottomPaddingProportion >= KiteSDK.FLOAT_ZERO_THRESHOLD )
        {
        imageWidth = widthSize / ( mLeftPaddingProportion + 1f + mRightPaddingProportion );

        // Now that we have the width, we can calculate the height using the aspect ratio
        imageHeight = imageWidth * mWidthToHeightMultiplier;


        // Finally, calculate and set the padding.

        float leftPadding   = imageWidth * mLeftPaddingProportion;
        float rightPadding  = imageWidth * mRightPaddingProportion;
        float topPadding    = imageHeight * mTopPaddingProportion;
        float bottomPadding = imageHeight * mBottomPaddingProportion;

        setPadding( (int)leftPadding, (int)topPadding, (int)rightPadding, (int)bottomPadding );
        }
      else
        {
        // If no padding proportions have been set, then leave the padding as it is, but still calculate
        // the image dimensions.

        imageWidth  = widthSize - ( getPaddingLeft() + getPaddingRight() );
        imageHeight = imageWidth * mWidthToHeightMultiplier;
        }


      // Set the width and height of the image

      ViewGroup.LayoutParams imageLayoutParams = mImageView.getLayoutParams();

      imageLayoutParams.width  = (int)imageWidth;
      imageLayoutParams.height = (int)imageHeight;

      mImageView.setLayoutParams( imageLayoutParams );
      }


    super.onMeasure( widthMeasureSpec, heightMeasureSpec );
    }


  /*****************************************************
   *
   * Called with the image size.
   *
   *****************************************************/
  @Override
  public void onSizeChanged( int width, int height, int previousWidth, int previousHeight )
    {
    super.onSizeChanged( width, height, previousWidth, previousHeight );

    mWidth  = width;
    mHeight = height;

    checkRequestImage();
    }


  ////////// IImageConsumer Method(s) //////////

  /*****************************************************
   *
   * Called when the image is downloading.
   *
   *****************************************************/
  @Override
  public void onImageDownloading( Object key )
    {
    if ( mShowProgressSpinnerOnDownload ) showProgressSpinner();
    }


  /*****************************************************
   *
   * Called when the image is available.
   *
   *****************************************************/
  @Override
  public void onImageAvailable( Object key, Bitmap bitmap )
    {
    if ( keyIsOK( key ) )
      {
      // Make sure we don't do anything if we get the bitmap delivered
      // more than once.
      mExpectedKey = null;

      setImageBitmap( bitmap );

      fadeImageIn();
      }
    }


  ////////// Animation.AnimationListener Method(s) //////////

  /*****************************************************
   *
   * Called when an animation starts.
   *
   *****************************************************/
  @Override
  public void onAnimationStart( Animation animation )
    {
    // Ignore
    }


  /*****************************************************
   *
   * Called when an animation completes.
   *
   *****************************************************/
  @Override
  public void onAnimationEnd( Animation animation )
    {
    // Clear the animation

    if ( animation == mFadeInAnimation )
      {
      mFadeInAnimation = null;

      mImageView.setAnimation( null );
      }
    }


  /*****************************************************
   *
   * Called when an animation repeats.
   *
   *****************************************************/
  @Override
  public void onAnimationRepeat( Animation animation )
    {
    // Ignore
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Initialises this widget.
   *
   *****************************************************/
  protected void initialise( Context context, AttributeSet attributeSet, int defaultStyle )
    {
    // Get the view
    View view = onCreateView( context, attributeSet, defaultStyle );

    // Get the views
    mImageView       = (ImageView)view.findViewById( R.id.image_view );
    mProgressSpinner = (ProgressBar)view.findViewById( R.id.progress_spinner );


    // Check the XML attributes

    if ( attributeSet != null )
      {
      TypedArray typedArray = context.obtainStyledAttributes( attributeSet, R.styleable.FixableImageFrame, defaultStyle, defaultStyle );


      // If an aspect ratio was defined in the XML then set it now.
      // ** Otherwise leave it at its uninitialised value **

      TypedValue value = new TypedValue();

      if ( typedArray.getValue( R.styleable.FixableImageFrame_aspectRatio, value ) )
        {
        setImageAspectRatio( value.getFloat() );
        }


      typedArray.recycle();
      }

    }


  /*****************************************************
   *
   * Returns the view for this frame. The view should be
   * attached to this frame when this method returns.
   *
   *****************************************************/
  abstract protected View onCreateView( Context context, AttributeSet attributeSet, int defaultStyle );


  /*****************************************************
   *
   * Sets the aspect ratio for images.
   *
   *****************************************************/
  public void setImageAspectRatio( float aspectRatio )
    {
    mWidthToHeightMultiplier = 1.0f / aspectRatio;
    }


  /*****************************************************
   *
   * Sets a frame border around the image as a proportion
   * of the image size (which we won't know until we are
   * measured), by setting the padding.
   *
   *****************************************************/
  public void setPaddingProportions( float leftProportion, float topProportion, float rightProportion, float bottomProportion )
    {
    mLeftPaddingProportion   = leftProportion;
    mTopPaddingProportion    = topProportion;
    mRightPaddingProportion  = rightProportion;
    mBottomPaddingProportion = bottomProportion;
    }


  /*****************************************************
   *
   * Sets the image bitmap.
   *
   *****************************************************/
  public void setImageBitmap( Bitmap bitmap )
    {
    mImageView.setVisibility( View.VISIBLE );
    mImageView.setImageBitmap( bitmap );

    // Automatically clear any progress spinner
    hideProgressSpinner();
    }


  /*****************************************************
   *
   * Shows any progress spinner.
   *
   *****************************************************/
  public void showProgressSpinner()
    {
    if ( mProgressSpinner != null ) mProgressSpinner.setVisibility( View.VISIBLE );
    }


  /*****************************************************
   *
   * Hides any progress spinner.
   *
   *****************************************************/
  public void hideProgressSpinner()
    {
    if ( mProgressSpinner != null ) mProgressSpinner.setVisibility( View.GONE );
    }


  /*****************************************************
   *
   * Sets whether the progress spinner is shown when an
   * image is being downloaded.
   *
   *****************************************************/
  public void setShowProgressSpinnerOnDownload( boolean showProgressSpinnerOnDownload )
    {
    mShowProgressSpinnerOnDownload = showProgressSpinnerOnDownload;
    }


  /*****************************************************
   *
   * Sets the source of the image to be scaled to the
   * correct size.
   *
   *****************************************************/
  public void requestScaledImageOnceSized( String imageClass, Object imageSource )
    {
    // Don't do anything if we're already waiting for the image
    if ( imageClass.equals( mRequestImageClass ) && imageSource.equals( mRequestImageSource ) ) return;


    clear();

    mRequestImageClass  = imageClass;
    mRequestImageSource = imageSource;

    checkRequestImage();
    }


  /*****************************************************
   *
   * Sets the source of the image as an asset to be scaled
   * to the correct size.
   *
   *****************************************************/
  public void requestScaledImageOnceSized( Asset asset )
    {
    requestScaledImageOnceSized( AssetHelper.IMAGE_CLASS_STRING_ASSET, asset );
    }


  /*****************************************************
   *
   * Checks if we have everything we need to request
   * the image.
   *
   *****************************************************/
  private void checkRequestImage()
    {
    if ( mWidth > 0 && mHeight > 0 && mRequestImageSource != null )
      {
      if ( mRequestImageSource instanceof Asset )
        {
        setExpectedKey( mRequestImageSource );

        AssetHelper.requestImage( getContext(), (Asset)mRequestImageSource, null, mWidth, this );
        }
      else if ( mRequestImageSource instanceof URL )
        {
        setExpectedKey( mRequestImageSource );

        ImageAgent.getInstance( getContext() ).requestImage( mRequestImageClass, mRequestImageSource, (URL)mRequestImageSource, null, mWidth, this );
        }
      }
    }


  /*****************************************************
   *
   * Clears the image and sets the key for the next
   * expected image.
   *
   *****************************************************/
  public void clearForNewImage( Object expectedKey )
    {
    setExpectedKey( expectedKey );

    setImageBitmap( null );
    }


  /*****************************************************
   *
   * Clears the image and sets a wildcard key for the next
   * expected image.
   *
   *****************************************************/
  public void clearForAnyImage()
    {
    setExpectedKey( ANY_KEY );

    mImageView.setImageBitmap( null );
    }


  /*****************************************************
   *
   * Clears the image and key.
   *
   *****************************************************/
  public void clear()
    {
    clearForNewImage( null );
    }


  /*****************************************************
   *
   * Sets the key to expect.
   *
   *****************************************************/
  public void setExpectedKey( Object key )
    {
    mExpectedKey = key;
    }


  /*****************************************************
   *
   * Returns true if the supplied key is OK.
   *
   *****************************************************/
  protected boolean keyIsOK( Object key )
    {
    return ( mExpectedKey == ANY_KEY || key.equals( mExpectedKey ) );
    }


  /*****************************************************
   *
   * Starts a fade-in animation on the image.
   *
   *****************************************************/
  private void fadeImageIn()
    {
    mFadeInAnimation = new AlphaAnimation( ALPHA_TRANSPARENT, ALPHA_OPAQUE );
    mFadeInAnimation.setDuration( FADE_IN_ANIMATION_DURATION_MILLIS );
    mFadeInAnimation.setFillAfter( true );
    mFadeInAnimation.setAnimationListener( this );

    mImageView.startAnimation( mFadeInAnimation );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }
