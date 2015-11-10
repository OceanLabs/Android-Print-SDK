/*****************************************************
 *
 * AspectRatioEnforcer.java
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

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import ly.kite.KiteSDK;
import ly.kite.R;


///// Class Declaration /////

/*****************************************************
 *
 * This class is used by Views to enforce aspect ratios.
 *
 *****************************************************/
public class AspectRatioEnforcer
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "AspectRatioEnforcer";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private float  mWidthToHeightMultiplier;

  private float  mLeftPaddingProportion;
  private float  mTopPaddingProportion;
  private float  mRightPaddingProportion;
  private float  mBottomPaddingProportion;

  private int    mLastCalculatedWidthMeasureSpec;
  private int    mLastCalculatedHeightMeasureSpec;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public AspectRatioEnforcer( Context context, AttributeSet attributeSet, int defaultStyle )
    {
    // Check the XML attributes

    if ( attributeSet != null )
      {
      TypedArray typedArray = context.obtainStyledAttributes( attributeSet, R.styleable.AspectRatioEnforcer, defaultStyle, defaultStyle );


      // If an aspect ratio was defined in the XML then set it now.
      // ** Otherwise leave it at its uninitialised value **

      TypedValue value = new TypedValue();

      if ( typedArray.getValue( R.styleable.AspectRatioEnforcer_aspectRatio, value ) )
        {
        setAspectRatio( value.getFloat() );
        }


      typedArray.recycle();
      }

    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets the aspect ratio for content.
   *
   *****************************************************/
  public void setAspectRatio( float aspectRatio )
    {
    mWidthToHeightMultiplier = 1.0f / aspectRatio;
    }


  /*****************************************************
   *
   * Sets a frame border around the image as a proportion
   * of the image size (which we won't know until we are
   * measured), by setting the padding.
   *
   *****************************************************/
  public void setPaddingProportions( float leftProportion, float topProportion, float rightProportion, float bottomProportion )
    {
    mLeftPaddingProportion   = leftProportion;
    mTopPaddingProportion    = topProportion;
    mRightPaddingProportion  = rightProportion;
    mBottomPaddingProportion = bottomProportion;
    }


  /*****************************************************
   *
   * Called by the enforcing view from its onMeasure
   * method.
   *
   *****************************************************/
  public void onMeasure( View view, int widthMeasureSpec, int heightMeasureSpec )
    {
    int widthMode = View.MeasureSpec.getMode( widthMeasureSpec );
    int widthSize = View.MeasureSpec.getSize( widthMeasureSpec );


    // We only do this jiggery-pokery with the size if the width is known and we were
    // supplied an aspect ratio.

    if ( ( widthMode == View.MeasureSpec.AT_MOST || widthMode == View.MeasureSpec.EXACTLY ) &&
            mWidthToHeightMultiplier >= KiteSDK.FLOAT_ZERO_THRESHOLD )
      {
      float contentWidth;
      float contentHeight;
      float viewHeight;

      if ( mLeftPaddingProportion   >= KiteSDK.FLOAT_ZERO_THRESHOLD ||
           mTopPaddingProportion    >= KiteSDK.FLOAT_ZERO_THRESHOLD ||
           mRightPaddingProportion  >= KiteSDK.FLOAT_ZERO_THRESHOLD ||
           mBottomPaddingProportion >= KiteSDK.FLOAT_ZERO_THRESHOLD )
        {

        // If padding proportions have been supplied, our calculations are based on the following equations:
        //
        // Available width =   left padding                            + content width +   right padding
        //                 = ( content width * left padding proportion ) + content width + ( content width * right padding proportion )
        //                 =   content width * ( left padding proportion + 1 + right padding proportion )
        //
        // Therefore: content width = available width / ( left padding proportion + 1 + right padding proportion )
        //
        contentWidth = widthSize / ( mLeftPaddingProportion + 1f + mRightPaddingProportion );

        // Now that we have the width, we can calculate the height using the aspect ratio
        contentHeight = contentWidth * mWidthToHeightMultiplier;


        // Finally, calculate and set the padding.

        float leftPadding   = contentWidth  * mLeftPaddingProportion;
        float rightPadding  = contentWidth  * mRightPaddingProportion;
        float topPadding    = contentHeight * mTopPaddingProportion;
        float bottomPadding = contentHeight * mBottomPaddingProportion;

        view.setPadding( (int) leftPadding, (int) topPadding, (int) rightPadding, (int) bottomPadding );

        viewHeight = topPadding + contentHeight + bottomPadding;
        }
      else
        {
        // If no padding proportions have been set, then leave the padding as it is, and just calculate
        // the content dimensions.

        contentWidth  = widthSize - ( view.getPaddingLeft() + view.getPaddingRight() );
        contentHeight = contentWidth * mWidthToHeightMultiplier;

        viewHeight = view.getPaddingTop() + contentHeight + view.getPaddingBottom();
        }


      // Adjust the height measure spec to set the height of the view
      heightMeasureSpec = View.MeasureSpec.makeMeasureSpec( (int) viewHeight, widthMode );
      }


    mLastCalculatedWidthMeasureSpec  = widthMeasureSpec;
    mLastCalculatedHeightMeasureSpec = heightMeasureSpec;
    }


  /*****************************************************
   *
   * Returns the width measure spec calculated in the
   * onMeasure method.
   *
   *****************************************************/
  public int getWidthMeasureSpec()
    {
    return ( mLastCalculatedWidthMeasureSpec );
    }


  /*****************************************************
   *
   * Returns the height measure spec calculated in the
   * onMeasure method.
   *
   *****************************************************/
  public int getHeightMeasureSpec()
    {
    return ( mLastCalculatedHeightMeasureSpec );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

