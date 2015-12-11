/*****************************************************
 *
 * OverlayLabel.java
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
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import ly.kite.R;


///// Class Declaration /////

/*****************************************************
 *
 * This class is a widget that displays a product item -
 * either a group or product. It allows images to be
 * supplied at a later stage, fading them in where
 * appropriate.
 *
 *****************************************************/
public class OverlayLabel extends FrameLayout
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                       = "OverlayLabel";

  private static final int     DEFAULT_LABEL_COLOUR          = 0xff000000;  // Black
  private static final float   DEFAULT_ROUNDED_CORNER_RADIUS = 3f;

  private static final int     DROP_SHADOW_COLOUR            = 0xff000000;  // Black


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Drawable  mBackgroundOverlayDrawable;
  private Bitmap    mBackgroundOverlayBitmap;

  private TextView  mLabelTextView;
  private float     mCornerRadius;

  private Rect      mBackgroundOverlayBitmapRect;
  private RectF     mSolidRect;
  private Paint     mSolidPaint;


  private boolean   mShowLabelShadow;

  private float     mShadowBlurRadius;
  private float     mShadowYOffset;
  private RectF     mShadowRect;
  private Paint     mShadowPaint;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public OverlayLabel( Context context )
    {
    super( context );

    initialise( context, null, 0 );
    }

  public OverlayLabel( Context context, AttributeSet attrs )
    {
    super( context, attrs );

    initialise( context, attrs, 0 );
    }

  public OverlayLabel( Context context, AttributeSet attrs, int defStyleAttr )
    {
    super( context, attrs, defStyleAttr );

    initialise( context, attrs, defStyleAttr );
    }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public OverlayLabel( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes )
    {
    super( context, attrs, defStyleAttr, defStyleRes );

    initialise( context, attrs, defStyleAttr );
    }


  ////////// View Method(s) //////////

  /*****************************************************
   *
   * Called to set the background colour.
   *
   *****************************************************/
  @Override
  public void setBackgroundColor( int colour )
    {
    // We intercept this and set the paint colour. The base text view
    // doesn't draw the background; we draw it ourselves.

    int alpha = mSolidPaint.getAlpha();

    mSolidPaint.setColor( colour );
    mSolidPaint.setAlpha( alpha );  // Restore the original alpha
    }


  /*****************************************************
   *
   * Called when the size changes.
   *
   *****************************************************/
  @Override
  public void onSizeChanged( int width, int height, int oldWidth, int oldHeight )
    {
    super.onSizeChanged( width, height, oldWidth, oldHeight );

    prepare();
    }


  /*****************************************************
   *
   * Called to draw the view.
   *
   *****************************************************/
  @Override
  protected void onDraw( Canvas canvas )
    {
    // Draw the background shadow
    if ( mShowLabelShadow ) canvas.drawRoundRect( mShadowRect, mCornerRadius, mCornerRadius, mShadowPaint );

    // Draw the solid background
    canvas.drawRoundRect( mSolidRect, mCornerRadius, mCornerRadius, mSolidPaint );

    // Draw any overlay
    if ( mBackgroundOverlayBitmap != null )
      {
      canvas.drawBitmap( mBackgroundOverlayBitmap, mBackgroundOverlayBitmapRect, mSolidRect, null );
      }

    // Draw the children
    super.onDraw( canvas );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Initialises this custom view.
   *
   *****************************************************/
  private void initialise( Context context, AttributeSet attributeSet, int defaultStyle )
    {
    // Inflate the layout and get any view references

    LayoutInflater layoutInflator = LayoutInflater.from( context );

    View view = layoutInflator.inflate( R.layout.overlay_label, this, true );

    mLabelTextView = (TextView)view.findViewById( R.id.label_text_view );


    mCornerRadius = DEFAULT_ROUNDED_CORNER_RADIUS;

    mSolidPaint = new Paint();
    mSolidPaint.setColor( DEFAULT_LABEL_COLOUR );
    mSolidPaint.setAntiAlias( true );

    mShadowPaint = new Paint();
    mShadowPaint.setColor( DROP_SHADOW_COLOUR );
    mShadowPaint.setAntiAlias( true );


    mShowLabelShadow = true;


    // Check the XML attributes

    if ( attributeSet != null )
      {
      TypedArray typedArray = context.obtainStyledAttributes( attributeSet, R.styleable.OverlayLabel, defaultStyle, defaultStyle );

      // See if there is a background overlay drawable
      mBackgroundOverlayDrawable = typedArray.getDrawable( R.styleable.OverlayLabel_backgroundOverlayDrawable );

      typedArray.recycle();
      }


    // Frames don't normally draw anything other than their children, so we need to let the parent
    // know that we'll be drawing a background.
    setWillNotDraw( false );
    }


  /*****************************************************
   *
   * Sets the text.
   *
   *****************************************************/
  public void setText( String text )
    {
    if ( mLabelTextView != null ) mLabelTextView.setText( text );
    }


  /*****************************************************
   *
   * Sets the corner radius.
   *
   *****************************************************/
  public void setCornerRadius( float radius )
    {
    mCornerRadius = radius;
    }


  /*****************************************************
   *
   * Sets the background drop shadow parameters. Currently
   * only a Y offset is supported.
   *
   *****************************************************/
  public void setBackgroundShadow( int colour, float blurRadius, float yOffset )
    {
    mShadowPaint.setColor( colour );

    mShadowBlurRadius = blurRadius;
    mShadowYOffset    = yOffset;

    prepare();
    }


  /*****************************************************
   *
   * Sets the label opacity, between 0.0f and 1.0f.
   *
   *****************************************************/
  public void setLabelOpacity( float opacity )
    {
    mSolidPaint.setAlpha( (int)( opacity * 255f ) );
    }


  /*****************************************************
   *
   * Sets whether the label drop shadow is hidden.
   *
   *****************************************************/
  public void setHideLabelShadow( boolean hideLabelShadow )
    {
    mShowLabelShadow = ! hideLabelShadow;
    }


  /*****************************************************
   *
   * Sets up anything that depends on dimensions.
   *
   *****************************************************/
  private void prepare()
    {
    int viewWidth  = getWidth();
    int viewHeight = getHeight();

    int solidRectWidth  = (int)( viewWidth - mShadowBlurRadius );
    int solidRectHeight = (int)( viewHeight - mShadowBlurRadius - mShadowYOffset );

    mSolidRect  = new RectF( mShadowBlurRadius, mShadowBlurRadius,               solidRectWidth,   solidRectHeight );
    mShadowRect = new RectF( mSolidRect.left,   mSolidRect.top + mShadowYOffset, mSolidRect.right, mSolidRect.bottom + mShadowYOffset );

    mShadowPaint.setMaskFilter( new BlurMaskFilter( mShadowBlurRadius, BlurMaskFilter.Blur.NORMAL ) );


    if ( mBackgroundOverlayDrawable != null && solidRectWidth > 0 && solidRectHeight > 0 )
      {
      // In order to draw the overlay drawable matching the same shape as the solid rectangle, we need
      // to prepare a bitmap, combining the rectangle amd the drawable.

      // Convert the background overlay drawable into a bitmap

      Bitmap drawableBitmap = Bitmap.createBitmap( solidRectWidth, solidRectHeight, Bitmap.Config.ARGB_8888 );
      Canvas drawableCanvas = new Canvas( drawableBitmap );

      mBackgroundOverlayDrawable.setBounds( 0, 0, solidRectWidth, solidRectHeight );
      mBackgroundOverlayDrawable.draw( drawableCanvas );


      // Create the overlay bitmap, and a temporary canvas to draw into it

      mBackgroundOverlayBitmap = Bitmap.createBitmap( viewWidth, viewHeight, Bitmap.Config.ARGB_8888 );

      Canvas backgroundOverlayCanvas = new Canvas( mBackgroundOverlayBitmap );


      // Draw the solid rectangle shape on the overlay canvas to act as a mask

      mBackgroundOverlayBitmapRect = new Rect( 0, 0, solidRectWidth, solidRectHeight );

      RectF bitmapRectF = new RectF( 0f, 0f, solidRectWidth, solidRectHeight );

      Paint paint = new Paint();
      paint.setColor( 0xffffffff );
      paint.setAntiAlias( true );

      backgroundOverlayCanvas.drawRoundRect( bitmapRectF, mCornerRadius, mCornerRadius, paint );


      // Draw the drawable bitmap onto the overlay canvas, clipping to the rounded rectangle

      paint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.DST_ATOP ) );

      backgroundOverlayCanvas.drawBitmap( drawableBitmap, mBackgroundOverlayBitmapRect, mBackgroundOverlayBitmapRect, paint );
      }



    // We don't adjust the padding - it needs to be set manually to vertically center any text.
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }
