/*****************************************************
 *
 * MaskedImageView.java
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
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import ly.kite.animation.ASimpleFloatPropertyAnimator;
import ly.kite.print.Bleed;


///// Class Declaration /////

/*****************************************************
 *
 * This class is a view that displays a mask over another
 * image (such as a photo). The underlying photo/image
 * may be moved and zoomed.
 *
 *****************************************************/
public class MaskedImageView extends View implements GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                          = "MaskedImageView";

  private static final float FLOAT_ZERO_THRESHOLD               = 0.0001f;

  private static final float MAX_IMAGE_ZOOM                     = 3.0f;

  private static final long  FLY_BACK_ANIMATION_DURATION_MILLIS = 150L;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private int                   mViewWidth;
  private int                   mViewHeight;
  private float                 mViewAspectRatio;

  private Bitmap                mMaskBitmap;
  private Bleed                 mMaskBleed;
  private Rect                  mMaskToBlendSourceRect;

  private Bitmap                mImageBitmap;
  private Rect                  mImageToBlendSourceRect;

  private RectF                 mMaskToBlendTargetRect;
  private RectF                 mImageToBlendTargetRect;

  private Rect                  mBlendToViewSourceRect;
  private RectF                 mBlendToViewTargetRect;

  private float                 mImageMinScaleFactor;
  private float                 mImageScaleFactor;
  private float                 mImageMaxScaleFactor;

  private Bitmap                mBlendBitmap;
  private Canvas                mBlendCanvas;
  private Paint                 mBlendPaint;

  private GestureDetector       mGestureDetector;
  private ScaleGestureDetector  mScaleGestureDetector;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public MaskedImageView( Context context )
    {
    super( context );

    initialise( context );
    }

  public MaskedImageView( Context context, AttributeSet attrs )
    {
    super( context, attrs );

    initialise( context );
    }

  public MaskedImageView( Context context, AttributeSet attrs, int defStyleAttr )
    {
    super( context, attrs, defStyleAttr );

    initialise( context );
    }

  @TargetApi( Build.VERSION_CODES.LOLLIPOP )
  public MaskedImageView( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes )
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

    mViewWidth       = width;
    mViewHeight      = height;

    mViewAspectRatio = (float)width / (float)height;

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
    // If we only have the mask then just draw it as normal. If we have
    // the image as well then combine it with the mask.

    if ( mMaskToBlendTargetRect != null )
      {
      mBlendCanvas.drawColor( 0x00000000 );
      mBlendCanvas.drawBitmap( mMaskBitmap, mMaskToBlendSourceRect, mMaskToBlendTargetRect, null );

      if ( mImageToBlendTargetRect != null )
        {
        mBlendCanvas.drawBitmap( mImageBitmap, mImageToBlendSourceRect, mImageToBlendTargetRect, mBlendPaint );
        }

      canvas.drawBitmap( mBlendBitmap, mBlendToViewSourceRect, mBlendToViewTargetRect, null );
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

    // If we got an up event, then we need to make sure that the mask is still filled by
    // the image.
    if ( event.getActionMasked() == MotionEvent.ACTION_UP &&
         mImageToBlendTargetRect != null )
      {
      // If we need to shift the image horizontally - start an animation to
      // shift the image left or right.

      if ( mImageToBlendTargetRect.left > 0f )
        {
        new HorizontalImageAnimator( mImageToBlendTargetRect.left, 0, mImageToBlendTargetRect.width() ).start();
        }
      else if ( mImageToBlendTargetRect.right < mBlendToViewSourceRect.right )
        {
        new HorizontalImageAnimator( mImageToBlendTargetRect.left, mImageToBlendTargetRect.left + ( mBlendToViewSourceRect.right - mImageToBlendTargetRect.right ), mImageToBlendTargetRect.width() ).start();
        }


      // If we need to shift the image horizontally - start an animation to
      // shift the image up or down.

      if ( mImageToBlendTargetRect.top > 0f )
        {
        new VerticalImageAnimator( mImageToBlendTargetRect.top, 0, mImageToBlendTargetRect.height() ).start();
        }
      else if ( mImageToBlendTargetRect.bottom < mBlendToViewSourceRect.bottom )
        {
        new VerticalImageAnimator( mImageToBlendTargetRect.top, mImageToBlendTargetRect.top + ( mBlendToViewSourceRect.bottom - mImageToBlendTargetRect.bottom ), mImageToBlendTargetRect.height() ).start();
        }

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
    if ( mImageToBlendTargetRect == null ) return ( false );


    mImageToBlendTargetRect.left   -= distanceX;
    mImageToBlendTargetRect.right  -= distanceX;

    mImageToBlendTargetRect.top    -= distanceY;
    mImageToBlendTargetRect.bottom -= distanceY;


    invalidate();

    return false;
    }

  @Override
  public void onLongPress( MotionEvent e )
    {

    }

  @Override
  public boolean onFling( MotionEvent e1, MotionEvent e2, float velocityX, float velocityY )
    {
    return false;
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
    if ( mImageToBlendTargetRect == null ) return ( false );


    // Get the focus point for the scale
    float viewFocusX = detector.getFocusX();
    float viewFocusY = detector.getFocusY();

    // Work out the image focus point
    float imageFocusX = ( viewFocusX - mImageToBlendTargetRect.left ) / mImageScaleFactor;
    float imageFocusY = ( viewFocusY - mImageToBlendTargetRect.top  ) / mImageScaleFactor;


    setImageScaleFactor( mImageScaleFactor * detector.getScaleFactor() );


    // Work out the new bounds - keeping the image focus point in the same place

    mImageToBlendTargetRect.left   = ( viewFocusX - ( imageFocusX * mImageScaleFactor ) );
    mImageToBlendTargetRect.right  = ( viewFocusX + ( ( mImageBitmap.getWidth() - imageFocusX ) * mImageScaleFactor ) );
    mImageToBlendTargetRect.top    = ( viewFocusY - ( imageFocusY * mImageScaleFactor ) );
    mImageToBlendTargetRect.bottom = ( viewFocusY + ( ( mImageBitmap.getHeight() - imageFocusY ) * mImageScaleFactor ) );


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
    mBlendPaint = new Paint();
    mBlendPaint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.SRC_IN ) );

    // Monitor both panning and zooming
    mGestureDetector      = new GestureDetector( context, this );
    mScaleGestureDetector = new ScaleGestureDetector( context, this );
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
   * Sets the mask bitmap.
   *
   *****************************************************/
  public void setMask( Bitmap bitmap, Bleed bleed )
    {
    mMaskBitmap = bitmap;
    mMaskBleed  = bleed;

    calculateSizes();

    invalidate();
    }


  /*****************************************************
   *
   * Returns the mask bitmap.
   *
   *****************************************************/
  public Bitmap getMaskBitmap()
    {
    return ( mMaskBitmap );
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

    if ( mMaskBitmap      == null ||
         mMaskBleed       == null ||
         mViewAspectRatio  < FLOAT_ZERO_THRESHOLD ) return;


    float halfViewWidth  = mViewWidth  * 0.5f;
    float halfViewHeight = mViewHeight * 0.5f;


    // We need to scale the mask so that the mask plus bleed ( = blend canvas size) fits
    // entirely within the view.
    // The mask will therefore be the same size as, or smaller (if there is a bleed) than, the
    // blend canvas.

    int unscaledMaskWidth           = mMaskBitmap.getWidth();
    int unscaledMaskHeight          = mMaskBitmap.getHeight();

    int unscaledMaskPlusBleedWidth  = mMaskBleed.leftPixels + unscaledMaskWidth  + mMaskBleed.rightPixels;
    int unscaledMaskPlusBleedHeight = mMaskBleed.topPixels  + unscaledMaskHeight + mMaskBleed.bottomPixels;


    // The mask and bleed needs to fit entirely within the view, like the centerInside scale type.

    float maskPlusBleedAspectRatio = (float)unscaledMaskPlusBleedWidth / (float)unscaledMaskPlusBleedHeight;
    float blendAspectRatio         = maskPlusBleedAspectRatio;

    float maskScaleFactor;

    if ( maskPlusBleedAspectRatio <= mViewAspectRatio )
      {
      maskScaleFactor = (float)mViewHeight / (float)unscaledMaskPlusBleedHeight;
      }
    else
      {
      maskScaleFactor = (float)mViewWidth / (float)unscaledMaskPlusBleedWidth;
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


    mMaskToBlendSourceRect = new Rect( 0, 0, unscaledMaskWidth, unscaledMaskHeight );
    mMaskToBlendTargetRect = new RectF( halfBlendWidth - halfScaledMaskWidth, halfBlendHeight - halfScaledMaskHeight, halfBlendWidth + halfScaledMaskWidth, halfBlendHeight + halfScaledMaskHeight );


    mBlendToViewSourceRect = new Rect( 0, 0, (int)blendWidth, (int)blendHeight );
    mBlendToViewTargetRect = new RectF( halfViewWidth - halfBlendWidth, halfViewHeight - halfBlendHeight, halfViewWidth + halfBlendWidth, halfViewHeight + halfBlendHeight );


    // Create the bitmap-backed canvas for blending the mask and image
    mBlendBitmap = Bitmap.createBitmap( (int)blendWidth, (int)blendHeight, Bitmap.Config.ARGB_8888 );
    mBlendCanvas = new Canvas( mBlendBitmap );


    if ( mImageBitmap == null ) return;


    // The image needs to fill the mask, like the centerCropped scale type

    int unscaledImageWidth  = mImageBitmap.getWidth();
    int unscaledImageHeight = mImageBitmap.getHeight();


    float imageAspectRatio = (float)unscaledImageWidth / (float)unscaledImageHeight;

    if ( imageAspectRatio <= blendAspectRatio )
      {
      mImageScaleFactor = blendWidth / (float)unscaledImageWidth;
      }
    else
      {
      mImageScaleFactor = blendHeight / (float)unscaledImageHeight;
      }

    float scaledImageWidth      = unscaledImageWidth  * mImageScaleFactor;
    float halfScaledImageWidth  = scaledImageWidth * 0.5f;

    float scaledImageHeight     = unscaledImageHeight * mImageScaleFactor;
    float halfScaledImageHeight = scaledImageHeight * 0.5f;

    mImageToBlendSourceRect = new Rect( 0, 0, unscaledImageWidth, unscaledImageHeight );
    mImageToBlendTargetRect = new RectF( halfBlendWidth - halfScaledImageWidth, halfBlendHeight - halfScaledImageHeight, halfBlendWidth + halfScaledImageWidth, halfBlendHeight + halfScaledImageHeight );


    mImageMinScaleFactor = mImageScaleFactor;
    mImageMaxScaleFactor = mImageMinScaleFactor * MAX_IMAGE_ZOOM;
    }


  /*****************************************************
   *
   * Returns true if we have both bitmaps, false otherwise.
   *
   *****************************************************/
  public boolean bothBitmapsAvailable()
    {
    return ( mImageBitmap != null && mMaskBitmap != null );
    }


  /*****************************************************
   *
   * Returns a copy of the image, which has been cropped
   * to the mask.
   *
   *****************************************************/
  public Bitmap getImageCroppedToMask()
    {
    // We need to calculate the bounds of the scaled mask plus
    // bleed on the unscaled image.

    // Start by determining the bounds of the mask plus bleed within
    // the scaled image. (We know it's within because the image
    // always fills the blend are entirely, so its bounds must be
    // at or outside the blend area).

    float scaledLeft   = - mImageToBlendTargetRect.left;
    float scaledTop    = - mImageToBlendTargetRect.top;
    float scaledRight  = scaledLeft + mBlendToViewSourceRect.right;
    float scaledBottom = scaledTop  + mBlendToViewSourceRect.bottom;


    // Scale the values up to the actual image size
    float unscaledLeft   = scaledLeft / mImageScaleFactor;
    float unscaledTop    = scaledTop / mImageScaleFactor;
    float unscaledRight  = scaledRight / mImageScaleFactor;
    float unscaledBottom = scaledBottom / mImageScaleFactor;


    // Create a new bitmap containing just the cropped part
    Bitmap croppedImageBitmap = Bitmap.createBitmap( mImageBitmap, (int)unscaledLeft, (int)unscaledTop, (int)( unscaledRight - unscaledLeft ), (int)( unscaledBottom - unscaledTop ) );

    return ( croppedImageBitmap );
    }


  ////////// Inner Class(es) //////////

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
      mImageToBlendTargetRect.left  = (int)value;
      mImageToBlendTargetRect.right = mImageToBlendTargetRect.left + mWidth;

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
      mImageToBlendTargetRect.top  = (int)value;
      mImageToBlendTargetRect.bottom = mImageToBlendTargetRect.top + mHeight;

      invalidate();
      }

    }

  }

