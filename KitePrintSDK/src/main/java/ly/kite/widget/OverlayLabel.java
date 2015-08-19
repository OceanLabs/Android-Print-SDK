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
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
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
public class OverlayLabel extends TextView
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                       = "OverlayLabel";

  private static final int     DEFAULT_LABEL_COLOUR          = 0xff000000;  // Black
  private static final float   DEFAULT_ROUNDED_CORNER_RADIUS = 3f;

  private static final int     DROP_SHADOW_COLOUR            = 0xff000000;  // Black


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private float  mCornerRadius;

  private RectF  mSolidRect;
  private Paint  mSolidPaint;

  private float  mShadowBlurRadius;
  private float  mShadowYOffset;
  private RectF  mShadowRect;
  private Paint  mShadowPaint;

  private int    mNormalPaddingLeft;
  private int    mNormalPaddingTop;
  private int    mNormalPaddingRight;
  private int    mNormalPaddingBottom;


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

    mSolidPaint.setColor( colour );
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

    updateDimensions();
    }


  /*****************************************************
   *
   * Called to draw the view.
   *
   *****************************************************/
  @Override
  protected void onDraw( Canvas canvas)
    {
    // Draw the background shadow
    canvas.drawRoundRect( mShadowRect, mCornerRadius, mCornerRadius, mShadowPaint );

    // Draw the solid background
    canvas.drawRoundRect( mSolidRect, mCornerRadius, mCornerRadius, mSolidPaint );

    // Get the the base class to draw the text
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
    mCornerRadius = DEFAULT_ROUNDED_CORNER_RADIUS;

    mSolidPaint = new Paint();
    mSolidPaint.setColor( DEFAULT_LABEL_COLOUR );
    mSolidPaint.setAntiAlias( true );

    mShadowPaint = new Paint();
    mShadowPaint.setColor( DROP_SHADOW_COLOUR );
    mShadowPaint.setAntiAlias( true );
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

    updateDimensions();
    }


  /*****************************************************
   *
   * Sets up anything that depends on dimensions.
   *
   *****************************************************/
  private void updateDimensions()
    {
    mSolidRect  = new RectF( mShadowBlurRadius, mShadowBlurRadius,               getWidth() - mShadowBlurRadius, getHeight() - mShadowBlurRadius - mShadowYOffset );
    mShadowRect = new RectF( mSolidRect.left,   mSolidRect.top + mShadowYOffset, mSolidRect.right,               mSolidRect.bottom + mShadowYOffset );

    mShadowPaint.setMaskFilter( new BlurMaskFilter( mShadowBlurRadius, BlurMaskFilter.Blur.NORMAL ) );

    // We don't adjust the padding, so it needs to be set manually to center any text.

    // We also need to adjust the padding to take account the shifted solid rectangle
//    super.setPadding(
//            (int)( mNormalPaddingLeft   + mShadowBlurRadius ),
//            (int)( mNormalPaddingTop    + mShadowBlurRadius ),
//            (int)( mNormalPaddingRight  + mShadowBlurRadius ),
//            (int)( mNormalPaddingBottom + mShadowBlurRadius + mShadowYOffset ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }
