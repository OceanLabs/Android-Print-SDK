/*****************************************************
 *
 * VerticallyScalingImageView.java
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
 * This class extends an ImageView to implement an image view
 * that scales vertically to accommodate an image, whilst maintaining
 * its aspect ratio.
 *
 *****************************************************/

///// Package Declaration /////

package ly.kite.widget;


///// Import(s) /////

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;


///// Class Declaration /////

public class VerticallyScalingImageView extends ImageView
  {
  ////////// Static Constant(s) //////////

  private static final String  LOG_TAG              = "VerticallyScalingImageView";
  private static final boolean DEBUGGING_IS_ENABLED = false;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public VerticallyScalingImageView( Context context )
    {
    super( context );
    }

  public VerticallyScalingImageView( Context context, AttributeSet attrs )
    {
    super( context, attrs );
    }

  public VerticallyScalingImageView( Context context, AttributeSet attrs, int defStyle )
    {
    super( context, attrs, defStyle );
    }

  ////////// ImageView Method(s) //////////

  /*****************************************************
   *
   * Sets the image drawable.
   *
   *****************************************************/
  @Override
  public void setImageBitmap( Bitmap bitmap )
    {
    super.setImageBitmap( bitmap );

    // Get the bitmap dimensions
    float widthAsFloat  = bitmap.getWidth();
    float heightAsFloat = bitmap.getHeight();

    // Determine the scaled width of the bitmap
    float scaledWidthAsFloat = getMeasuredWidth();


    // Calculate the scaled height to keep the aspect ratio

    if ( widthAsFloat < 1.0f ) widthAsFloat = 1.0f;

    float scaledHeightAsFloat = ( scaledWidthAsFloat / widthAsFloat ) * heightAsFloat;


    // Now set the layout height to match the scaled height

    ViewGroup.LayoutParams layoutParams = getLayoutParams();

    layoutParams.height = (int)scaledHeightAsFloat;

    setLayoutParams( layoutParams );

    //forceLayout();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

