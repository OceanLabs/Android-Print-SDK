/*****************************************************
 *
 * FramedImageView.java
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
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import ly.kite.R;
import ly.kite.catalogue.Border;


///// Class Declaration /////

/*****************************************************
 *
 * This class overlays a frame on a standard image view.
 *
 *****************************************************/
public class FramedImageView extends AAREImageContainerFrame
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                         = "FramedImageView";

  private static final long    CHECK_ANIMATION_DURATION_MILLIS = 200L;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private StencilImageView  mStencilImageView;

  private Object            mExpectedKey;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public FramedImageView( Context context )
    {
    super( context );
    }

  public FramedImageView( Context context, AttributeSet attrs )
    {
    super( context, attrs );
    }

  public FramedImageView( Context context, AttributeSet attrs, int defStyleAttr )
    {
    super( context, attrs, defStyleAttr );
    }

  @TargetApi( Build.VERSION_CODES.LOLLIPOP )
  public FramedImageView( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes )
    {
    super( context, attrs, defStyleAttr, defStyleRes );
    }


  ////////// AFixableImageFrame Method(s) //////////

  /*****************************************************
   *
   * Returns the content view.
   *
   *****************************************************/
  @Override
  protected View onCreateView( Context context, AttributeSet attributeSet, int defaultStyle )
    {
    LayoutInflater layoutInflator = LayoutInflater.from( context );

    View view = layoutInflator.inflate( R.layout.framed_image_view, this, true );

    mStencilImageView = (StencilImageView)view.findViewById( R.id.image_view );


    return ( view );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets a frame border around the image by setting the
   * padding.
   *
   *****************************************************/
  public void setBorder( int width )
    {
    setPadding( width, width, width, width );
    }


  /*****************************************************
   *
   * Sets a frame border around the image by setting the
   * padding.
   *
   *****************************************************/
  public void setBorder( int left, int top, int right, int bottom )
    {
    setPadding( left, top, right, bottom );
    }


  /*****************************************************
   *
   * Sets a frame border around the image by setting the
   * padding.
   *
   *****************************************************/
  public void setBorder( Border border )
    {
    setBorder( border.leftPixels, border.topPixels, border.rightPixels, border.bottomPixels );
    }


  /*****************************************************
   *
   * Sets the stencil window.
   *
   *****************************************************/
  public void setStencil( int stencilDrawableResourceId )
    {
    mStencilImageView.setStencil( stencilDrawableResourceId );
    }


  ////////// Inner Class(es) //////////

  }

