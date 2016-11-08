/*****************************************************
 *
 * AREImageView.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2016 Kite Tech Ltd. https://www.kite.ly
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
import android.widget.FrameLayout;
import android.widget.ImageView;


///// Class Declaration /////

/*****************************************************
 *
 * This class is an aspect ratio enforced frame layout.
 *
 *****************************************************/
public class AREImageView extends ImageView
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "AREImageView";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private AspectRatioEnforcer  mAspectRatioEnforcer;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public AREImageView( Context context )
    {
    super( context );

    initialise( context, null, 0 );
    }

  public AREImageView( Context context, AttributeSet attrs )
    {
    super( context, attrs );

    initialise( context, attrs, 0 );
    }

  public AREImageView( Context context, AttributeSet attrs, int defStyleAttr )
    {
    super( context, attrs, defStyleAttr );

    initialise( context, attrs, defStyleAttr );
    }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public AREImageView( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes )
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
    mAspectRatioEnforcer.onMeasure( this, widthMeasureSpec, heightMeasureSpec );

    super.onMeasure( mAspectRatioEnforcer.getWidthMeasureSpec(), mAspectRatioEnforcer.getHeightMeasureSpec() );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Initialises this view.
   *
   *****************************************************/
  private void initialise( Context context, AttributeSet attrs, int defStyleAttr )
    {
    mAspectRatioEnforcer = new AspectRatioEnforcer( context, attrs, defStyleAttr );
    }


  /*****************************************************
   *
   * Sets the aspect ratio.
   *
   *****************************************************/
  public void setAspectRatio( float aspectRatio )
    {
    mAspectRatioEnforcer.setAspectRatio( aspectRatio );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

