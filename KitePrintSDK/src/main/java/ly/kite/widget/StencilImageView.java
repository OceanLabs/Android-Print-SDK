/*****************************************************
 *
 * StencilImageView.java
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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import ly.kite.image.ImageAgent;


///// Class Declaration /////

/*****************************************************
 *
 * This is an image view where a stencil can be applied,
 * acting as a window.
 *
 *****************************************************/
public class StencilImageView extends ImageView
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                         = "StencilImageView";

  private static final long    CHECK_ANIMATION_DURATION_MILLIS = 200L;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Bitmap     mOriginalImageBitmap;
  private Drawable   mStencilDrawable;

  private int        mViewWidth;
  private int        mViewHeight;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public StencilImageView( Context context )
    {
    super( context );
    }

  public StencilImageView( Context context, AttributeSet attrs )
    {
    super( context, attrs );
    }

  public StencilImageView( Context context, AttributeSet attrs, int defStyleAttr )
    {
    super( context, attrs, defStyleAttr );
    }

  @TargetApi( Build.VERSION_CODES.LOLLIPOP )
  public StencilImageView( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes )
    {
    super( context, attrs, defStyleAttr, defStyleRes );
    }


  ////////// ImageView Method(s) //////////

  /*****************************************************
   *
   * Sets the image bitmap.
   *
   *****************************************************/
  @Override
  public void setImageBitmap( Bitmap bitmap )
    {
    mOriginalImageBitmap = bitmap;

    createStencilledImage();
    }


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

    createStencilledImage();
    }


  /*****************************************************
   *
   * Draws the image view.
   *
   *****************************************************/
//  @Override
//  public void onDraw( Canvas canvas )
//    {
//    Drawable imageDrawable = getDrawable();
//
//    imageDrawable.draw( canvas );
//    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets the stencil window.
   *
   *****************************************************/
  public void setStencil( Drawable stencilDrawable )
    {
    mStencilDrawable = stencilDrawable;

    createStencilledImage();
    }


  /*****************************************************
   *
   * Sets the stencil window.
   *
   *****************************************************/
  public void setStencil( int stencilResourceId )
    {
    setStencil( getResources().getDrawable( stencilResourceId ) );
    }


  /*****************************************************
   *
   * Creates the bitmap to be drawn.
   *
   *****************************************************/
  private void createStencilledImage()
    {
    // If we don't have everything we need - just draw the
    // original image.

    if ( mViewWidth <= 0 || mViewHeight <= 0 ||
         mStencilDrawable     == null ||
         mOriginalImageBitmap == null )
      {
      super.setImageBitmap( mOriginalImageBitmap );

      return;
      }


    // Create a bitmap and canvas for drawing the stencilled image

    Bitmap blendedBitmap = Bitmap.createBitmap( mViewWidth, mViewHeight, Bitmap.Config.ARGB_8888 );
    Canvas blendCanvas   = new Canvas( blendedBitmap );

    blendCanvas.drawColor( 0x00000000 );


    // Draw the stencil to the canvas
    mStencilDrawable.setBounds( 0, 0, mViewWidth, mViewHeight );
    mStencilDrawable.draw( blendCanvas );


    // Draw the bitmap on the canvas using the stencil alpha. Make sure the bitmap maintains its aspect
    // ratio, but is cropped if necessary.

    Paint paint = new Paint();
    paint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.SRC_ATOP ) );

    Rect sourceRect = ImageAgent.getCropRectangle( mOriginalImageBitmap.getWidth(), mOriginalImageBitmap.getHeight(), (float)mViewWidth / (float)mViewHeight );
    Rect targetRect = new Rect( 0, 0, mViewWidth, mViewHeight );

    blendCanvas.drawBitmap( mOriginalImageBitmap, sourceRect, targetRect, paint );

    super.setImageBitmap( blendedBitmap );
    }


  ////////// Inner Class(es) //////////

  }

