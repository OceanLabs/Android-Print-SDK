/*****************************************************
 *
 * BellInterpolator.java
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

package ly.kite.animation;


///// Import(s) /////

import android.util.Log;
import android.view.animation.Interpolator;


///// Class Declaration /////

/*****************************************************
 *
 * This class is an interpolator that exhibits a bump or
 * dip, in the shape of a bell curve, around 0.5.
 *
 *****************************************************/
public class BellInterpolator implements Interpolator
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG             = "BellInterpolator";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private float    mBaseValue;
  private double   mConstantMultiplier;
  private double   mExponentMultiplier;
  //private boolean  mShowDebugOutput;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public BellInterpolator( float baseValue, float midValue, boolean showDebugOutput )
    {
    mBaseValue          = baseValue;
    mConstantMultiplier = midValue;
    mExponentMultiplier = 4f * Math.log( baseValue / midValue );
    //mShowDebugOutput    = showDebugOutput;
    }


  public BellInterpolator( float baseValue, float midValue )
    {
    this( baseValue, midValue, false );
    }


  ////////// Interpolator Method(s) //////////

  /*****************************************************
   *
   * Return the interpolated value.
   *
   *****************************************************/
  @Override
  public float getInterpolation( float input )
    {
    float xMinusHalf = input - 0.5f;

    float interpolation = (float)( mConstantMultiplier * Math.exp( mExponentMultiplier * ( xMinusHalf * xMinusHalf ) ) );

    //if ( mShowDebugOutput ) Log.d( TAG, "f(" + input + ") = " + interpolation );

    return ( interpolation );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

