/*****************************************************
 *
 * AnchorableImageView.java
 *
 *
 * Copyright (c) 2013, JL
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Neither the name(s) of the copyright holder(s) nor the name(s) of its
 *       contributor(s) may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER(S) AND CONTRIBUTOR(S) "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER(S) OR CONTRIBUTOR(S) BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * This view displays an image anchored to its top.
 *
 *****************************************************/

///// Package Declaration /////

package ly.kite.widget;


///// Import(s) /////

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;


///// Class Declaration /////

public class AnchorableImageView extends ImageView
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String LOG_TAG = "AnchorableImageView";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private int      mAnchorGravity;
  private float    mAnchorPoint;

  private boolean  mAnchored;

  private int      mLeft;
  private int      mTop;
  private int      mRight;
  private int      mBottom;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public AnchorableImageView( Context context )
    {
    super( context );
    }

  public AnchorableImageView( Context context, AttributeSet attrs )
    {
    super( context, attrs );
    }

  public AnchorableImageView( Context context, AttributeSet attrs, int defStyle )
    {
    super( context, attrs, defStyle );
    }


  ////////// ImageView Method(s) //////////

  /*****************************************************
   *
   * Sets the drawable.
   *
   *****************************************************/
  @Override
  public void setImageDrawable( Drawable drawable )
    {
    super.setImageDrawable( drawable );

    if ( mAnchored ) calculateDrawableBounds( getWidth(), getHeight() );
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

    if ( mAnchored ) calculateDrawableBounds( width, height );
    }


  /*****************************************************
   *
   * Draws the image view.
   *
   *****************************************************/
  @Override
  public void onDraw( Canvas canvas )
    {
    if ( mAnchored )
      {
      // Get the image

      Drawable drawable = getDrawable();

      if ( drawable == null ) return;


      // Draw the image onto the canvas

      drawable.setBounds( mLeft, mTop, mRight, mBottom );

      drawable.draw( canvas );
      }
    else
      {
      super.onDraw( canvas );
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets the anchor gravity.
   *
   *****************************************************/
  public void setAnchorGravity( int gravity )
    {
    mAnchorGravity = gravity;


    if ( gravity == Gravity.LEFT || gravity == Gravity.TOP || gravity == Gravity.RIGHT || gravity == Gravity.BOTTOM )
      {
      mAnchored = true;

      calculateDrawableBounds( getWidth(), getHeight() );
      }
    else
      {
      mAnchored = false;
      }


    invalidate();
    }


  /*****************************************************
   *
   * Sets the anchor point. This is the point on the image
   * (as a proportion of the image size) that is fixed to
   * the anchor position (defined by the gravity).
   *
   * Has no effect if the anchor gravity is not set.
   *
   *****************************************************/
  public void setAnchorPoint( float point )
    {
    mAnchorPoint = point;

    if ( mAnchored ) calculateDrawableBounds( getWidth(), getHeight() );

    invalidate();
    }


  /*****************************************************
   *
   * Calculates the drawable bounds.
   *
   * TODO: Factor in the anchor point
   *
   *****************************************************/
  private void calculateDrawableBounds( int viewWidth, int viewHeight )
    {
    // Make sure we have all the details we need

    if ( viewWidth < 1 || viewHeight < 1 ) return;


    Drawable drawable = getDrawable();

    if ( drawable == null ) return;


    int drawableWidth  = drawable.getIntrinsicWidth();
    int drawableHeight = drawable.getIntrinsicHeight();

    if ( drawableWidth < 1 || drawableHeight < 1 ) return;


    int viewHalfWidth  = (int)( viewWidth  * 0.5f );
    int viewHalfHeight = (int)( viewHeight * 0.5f );


    // Since we are being anchored, we have the option of filling the entire view, or
    // fitting inside it. Work out which from the scale type.

    ScaleType scaleType = getScaleType();

    boolean fillView = ( scaleType == ScaleType.CENTER_CROP    ||
                         scaleType == ScaleType.CENTER.FIT_END ||
                         scaleType == ScaleType.FIT_XY         );


    // Calculate the scale factor according to whether we're filling the view or not

    float widthScaleFactor  = (float)viewWidth  / (float)drawableWidth;
    float heightScaleFactor = (float)viewHeight / (float)drawableHeight;

    float scaleFactor;

    if ( fillView )
      {
      scaleFactor = Math.max( widthScaleFactor, heightScaleFactor );
      }
    else
      {
      scaleFactor = Math.min( widthScaleFactor, heightScaleFactor );
      }


    int scaledDrawableWidth  = (int)( drawableWidth  * scaleFactor );
    int scaledDrawableHeight = (int)( drawableHeight * scaleFactor );

    int scaledDrawableHalfWidth  = (int)( scaledDrawableWidth  * 0.5f );
    int scaledDrawableHalfHeight = (int)( scaledDrawableHeight * 0.5f );


    // Once we've determined what the anchor gravity is, calculate the bounds
    // of the image to match.

    if ( mAnchorGravity == Gravity.LEFT )
      {
      mLeft   = 0;
      mTop    = viewHalfHeight - scaledDrawableHalfHeight;
      mRight  = scaledDrawableWidth;
      mBottom = viewHalfHeight + scaledDrawableHalfHeight;
      }

    else if ( mAnchorGravity == Gravity.TOP )
      {
      mLeft   = viewHalfWidth - scaledDrawableHalfWidth;
      mTop    = 0;
      mRight  = viewHalfWidth + scaledDrawableHalfWidth;
      mBottom = scaledDrawableHeight;
      }

    else if ( mAnchorGravity == Gravity.RIGHT )
      {
      mLeft   = viewWidth - scaledDrawableWidth;
      mTop    = viewHalfHeight - scaledDrawableHalfHeight;
      mRight  = viewWidth;
      mBottom = viewHalfHeight + scaledDrawableHalfHeight;
      }

    else if ( mAnchorGravity == Gravity.BOTTOM )
      {
      mLeft   = viewHalfWidth - scaledDrawableHalfWidth;
      mTop    = viewHeight - scaledDrawableHeight;
      mRight  = viewHalfWidth + scaledDrawableHalfWidth;
      mBottom = viewHeight;
      }
    }


  ////////// Inner Class(es) //////////

  }
