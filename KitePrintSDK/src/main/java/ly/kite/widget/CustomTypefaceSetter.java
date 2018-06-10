/*****************************************************
 *
 * CustomTypefaceSetter.java
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


///// Class Declaration /////

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import ly.kite.R;

/*****************************************************
 *
 * This class sets a custom typeface for components.
 *
 *****************************************************/
public class CustomTypefaceSetter
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "CustomTypefaceSetter";

  static public  final String  CUSTOM_TYPEFACE_STYLE_PREFIX = "custom_typeface_style_";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Sets the typeface for a component.
   *
   *****************************************************/
  static public void setTypeface( Context context, TextView textView, AttributeSet attributeSet, int defaultStyle )
    {
    if ( textView == null ) return;


    // A custom typeface is set according to the following priority order:
    //   1. The "customTypefaceAssetName" attribute
    //   2. The "customTypefaceStyle" attribute ( -> "custom_typeface_style_x" )
    //   3. The R.string.custom_typeface_file_name string


    Resources resources = context.getResources();

    Typeface customTypeface = null;


    if ( attributeSet != null )
      {
      TypedArray typedArray = context.obtainStyledAttributes( attributeSet, R.styleable.CustomTypefaceWidget, defaultStyle, defaultStyle );

      TypedValue value = new TypedValue();

      // 1
      customTypeface = loadTypeface( context, typedArray.getString( R.styleable.CustomTypefaceWidget_customTypefaceAssetName  ) );

      if ( customTypeface == null )
        {
        // 2

        String customTypefaceStyle = typedArray.getString( R.styleable.CustomTypefaceWidget_customTypefaceStyle );

        if ( customTypefaceStyle != null && ! customTypefaceStyle.trim().equals( "" ) )
          {
          String customTypefaceResourceName = CUSTOM_TYPEFACE_STYLE_PREFIX + customTypefaceStyle;

          int customTypefaceAssetNameResourceId = resources.getIdentifier( customTypefaceResourceName, "string", context.getPackageName() );

          if ( customTypefaceAssetNameResourceId != 0 )
            {
            customTypeface = loadTypeface( context, resources.getString( customTypefaceAssetNameResourceId ) );
            }
          }
        }

      typedArray.recycle();
      }


    // 3
    if ( customTypeface == null )
      {
      customTypeface = loadTypeface( context, resources.getString( R.string.kitesdk_custom_typeface_file_name) );
      }



    // If we found a custom typeface - try to use it, but make sure we keep any
    // current style.

    if ( customTypeface != null )
      {
      Typeface currentTypeface = textView.getTypeface();

      if ( currentTypeface != null )
        {
        int style = currentTypeface.getStyle();

        textView.setTypeface( customTypeface, style );
        }
      else
        {
        textView.setTypeface( customTypeface );
        }
      }

    }


  /*****************************************************
   *
   * Tries to load a typeface asset.
   *
   *****************************************************/
  static private Typeface loadTypeface( Context context, String typefaceAssetName )
    {
    if ( typefaceAssetName == null || typefaceAssetName.trim().equals( "" ) ) return ( null );

    return ( TypefaceCache.getTypeface( context, typefaceAssetName ) );
    }


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

