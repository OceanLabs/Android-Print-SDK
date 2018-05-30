/*****************************************************
 *
 * EditableMaskedImageView.java
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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import ly.kite.KiteSDK;
import ly.kite.animation.ASimpleFloatPropertyAnimator;
import ly.kite.catalogue.Bleed;
import ly.kite.image.ImageAgent;


///// Class Declaration /////

/*****************************************************
 *
 * This class is a view that displays a mask over another
 * image (such as a photo). The underlying photo/image
 * may be panned and scaled.
 *
 *****************************************************/
public class EditableMaskedImageView extends View implements GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String LOG_TAG                               = "EditableMaskedImageView";

  static private final float  MAX_IMAGE_ZOOM                        = 3.0f;

  static private final long   FLY_BACK_ANIMATION_DURATION_MILLIS    = 150L;

  static private final int    OPAQUE_WHITE                          = 0xffffffff;
  static private final int    TRANSLUCENT_ALPHA                     = 50;
  static private final int    DEFAULT_BORDER_HIGHLIGHT_COLOUR       = 0xf0ffffff;

  static private final float  MIN_ANCHOR_POINT                      = 0.0f;
  static private final float  MAX_ANCHOR_POINT                      = 0.9f;

  static private final String BUNDLE_KEY_IMAGE_CENTER_X             = "imageCenterX";
  static private final String BUNDLE_KEY_IMAGE_CENTER_Y             = "imageCenterY";
  static private final String BUNDLE_KEY_IMAGE_SCALE_MULTIPLIER     = "imageScaleMultiplier";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private int                   mViewWidth;
  private int                   mViewHeight;

  private Drawable              mMaskDrawable;
  private int                   mMaskWidth;
  private int                   mMaskHeight;
  private Bleed                 mMaskBleed;

  private Bitmap                mImageBitmap;
  private Rect                  mFullImageSourceRect;

  private Rect                  mMaskToBlendTargetRect;
  private RectF                 mImageToBlendTargetRectF;

  // Each under image can potentially be a different size, so we need a unique source rect
  // for each one ...
  private Bitmap[]              mUnderImageArray;
  private Rect[]                mUnderImageSourceRectArray;

  // ... likewise for over images.
  private Bitmap[]              mOverImageArray;
  private Rect[]                mOverImageSourceRectArray;
  private String                mMaskBlendMode;

  private int                   mTranslucentBorderSizeInPixels;
  private Paint                 mTranslucentPaint;
  private RectF                 mImageToViewTargetRectF;

  private BorderHighlight       mBorderHighlight;
  private int                   mBorderHighlightSizeInPixels;
  private Paint                 mBorderHighlightPaint;

  private Bitmap                mTopLeftOverlayImage;
  private Rect                  mTopLeftSourceRect;
  private RectF                 mTopLeftTargetRectF;

  private Bitmap                mTopRightOverlayImage;
  private Rect                  mTopRightSourceRect;
  private RectF                 mTopRightTargetRectF;

  private Bitmap                mBottomLeftOverlayImage;
  private Rect                  mBottomLeftSourceRect;
  private RectF                 mBottomLeftTargetRectF;

  private Bitmap                mBottomRightOverlayImage;
  private Rect                  mBottomRightSourceRect;
  private RectF                 mBottomRightTargetRectF;

  private float                 mAnchorPoint;

  private Rect                  mFullBlendSourceRect;
  private RectF                 mBlendToViewTargetRectF;

  private float                 mImageMinScaleFactor;
  private float                 mImageScaleFactor;
  private float                 mImageMaxScaleFactor;

  private Paint                 mImageToBlendPaint;
  private Paint                 mDefaultPaint;
  private Paint                 mMultiplyPaint;

  private Bitmap                mBlendBitmap;
  private Canvas                mBlendCanvas;

  private GestureDetector       mGestureDetector;
  private ScaleGestureDetector  mScaleGestureDetector;

  private float                 mRestoredImageScaleMultiplier;
  private float                 mRestoredImageProportionalCenterX;
  private float                 mRestoredImageProportionalCenterY;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public EditableMaskedImageView( Context context )
    {
    super( context );

    initialise( context );
    }

  public EditableMaskedImageView( Context context, AttributeSet attrs )
    {
    super( context, attrs );

    initialise( context );
    }

  public EditableMaskedImageView( Context context, AttributeSet attrs, int defStyleAttr )
    {
    super( context, attrs, defStyleAttr );

    initialise( context );
    }

  @TargetApi( Build.VERSION_CODES.LOLLIPOP )
  public EditableMaskedImageView( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes )
    {
    super( context, attrs, defStyleAttr, defStyleRes );

    initialise( context );
    }


  ////////// View Method(s) //////////

  /*****************************************************
   *
   * Called when the view size changes.
   *
   *****************************************************/
  @Override
  public void onSizeChanged( int width, int height, int oldWidth, int oldHeight )
    {
    super.onSizeChanged( width, height, oldWidth, oldHeight );

    mViewWidth  = width;
    mViewHeight = height;

    calculateSizes();
    }


  /*****************************************************
   *
   * Draws the view.
   *
   *****************************************************/
  @Override
  public void onDraw( Canvas canvas )
    {
    // The mask can be different colours (such as white / red) but we are only
    // interested in its alpha.
    // The rules on colours / alphas are as follows:
    //   - Where the mask is transparent, the background is always visible
    //   - Mask + image    => image colour is displayed
    //   - Mask (no image) => white

    if ( mMaskToBlendTargetRect != null )
      {
      // If there is a translucent border, we need to draw the whole image to the canvas at a reduced
      // alpha first. Later, when we draw the mask-blended image, it should align with the translucent
      // version.

      if ( mTranslucentBorderSizeInPixels  > 0    &&
           mBlendToViewTargetRectF        != null &&
           mImageToBlendTargetRectF       != null )
        {
        mImageToViewTargetRectF.left   = mBlendToViewTargetRectF.left + mImageToBlendTargetRectF.left;
        mImageToViewTargetRectF.top    = mBlendToViewTargetRectF.top  + mImageToBlendTargetRectF.top;
        mImageToViewTargetRectF.right  = mBlendToViewTargetRectF.left + mImageToBlendTargetRectF.right;
        mImageToViewTargetRectF.bottom = mBlendToViewTargetRectF.top  + mImageToBlendTargetRectF.bottom;

        canvas.drawBitmap( mImageBitmap, mFullImageSourceRect, mImageToViewTargetRectF, mTranslucentPaint );
        }


      // We don't need to clear the canvas since we are always drawing the mask in the same place

      // Draw the mask, but apply a colour filter so that it is always white, but using the
      // mask's alpha.
      mMaskDrawable.setColorFilter( OPAQUE_WHITE, PorterDuff.Mode.SRC_ATOP );
      mMaskDrawable.setBounds( mMaskToBlendTargetRect );
      mMaskDrawable.draw( mBlendCanvas );

      // Blend the image with the white mask
      if ( mImageToBlendTargetRectF != null )
        {
        mBlendCanvas.drawBitmap( mImageBitmap, mFullImageSourceRect, mImageToBlendTargetRectF, mImageToBlendPaint );
        }


      // Draw any under images to the view canvas

      if ( mUnderImageArray != null )
        {
        for ( int underImageIndex = 0; underImageIndex < mUnderImageArray.length; underImageIndex ++ )
          {
          Bitmap underImage = mUnderImageArray[ underImageIndex ];

          if ( underImage != null )
            {
            canvas.drawBitmap( underImage, mUnderImageSourceRectArray[ underImageIndex ], mBlendToViewTargetRectF, mDefaultPaint );
            }
          }
        }


      // Draw the blended image to the actual view canvas
      canvas.drawBitmap( mBlendBitmap, mFullBlendSourceRect, mBlendToViewTargetRectF, mDefaultPaint );


      // Draw any over images to the view canvas

      if ( mOverImageArray != null )
        {
        for ( int overImageIndex = 0; overImageIndex < mOverImageArray.length; overImageIndex ++ )
          {
          Bitmap overImage = mOverImageArray[ overImageIndex ];

          if ( overImage != null )
            {
            if ( mMaskBlendMode != null && mMaskBlendMode.equals( "MULTIPLY" ) )
              {
              canvas.drawBitmap(overImage, mOverImageSourceRectArray[overImageIndex], mBlendToViewTargetRectF, mMultiplyPaint);
              }
            else
              {
              canvas.drawBitmap(overImage, mOverImageSourceRectArray[overImageIndex], mBlendToViewTargetRectF, mDefaultPaint);
              }
            }
          }
        }


      // Draw any border highlight

      if ( mBorderHighlight != null )
        {
        switch ( mBorderHighlight )
          {
          case RECTANGLE:
            canvas.drawRect( mBlendToViewTargetRectF, mBorderHighlightPaint );
            break;

          case OVAL:
            canvas.drawOval( mBlendToViewTargetRectF, mBorderHighlightPaint );
            break;
          }
        }


      // Draw any corner images
      if ( mTopLeftTargetRectF     != null ) canvas.drawBitmap( mTopLeftOverlayImage,     mTopLeftSourceRect,     mTopLeftTargetRectF,     mDefaultPaint );
      if ( mTopRightTargetRectF    != null ) canvas.drawBitmap( mTopRightOverlayImage,    mTopRightSourceRect,    mTopRightTargetRectF,    mDefaultPaint );
      if ( mBottomLeftTargetRectF  != null ) canvas.drawBitmap( mBottomLeftOverlayImage,  mBottomLeftSourceRect,  mBottomLeftTargetRectF,  mDefaultPaint );
      if ( mBottomRightTargetRectF != null ) canvas.drawBitmap( mBottomRightOverlayImage, mBottomRightSourceRect, mBottomRightTargetRectF, mDefaultPaint );
      }
    }


  /*****************************************************
   *
   * Called for touch events.
   *
   *****************************************************/
  @Override
  public boolean onTouchEvent( MotionEvent event )
    {
    // Pass the touch events to the gesture detectors
    boolean detector1ConsumedEvent = mGestureDetector.onTouchEvent( event );
    boolean detector2ConsumedEvent = mScaleGestureDetector.onTouchEvent( event );

    // If we got an up event, then we need to make sure that the image is still anchored
    // within the mask.
    if ( event.getActionMasked() == MotionEvent.ACTION_UP &&
         mImageToBlendTargetRectF != null )
      {
      checkAnchoring();
      }

    // If neither of the detectors consumed the event - pass it up to the parent class
    return ( detector1ConsumedEvent || detector2ConsumedEvent | super.onTouchEvent( event ) );
    }


  ////////// GestureDetector.OnGestureListener Method(s) //////////

  /*****************************************************
   *
   * Initialises the view.
   *
   *****************************************************/
  @Override
  public boolean onDown( MotionEvent e )
    {
    // Ignore

    return ( false );
    }

  @Override
  public void onShowPress( MotionEvent e )
    {
    // Ignore
    }

  @Override
  public boolean onSingleTapUp( MotionEvent e )
    {
    // Ignore

    return ( false );
    }

  @Override
  public boolean onScroll( MotionEvent e1, MotionEvent e2, float distanceX, float distanceY )
    {
    // Only do something if we are drawing the image
    if ( mImageToBlendTargetRectF == null ) return ( false );


    mImageToBlendTargetRectF.left   -= distanceX;
    mImageToBlendTargetRectF.right  -= distanceX;

    mImageToBlendTargetRectF.top    -= distanceY;
    mImageToBlendTargetRectF.bottom -= distanceY;


    invalidate();

    return false;
    }

  @Override
  public void onLongPress( MotionEvent e )
    {
    // Ignore
    }

  @Override
  public boolean onFling( MotionEvent e1, MotionEvent e2, float velocityX, float velocityY )
    {
    // Ignore
    return ( false );
    }


  ////////// ScaleGestureDetector.OnScaleGestureListener Method(s) //////////

  /*****************************************************
   *
   * Initialises the view.
   *
   *****************************************************/
  @Override
  public boolean onScale( ScaleGestureDetector detector )
    {
    // Only do something if we are drawing the image
    if ( mImageToBlendTargetRectF == null ) return ( false );


    // Get the focus point for the scale
    float viewFocusX = detector.getFocusX();
    float viewFocusY = detector.getFocusY();

    // Work out the image focus point
    float imageFocusX = ( viewFocusX - mImageToBlendTargetRectF.left ) / mImageScaleFactor;
    float imageFocusY = ( viewFocusY - mImageToBlendTargetRectF.top  ) / mImageScaleFactor;


    setImageScaleFactor( mImageScaleFactor * detector.getScaleFactor() );


    // Work out the new bounds - keeping the image focus point in the same place

    mImageToBlendTargetRectF.left   = ( viewFocusX - ( imageFocusX * mImageScaleFactor ) );
    mImageToBlendTargetRectF.right  = ( viewFocusX + ( ( mImageBitmap.getWidth() - imageFocusX ) * mImageScaleFactor ) );
    mImageToBlendTargetRectF.top    = ( viewFocusY - ( imageFocusY * mImageScaleFactor ) );
    mImageToBlendTargetRectF.bottom = ( viewFocusY + ( ( mImageBitmap.getHeight() - imageFocusY ) * mImageScaleFactor ) );


    invalidate();

    return ( true );
    }

  @Override
  public boolean onScaleBegin( ScaleGestureDetector detector )
    {
    // We need to acknowledge the begin, otherwise we won't get any
    // scale events.
    return ( true );
    }

  @Override
  public void onScaleEnd( ScaleGestureDetector detector )
    {
    // Ignore
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Initialises the view.
   *
   *****************************************************/
  private void initialise( Context context )
    {
    //must be enabled in order to temp. cache canvas bitmap
    this.setDrawingCacheEnabled(true);

    mImageToBlendPaint = new Paint();
    mImageToBlendPaint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.SRC_ATOP ) );
    mImageToBlendPaint.setAntiAlias( true );
    mImageToBlendPaint.setFilterBitmap( true );

    mDefaultPaint = new Paint();
    mMultiplyPaint = new Paint();
    mMultiplyPaint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.MULTIPLY ) );

    mTranslucentPaint = new Paint();
    mTranslucentPaint.setAlpha( TRANSLUCENT_ALPHA );
    mImageToViewTargetRectF = new RectF();

    // Monitor both panning and zooming
    mGestureDetector      = new GestureDetector( context, this );
    mScaleGestureDetector = new ScaleGestureDetector( context, this );
    }


  /*****************************************************
   *
   * Clears the image.
   *
   *****************************************************/
  public void clearImage()
    {
    setImageBitmap( null );
    }


  /*****************************************************
   *
   * Sets the image bitmap.
   *
   *****************************************************/
  public void setImageBitmap( Bitmap bitmap )
    {
    mImageBitmap = bitmap;

    calculateSizes();

    invalidate();
    }


  /*****************************************************
   *
   * Returns the image bitmap.
   *
   *****************************************************/
  public Bitmap getImageBitmap()
    {
    return ( mImageBitmap );
    }


  /*****************************************************
   *
   * Clears the mask.
   *
   *****************************************************/
  public void clearMask()
    {
    setMask( null, 0f, null );
    }


  /*****************************************************
   *
   * Sets the mask.
   *
   *****************************************************/
  public void setMask( Drawable maskDrawable, float aspectRatio, Bleed bleed )
    {
    mMaskDrawable = maskDrawable;


    // If an aspect ratio was supplied - invent a suitable width and height.
    if ( aspectRatio >= KiteSDK.FLOAT_ZERO_THRESHOLD )
      {
      mMaskHeight = 1000;
      mMaskWidth  = (int)( mMaskHeight * aspectRatio );
      }
    else
      {
      if ( maskDrawable != null )
        {
        mMaskWidth  = maskDrawable.getIntrinsicWidth();
        mMaskHeight = maskDrawable.getIntrinsicHeight();
        }
      else
        {
        mMaskWidth  = 0;
        mMaskHeight = 0;
        }
      }

    if ( bleed != null ) mMaskBleed  = bleed;
    else                 mMaskBleed = new Bleed( 0, 0, 0, 0 );


    calculateSizes();

    invalidate();
    }

  /*****************************************************
   *
   * Adds the mask blend mode
   *
   *****************************************************/
    public void setMaskBlendMode( String maskBlendMode )
    {
      mMaskBlendMode = maskBlendMode;

      invalidate();
    }


  /*****************************************************
   *
   * Sets the mask bitmap.
   *
   *****************************************************/
  public void setMask( Bitmap bitmap, Bleed bleed )
    {
    setMask( new BitmapDrawable( bitmap ), 0f, bleed );
    }


  /*****************************************************
   *
   * Sets the mask as a drawable resource id.
   *
   *****************************************************/
  public void setMask( int drawableResourceId, float aspectRatio, Bleed bleed )
    {
    setMask( getResources().getDrawable( drawableResourceId ), aspectRatio, bleed );
    }


  /*****************************************************
   *
   * Sets the mask as a drawable resource id.
   *
   *****************************************************/
  public void setMask( int drawableResourceId, float aspectRatio )
    {
    setMask( getResources().getDrawable( drawableResourceId ), aspectRatio, null );
    }


  /*****************************************************
   *
   * Sets the mask as a drawable resource id, and fixes
   * its aspect ratio
   *
   *****************************************************/
  public void setMask( int drawableResourceId )
    {
    setMask( drawableResourceId, 0f );
    }


  /*****************************************************
   *
   * Returns the mask drawable.
   *
   *****************************************************/
  public Drawable getMaskDrawable()
    {
    return ( mMaskDrawable );
    }


  /*****************************************************
   *
   * Sets an under image.
   *
   *****************************************************/
  public void setUnderImage( int index, Bitmap underImage )
    {
    int minArraySize = index + 1;

    // Create under arrays, if we haven't already done so
    if ( mUnderImageArray == null || mUnderImageSourceRectArray == null )
      {
      mUnderImageArray           = new Bitmap[ minArraySize ];
      mUnderImageSourceRectArray = new Rect[ minArraySize ];
      }


    // Make sure the arrays are big enough

    int currentArraySize = mUnderImageArray.length;

    if ( currentArraySize < minArraySize )
      {
      Bitmap[] newUnderImageArray           = new Bitmap[ minArraySize ];
      Rect[]   newUnderImageSourceRectArray = new Rect[ minArraySize ];

      System.arraycopy( mUnderImageArray,           0, newUnderImageArray,           0, currentArraySize );
      System.arraycopy( mUnderImageSourceRectArray, 0, newUnderImageSourceRectArray, 0, currentArraySize );

      mUnderImageArray           = newUnderImageArray;
      mUnderImageSourceRectArray = newUnderImageSourceRectArray;
      }


    // Put the image at the correct position and create a source rect
    mUnderImageArray          [ index ] = underImage;
    mUnderImageSourceRectArray[ index ] = new Rect( 0, 0, underImage.getWidth(), underImage.getHeight() );


    invalidate();
    }


  /*****************************************************
   *
   * Sets an over image.
   *
   *****************************************************/
  public void setOverImage( int index, Bitmap overImage )
    {
    int minArraySize = index + 1;

    // Create over arrays, if we haven't already done so
    if ( mOverImageArray == null || mOverImageSourceRectArray == null )
      {
      mOverImageArray           = new Bitmap[ minArraySize ];
      mOverImageSourceRectArray = new Rect[ minArraySize ];
      }


    // Make sure the arrays are big enough

    int currentArraySize = mOverImageArray.length;

    if ( currentArraySize < minArraySize )
      {
      Bitmap[] newOverImageArray           = new Bitmap[ minArraySize ];
      Rect[]   newOverImageSourceRectArray = new Rect[ minArraySize ];

      System.arraycopy( mOverImageArray,           0, newOverImageArray,           0, currentArraySize );
      System.arraycopy( mOverImageSourceRectArray, 0, newOverImageSourceRectArray, 0, currentArraySize );

      mOverImageArray           = newOverImageArray;
      mOverImageSourceRectArray = newOverImageSourceRectArray;
      }


    // Put the image at the correct position and create a source rect
    mOverImageArray          [ index ] = overImage;
    mOverImageSourceRectArray[ index ] = new Rect( 0, 0, overImage.getWidth(), overImage.getHeight() );


    invalidate();
    }


  /*****************************************************
   *
   * Clears the under and over images.
   *
   *****************************************************/
  public void clearUnderOverImages()
    {
    mUnderImageArray = null;
    mOverImageArray  = null;

    invalidate();
    }


  /*****************************************************
   *
   * Sets a translucent border minimum size in pixels.
   *
   *****************************************************/
  public void setTranslucentBorderPixels( int sizeInPixels )
    {
    if ( sizeInPixels < 0 ) sizeInPixels = 0;

    mTranslucentBorderSizeInPixels = sizeInPixels;

    calculateSizes();

    invalidate();
    }


  /*****************************************************
   *
   * Sets a border highlight.
   *
   *****************************************************/
  public void setBorderHighlight( BorderHighlight highlight, int colour, int sizeInPixels )
    {
    mBorderHighlight             = highlight;
    mBorderHighlightSizeInPixels = sizeInPixels;

    if ( highlight != null )
      {
      mBorderHighlightPaint = new Paint();
      mBorderHighlightPaint.setColor( colour );
      mBorderHighlightPaint.setAntiAlias( true );
      mBorderHighlightPaint.setStyle( Paint.Style.STROKE );
      mBorderHighlightPaint.setStrokeWidth( sizeInPixels );
      }
    else
      {
      mBorderHighlightPaint        = null;
      mBorderHighlightSizeInPixels = 0;
      }


    prepareCornerRectangles();

    invalidate();
    }

  /*****************************************************
   *
   * Sets a border highlight.
   *
   *****************************************************/
  public void setBorderHighlight( BorderHighlight highlight, int size )
    {
    setBorderHighlight( highlight, DEFAULT_BORDER_HIGHLIGHT_COLOUR, size );
    }


  /*****************************************************
   *
   * Sets corner overlay images.
   *
   *****************************************************/
  public void setCornerOverlays( Bitmap topLeftImage, Bitmap topRightImage, Bitmap bottomLeftImage, Bitmap bottomRightImage )
    {
    mTopLeftOverlayImage     = topLeftImage;
    mTopRightOverlayImage    = topRightImage;
    mBottomLeftOverlayImage  = bottomLeftImage;
    mBottomRightOverlayImage = bottomRightImage;

    prepareCornerRectangles();

    invalidate();
    }


  /*****************************************************
   *
   * Sets the anchor point.
   *
   *****************************************************/
  public void setAnchorPoint( float anchorPoint )
    {
    if      ( anchorPoint < MIN_ANCHOR_POINT ) mAnchorPoint = MIN_ANCHOR_POINT;
    else if ( anchorPoint > MAX_ANCHOR_POINT ) mAnchorPoint = MAX_ANCHOR_POINT;
    else                                       mAnchorPoint = anchorPoint;

    checkAnchoring();
    }


  /*****************************************************
   *
   * Sets up the source / target rectangles for the corner
   * overlays.
   *
   *****************************************************/
  private void prepareCornerRectangles()
    {
    mTopLeftSourceRect      = null;
    mTopLeftTargetRectF     = null;
    mTopRightSourceRect     = null;
    mTopRightTargetRectF    = null;
    mBottomLeftSourceRect   = null;
    mBottomLeftTargetRectF  = null;
    mBottomRightSourceRect  = null;
    mBottomRightTargetRectF = null;


    // We can't create the target rectangles until we know the blend to view target rectangle
    if ( mBlendToViewTargetRectF == null ) return;


    if ( mTopLeftOverlayImage != null )
      {
      int imageWidth  = mTopLeftOverlayImage.getWidth();
      int imageHeight = mTopLeftOverlayImage.getHeight();

      mTopLeftSourceRect  = new Rect( 0, 0, imageWidth, imageHeight );
      mTopLeftTargetRectF = new RectF(
              mBlendToViewTargetRectF.left + mBorderHighlightSizeInPixels,
              mBlendToViewTargetRectF.top  + mBorderHighlightSizeInPixels,
              mBlendToViewTargetRectF.left + mBorderHighlightSizeInPixels + imageWidth,
              mBlendToViewTargetRectF.top  + mBorderHighlightSizeInPixels + imageHeight );
      }


    if ( mTopRightOverlayImage != null )
      {
      int imageWidth  = mTopRightOverlayImage.getWidth();
      int imageHeight = mTopRightOverlayImage.getHeight();

      mTopRightSourceRect  = new Rect( 0, 0, imageWidth, imageHeight );
      mTopRightTargetRectF = new RectF(
              mBlendToViewTargetRectF.right - mBorderHighlightSizeInPixels - imageWidth,
              mBlendToViewTargetRectF.top   + mBorderHighlightSizeInPixels,
              mBlendToViewTargetRectF.right - mBorderHighlightSizeInPixels,
              mBlendToViewTargetRectF.top   + mBorderHighlightSizeInPixels + imageHeight );
      }


    if ( mBottomLeftOverlayImage != null )
      {
      int imageWidth  = mBottomLeftOverlayImage.getWidth();
      int imageHeight = mBottomLeftOverlayImage.getHeight();

      mBottomLeftSourceRect  = new Rect( 0, 0, imageWidth, imageHeight );
      mBottomLeftTargetRectF = new RectF(
              mBlendToViewTargetRectF.left   + mBorderHighlightSizeInPixels,
              mBlendToViewTargetRectF.bottom - mBorderHighlightSizeInPixels - imageHeight,
              mBlendToViewTargetRectF.left   + mBorderHighlightSizeInPixels + imageWidth,
              mBlendToViewTargetRectF.bottom - mBorderHighlightSizeInPixels );
      }


    if ( mBottomRightOverlayImage != null )
      {
      int imageWidth  = mBottomRightOverlayImage.getWidth();
      int imageHeight = mBottomRightOverlayImage.getHeight();

      mBottomRightSourceRect  = new Rect( 0, 0, imageWidth, imageHeight );
      mBottomRightTargetRectF = new RectF(
              mBlendToViewTargetRectF.right  - mBorderHighlightSizeInPixels - imageWidth,
              mBlendToViewTargetRectF.bottom - mBorderHighlightSizeInPixels - imageHeight,
              mBlendToViewTargetRectF.right  - mBorderHighlightSizeInPixels,
              mBlendToViewTargetRectF.bottom - mBorderHighlightSizeInPixels );
      }
    }


  /*****************************************************
   *
   * Sets the image scale factor.
   *
   * Note that we bound the scale factor. This means that
   * we don't have an elastic overstretch effect on zooming
   * like we do with panning. It doesn't look as nice, but
   * it makes the calculation of bounce back for panning a
   * lot easier.
   *
   *****************************************************/
  private void setImageScaleFactor( float candidateScaleFactor )
    {
    if ( candidateScaleFactor >= mImageMinScaleFactor &&
         candidateScaleFactor <= mImageMaxScaleFactor )
      {
      mImageScaleFactor = candidateScaleFactor;
      }
    }


  /*****************************************************
   *
   * Calculates the sizes.
   *
   *****************************************************/
  private void calculateSizes()
    {
    // See if we have enough to calculate the sizes

    if ( mMaskDrawable    == null ||
         mMaskBleed       == null ||
         mViewWidth        < 1    ||
         mViewHeight       < 1    ) return;


    float halfViewWidth  = mViewWidth  * 0.5f;
    float halfViewHeight = mViewHeight * 0.5f;


    // We need to scale the mask so that the mask plus bleed ( = blend canvas size) fits
    // entirely within the view, with some room for any translucent border.
    // The mask will therefore be the same size as, or smaller (if there is a bleed) than, the
    // blend canvas.

    float unscaledMaskWidth           = mMaskWidth;
    float unscaledMaskHeight          = mMaskHeight;

    float unscaledMaskPlusBleedWidth  = mMaskBleed.leftPixels + unscaledMaskWidth  + mMaskBleed.rightPixels;
    float unscaledMaskPlusBleedHeight = mMaskBleed.topPixels  + unscaledMaskHeight + mMaskBleed.bottomPixels;


    // The mask and bleed needs to fit entirely within the view (minus any translucent
    // border), like the centerInside scale type.

    float maskPlusBleedAspectRatio   = unscaledMaskPlusBleedWidth / unscaledMaskPlusBleedHeight;
    float blendAspectRatio           = maskPlusBleedAspectRatio;

    float viewDisplayableWidth       = mViewWidth - mTranslucentBorderSizeInPixels - mTranslucentBorderSizeInPixels;
    float viewDisplayableHeight      = mViewHeight - mTranslucentBorderSizeInPixels - mTranslucentBorderSizeInPixels;

    float viewDisplayableAspectRatio = viewDisplayableWidth / viewDisplayableHeight;


    float maskScaleFactor;

    if ( maskPlusBleedAspectRatio <= viewDisplayableAspectRatio )
      {
      maskScaleFactor = viewDisplayableHeight / unscaledMaskPlusBleedHeight;
      }
    else
      {
      maskScaleFactor = viewDisplayableWidth / unscaledMaskPlusBleedWidth;
      }


    float scaledMaskPlusBleedWidth = unscaledMaskPlusBleedWidth  * maskScaleFactor;
    float scaledMaskWidth          = unscaledMaskWidth  * maskScaleFactor;
    float halfScaledMaskWidth      = scaledMaskWidth * 0.5f;

    float blendWidth               = scaledMaskPlusBleedWidth;
    float halfBlendWidth           = blendWidth  * 0.5f;


    float scaledMaskPlusBleedHeight = unscaledMaskPlusBleedHeight * maskScaleFactor;
    float scaledMaskHeight          = unscaledMaskHeight * maskScaleFactor;
    float halfScaledMaskHeight      = scaledMaskHeight * 0.5f;

    float blendHeight               = scaledMaskPlusBleedHeight;
    float halfBlendHeight           = blendHeight * 0.5f;


    mMaskToBlendTargetRect = new Rect(
            Math.round( halfBlendWidth - halfScaledMaskWidth ),
            Math.round( halfBlendHeight - halfScaledMaskHeight ),
            Math.round( halfBlendWidth + halfScaledMaskWidth ),
            Math.round( halfBlendHeight + halfScaledMaskHeight ) );

    mFullBlendSourceRect    = new Rect( 0, 0, (int)blendWidth, (int)blendHeight );

    mBlendToViewTargetRectF = new RectF( halfViewWidth - halfBlendWidth, halfViewHeight - halfBlendHeight, halfViewWidth + halfBlendWidth, halfViewHeight + halfBlendHeight );


    // We can prepare any corner rectangles once we have the blend to view target rectangle
    prepareCornerRectangles();


    // Create the bitmap-backed canvas for blending the mask and image
    mBlendBitmap = Bitmap.createBitmap( (int)blendWidth, (int)blendHeight, Bitmap.Config.ARGB_8888 );
    mBlendCanvas = new Canvas( mBlendBitmap );


    if ( mImageBitmap == null )
      {
      // Make sure we don't try to draw the image if it's been cleared
      mImageToBlendTargetRectF = null;

      return;
      }


    // We need to calculate the minimum / maximum allowed image sizes, and the default
    // initial image size.

    int unscaledImageWidth  = mImageBitmap.getWidth();
    int unscaledImageHeight = mImageBitmap.getHeight();


    float imageAspectRatio = (float)unscaledImageWidth / (float)unscaledImageHeight;

    float imageDefaultScaleFactor;

    if ( imageAspectRatio <= blendAspectRatio )
      {
      mImageMinScaleFactor    = blendWidth / unscaledImageWidth;
      imageDefaultScaleFactor = (float)( blendWidth + mTranslucentBorderSizeInPixels + mTranslucentBorderSizeInPixels ) / unscaledImageWidth;
      }
    else
      {
      mImageMinScaleFactor    = blendHeight / unscaledImageHeight;
      imageDefaultScaleFactor = (float)( blendHeight + mTranslucentBorderSizeInPixels + mTranslucentBorderSizeInPixels ) / unscaledImageHeight;
      }

    mImageMaxScaleFactor = mImageMinScaleFactor * MAX_IMAGE_ZOOM;


    // See if we want to restore a scale factor. Note that we use a multiplier
    // which is based on the minimum scale factor, so that it will still work
    // when the orientation is changed.

    float restoredImageScaleFactor = mImageMinScaleFactor * mRestoredImageScaleMultiplier;

    if ( restoredImageScaleFactor >= mImageMinScaleFactor &&
         restoredImageScaleFactor <= mImageMaxScaleFactor )
      {
      mImageScaleFactor = restoredImageScaleFactor;
      }
    else
      {
      mImageScaleFactor = imageDefaultScaleFactor;
      }


    float scaledImageWidth      = unscaledImageWidth  * mImageScaleFactor;
    float halfScaledImageWidth  = scaledImageWidth * 0.5f;

    float scaledImageHeight     = unscaledImageHeight * mImageScaleFactor;
    float halfScaledImageHeight = scaledImageHeight * 0.5f;

    mFullImageSourceRect = new Rect( 0, 0, unscaledImageWidth, unscaledImageHeight );


    // See if we want to restore an image position. We only use the x and y just in case
    // we end up applying it to a different image - the aspect ratio won't be wrong.

    RectF restoredImageToBlendTargetRectF = new RectF(
            halfBlendWidth  - (       mRestoredImageProportionalCenterX   * scaledImageWidth  ),
            halfBlendHeight - (       mRestoredImageProportionalCenterY   * scaledImageHeight ),
            halfBlendWidth  + ( ( 1 - mRestoredImageProportionalCenterX ) * scaledImageWidth  ),
            halfBlendHeight + ( ( 1 - mRestoredImageProportionalCenterY ) * scaledImageHeight ) );

    if ( restoredImageToBlendTargetRectF.left   <= 0f &&
         restoredImageToBlendTargetRectF.top    <= 0f &&
         restoredImageToBlendTargetRectF.right  >= ( blendWidth - 1) &&
         restoredImageToBlendTargetRectF.bottom >= ( blendHeight - 1 ) )
      {
      mImageToBlendTargetRectF = restoredImageToBlendTargetRectF;
      }
    else
      {
      mImageToBlendTargetRectF = new RectF( halfBlendWidth - halfScaledImageWidth, halfBlendHeight - halfScaledImageHeight, halfBlendWidth + halfScaledImageWidth, halfBlendHeight + halfScaledImageHeight );
      }


    clearState();
    }


  /*****************************************************
   *
   * Saves the state to a bundle. We only save the image
   * scale factor and position.
   *
   *****************************************************/
  public void saveState( Bundle outState )
    {
    // Save the image size
    if ( mImageMinScaleFactor > 0f )
      {
      // Save the image size relative to its min scale factor
      outState.putFloat( BUNDLE_KEY_IMAGE_SCALE_MULTIPLIER, mImageScaleFactor / mImageMinScaleFactor );
      }

    // Save the image position as the proportional position within the image
    // of the center of the blend rectangle.
    if ( mImageToBlendTargetRectF != null && mBlendBitmap != null )
      {
      outState.putFloat( BUNDLE_KEY_IMAGE_CENTER_X, ( ( mBlendBitmap.getWidth()  * 0.5f ) - mImageToBlendTargetRectF.left ) / ( mImageToBlendTargetRectF.right  - mImageToBlendTargetRectF.left ) );
      outState.putFloat( BUNDLE_KEY_IMAGE_CENTER_Y, ( ( mBlendBitmap.getHeight() * 0.5f ) - mImageToBlendTargetRectF.top  ) / ( mImageToBlendTargetRectF.bottom - mImageToBlendTargetRectF.top ) );
      }

    }


  /*****************************************************
   *
   * Restores the state from the supplied parameters.
   * There is no guarantee that they will be used.
   *
   *****************************************************/
  public void restoreState( float imageProportionalCenterX, float imageProportionalCenterY, float imageScaleMultiplier )
    {
    mRestoredImageProportionalCenterX = imageProportionalCenterX;
    mRestoredImageProportionalCenterY = imageProportionalCenterY;
    mRestoredImageScaleMultiplier     = imageScaleMultiplier;
    }


  /*****************************************************
   *
   * Restores the state from a bundle. We only try to restore
   * the image scale factor and position, and there is
   * no guarantee that they will be used.
   *
   *****************************************************/
  public void restoreState( Bundle inState )
    {
    if ( inState != null )
      {
      restoreState(
              inState.getFloat( BUNDLE_KEY_IMAGE_CENTER_X ),
              inState.getFloat( BUNDLE_KEY_IMAGE_CENTER_Y ),
              inState.getFloat( BUNDLE_KEY_IMAGE_SCALE_MULTIPLIER ) );
      }
    }


  /*****************************************************
   *
   * Restores the state from the supplied parameters.
   * There is no guarantee that they will be used.
   *
   *****************************************************/
  public void restoreState( RectF proportionalCropRectangle )
    {
    float cropRectangleWidth  = proportionalCropRectangle.width();
    float cropRectangleHeight = proportionalCropRectangle.height();

    if ( cropRectangleWidth > 0f && cropRectangleHeight > 0f )
      {
      float imageProportionCenterX    = ( proportionalCropRectangle.left + proportionalCropRectangle.right ) * 0.5f;
      float imageProportionCenterY    = ( proportionalCropRectangle.top + proportionalCropRectangle.bottom ) * 0.5f;

      float horizontalScaleMultiplier = 1.0f / cropRectangleWidth;
      float verticalScaleMultiplier   = 1.0f / cropRectangleHeight;

      float imageScaleMultiplier      = Math.min( horizontalScaleMultiplier, verticalScaleMultiplier );

      restoreState( imageProportionCenterX, imageProportionCenterY, imageScaleMultiplier );
      }
    }


  /*****************************************************
   *
   * Clears any restored state.
   *
   *****************************************************/
  public void clearState()
    {
    mRestoredImageProportionalCenterX = 0f;
    mRestoredImageProportionalCenterY = 0f;
    mRestoredImageScaleMultiplier     = 0f;
    }


  /*****************************************************
   *
   * Checks if the image is anchored, and animates it back
   * to a safe position if not.
   *
   *****************************************************/
  private void checkAnchoring()
    {
    if ( mImageToBlendTargetRectF == null ) return;


    // If we need to shift the image horizontally - start an animation to
    // shift the image left or right.

    float leftBound  = mFullBlendSourceRect.width() * mAnchorPoint;
    float rightBound = mFullBlendSourceRect.width() - leftBound;

    if ( mImageToBlendTargetRectF.left > leftBound )
      {
      new HorizontalImageAnimator( mImageToBlendTargetRectF.left, leftBound, mImageToBlendTargetRectF.width() ).start();
      }
    else if ( mImageToBlendTargetRectF.right < rightBound )
      {
      new HorizontalImageAnimator( mImageToBlendTargetRectF.left, mImageToBlendTargetRectF.left + ( rightBound - mImageToBlendTargetRectF.right ), mImageToBlendTargetRectF.width() ).start();
      }


    // If we need to shift the image horizontally - start an animation to
    // shift the image up or down.

    float topBound    = mFullBlendSourceRect.height() * mAnchorPoint;
    float bottomBound = mFullBlendSourceRect.height() - topBound;

    if ( mImageToBlendTargetRectF.top > topBound )
      {
      new VerticalImageAnimator( mImageToBlendTargetRectF.top, topBound, mImageToBlendTargetRectF.height() ).start();
      }
    else if ( mImageToBlendTargetRectF.bottom < bottomBound )
      {
      new VerticalImageAnimator( mImageToBlendTargetRectF.top, mImageToBlendTargetRectF.top + ( bottomBound - mImageToBlendTargetRectF.bottom ), mImageToBlendTargetRectF.height() ).start();
      }
    }


  /*****************************************************
   *
   * Returns a rectangle containing the crop bounds as a
   * proportion of the image.
   *
   *****************************************************/
  public RectF getImageProportionalCropRectangle()
    {
    // We need to calculate the bounds of the scaled mask plus
    // bleed on the unscaled image.

    // Make sure we have the dimensions we need
    if ( mImageToBlendTargetRectF == null || mFullBlendSourceRect == null ) return ( null );


    // Start by determining the bounds of the mask plus bleed within
    // the scaled image. (We know it's within because the image
    // always fills the blend are entirely, so its bounds must be
    // at or outside the blend area).

    float scaledLeft   = - mImageToBlendTargetRectF.left;
    float scaledTop    = - mImageToBlendTargetRectF.top;
    float scaledRight  = scaledLeft + mFullBlendSourceRect.right;
    float scaledBottom = scaledTop  + mFullBlendSourceRect.bottom;


    // Scale the values up to the actual image size
    float unscaledLeft   = scaledLeft   / mImageScaleFactor;
    float unscaledTop    = scaledTop    / mImageScaleFactor;
    float unscaledRight  = scaledRight  / mImageScaleFactor;
    float unscaledBottom = scaledBottom / mImageScaleFactor;


    // Calculate the bounds as proportions of the actual image size

    float bitmapWidth  = (float)mImageBitmap.getWidth();
    float bitmapHeight = (float)mImageBitmap.getHeight();

    float leftAsProportion   = unscaledLeft   / bitmapWidth;
    float topAsProportion    = unscaledTop    / bitmapHeight;
    float rightAsProportion  = unscaledRight  / bitmapWidth;
    float bottomAsProportion = unscaledBottom / bitmapHeight;


    return ( new RectF( leftAsProportion, topAsProportion, rightAsProportion, bottomAsProportion ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A type of border highlight.
   *
   *****************************************************/
  public enum BorderHighlight
    {
    RECTANGLE,
    OVAL
    }


  /*****************************************************
   *
   * A horizontal animator for the image.
   *
   *****************************************************/
  private class HorizontalImageAnimator extends ASimpleFloatPropertyAnimator
    {
    private float mWidth;


    HorizontalImageAnimator( float xInitial, float xFinal, float width )
      {
      super( FLY_BACK_ANIMATION_DURATION_MILLIS, xInitial, xFinal, new AccelerateDecelerateInterpolator() );

      mWidth = width;
      }

    @Override
    public void onSetValue( float value )
      {
      mImageToBlendTargetRectF.left  = (int)value;
      mImageToBlendTargetRectF.right = mImageToBlendTargetRectF.left + mWidth;

      invalidate();
      }

    }


  /*****************************************************
   *
   * A vertical animator for the image.
   *
   *****************************************************/
  private class VerticalImageAnimator extends ASimpleFloatPropertyAnimator
    {
    private float mHeight;


    VerticalImageAnimator( float yInitial, float yFinal, float height )
      {
      super( FLY_BACK_ANIMATION_DURATION_MILLIS, yInitial, yFinal, new AccelerateDecelerateInterpolator() );

      mHeight = height;
      }

    @Override
    public void onSetValue( float value )
      {
      mImageToBlendTargetRectF.top  = (int)value;
      mImageToBlendTargetRectF.bottom = mImageToBlendTargetRectF.top + mHeight;

      invalidate();
      }

    }

    public Bitmap getPreviewBitmap() {
      return this.getDrawingCache();
    }

  }

