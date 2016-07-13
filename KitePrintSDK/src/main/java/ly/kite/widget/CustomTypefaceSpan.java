/*****************************************************
 *
 * CustomTypefaceSpan.java
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


///// Class Declaration /////

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/*****************************************************
 *
 * This class applies a custom typeface to a text span.
 *
 *****************************************************/
public class CustomTypefaceSpan extends MetricAffectingSpan
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "CustomTypefaceSpan";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Typeface  mTypeface;
  private float     mTextSize;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public CustomTypefaceSpan( Context context, String typefaceAssetName, float textSize )
    {
    if ( typefaceAssetName == null || typefaceAssetName.trim().equals( "" ) )
      {
      throw ( new IllegalArgumentException( "No typeface asset name supplied: " + typefaceAssetName ) );
      }

    mTypeface = TypefaceCache.getTypeface( context, typefaceAssetName );
    mTextSize = textSize;
    }


  public CustomTypefaceSpan( Context context, String typefaceAssetName )
    {
    this( context, typefaceAssetName, 0f );
    }


  ////////// MetricAffectingSpan Method(s) //////////

  @Override
  public void updateDrawState( TextPaint textPaint )
    {
    setTypeface( textPaint );
    }

  @Override
  public void updateMeasureState( TextPaint textPaint )
    {
    setTypeface( textPaint );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Applies the typeface to the paint.
   *
   *****************************************************/
  private void setTypeface( Paint paint )
    {
    int previousStyle;

    Typeface previousTypeface = paint.getTypeface();

    if ( previousTypeface == null ) previousStyle = 0;
    else                            previousStyle = previousTypeface.getStyle();

    Typeface newTypeface = Typeface.create( mTypeface, previousStyle );

    int fake = previousStyle & ~newTypeface.getStyle();

    if ( ( fake & Typeface.BOLD ) != 0 )
      {
      paint.setFakeBoldText( true );
      }

    if ( ( fake & Typeface.ITALIC ) != 0 )
      {
      paint.setTextSkewX( -0.25f );
      }

    paint.setTypeface( newTypeface );

    if ( mTextSize >= 1f ) paint.setTextSize( mTextSize );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }
