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
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.net.URL;

import ly.kite.R;
import ly.kite.product.Asset;
import ly.kite.product.AssetHelper;
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
abstract public class AImageContainerFrame extends FrameLayout implements IImageConsumer
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG              = "AImageContainerFrame";

  public  static final float   DEFAULT_ASPECT_RATIO = 1.389f;

  private static final Object  ANY_KEY              = new Object();


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private   int           mWidth;
  private   int           mHeight;

  protected ImageView     mImageView;

  private   float         mWidthToHeightMultiplier;

  private   String        mRequestImageClass;
  private   Object        mRequestImageSource;

  private   Object        mExpectedKey;


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
    // If an aspect ratio was set - set the image view dimensions

    if ( mWidthToHeightMultiplier > 0.001f )
      {
      int widthMode = MeasureSpec.getMode( widthMeasureSpec );
      int widthSize = MeasureSpec.getSize( widthMeasureSpec );

      if ( widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY )
        {
        ViewGroup.LayoutParams imageLayoutParams = mImageView.getLayoutParams();

        imageLayoutParams.width  = widthSize;
        imageLayoutParams.height = (int)( widthSize * mWidthToHeightMultiplier );

        mImageView.setLayoutParams( imageLayoutParams );
        }
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

    mWidth   = width;
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
    // Ignore
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
      mImageView.setImageBitmap( bitmap );
      }
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

    // Get the image view
    mImageView = (ImageView)view.findViewById( R.id.image_view );


    // Check the XML attributes

    if ( attributeSet != null )
      {
      TypedArray typedArray = context.obtainStyledAttributes( attributeSet, R.styleable.FixableImageFrame, defaultStyle, defaultStyle );


      // If an aspect ratio was defined in the XML then set it now.
      // ** Otherwise leave it at its uninitialised value **

      TypedValue value = new TypedValue();

      if ( typedArray.getValue( R.styleable.FixableImageFrame_aspectRatio, value ) )
        {
        setAspectRatio( value.getFloat() );
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
  public void setAspectRatio( float aspectRatio )
    {
    mWidthToHeightMultiplier = 1.0f / aspectRatio;
    }


  /*****************************************************
   *
   * Sets the image bitmap.
   *
   *****************************************************/
  public void setImageBitmap( Bitmap bitmap )
    {
    mImageView.setImageBitmap( bitmap );
    }


  /*****************************************************
   *
   * Sets the source of the image to be scaled to the
   * correct size.
   *
   *****************************************************/
  public void requestScaledImageOnceSized( String imageClass, Object imageSource )
    {
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

    mImageView.setImageBitmap( null );
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


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

